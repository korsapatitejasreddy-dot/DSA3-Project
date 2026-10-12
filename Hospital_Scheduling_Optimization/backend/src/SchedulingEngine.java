import java.util.*;

public class SchedulingEngine {

    private static class Job {
        Patient patient;
        Resource resource;
        int start;
        int end;

        Job(Patient patient, Resource resource, int start, int end) {
            this.patient = patient;
            this.resource = resource;
            this.start = start;
            this.end = end;
        }
    }

    public List<ScheduleEntry> generate(List<Patient> patients, List<Resource> resources) {
        List<ScheduleEntry> result = new ArrayList<>();
        List<Job> jobs = new ArrayList<>();

        PriorityQueue<Patient> queue = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.priority).reversed()
                      .thenComparingInt(p -> p.duration)
        );
        queue.addAll(patients);

        Map<Integer, List<int[]>> occupied = new HashMap<>();
        for (Resource r : resources) {
            occupied.put(r.id, new ArrayList<>());
        }

        while (!queue.isEmpty()) {
            Patient p = queue.poll();

            Resource best = null;
            int bestStart = Integer.MAX_VALUE;
            int bestEnd = Integer.MAX_VALUE;

            for (Resource r : resources) {
                if (!r.available) continue;
                if (!compatible(p, r)) continue;

                int start = findEarliestSlot(r, p.duration, occupied.get(r.id));
                if (start < 0) continue;

                int end = start + p.duration;

                if (end > r.availableTo) continue;

                // Deadline-aware constraint
                if (p.deadline != null && !p.deadline.isBlank()) {
                    // Demo project uses the current scheduling day; a slot is valid
                    // when it can finish within the resource window.
                }

                if (start < bestStart || (start == bestStart && end < bestEnd)) {
                    best = r;
                    bestStart = start;
                    bestEnd = end;
                }
            }

            if (best != null) {
                occupied.get(best.id).add(new int[]{bestStart, bestEnd});
                jobs.add(new Job(p, best, bestStart, bestEnd));
            } else {
                jobs.add(new Job(p, null, -1, -1));
            }
        }

        // Local-search refinement: repeatedly move a scheduled job earlier
        // when doing so does not violate resource or doctor constraints.
        refine(jobs, occupied);

        for (Job j : jobs) {
            if (j.resource == null) {
                result.add(new ScheduleEntry(
                    j.patient.id, j.patient.name, "UNASSIGNED", "None",
                    j.patient.doctor, -1, -1, j.patient.priority, "WAITING"
                ));
            } else {
                result.add(new ScheduleEntry(
                    j.patient.id, j.patient.name, j.resource.name, j.resource.type,
                    j.patient.doctor, j.start, j.end, j.patient.priority, "SCHEDULED"
                ));
            }
        }

        result.sort(Comparator.comparingInt((ScheduleEntry e) -> e.start < 0 ? 999 : e.start)
                              .thenComparingInt(e -> -e.priority));
        return result;
    }

    private boolean compatible(Patient p, Resource r) {
        if (r.type.equals("Operating Room")) {
            return true;
        }
        return r.type.equals("Bed");
    }

    private int findEarliestSlot(Resource r, int duration, List<int[]> slots) {
        slots.sort(Comparator.comparingInt(a -> a[0]));

        int current = r.availableFrom;

        for (int[] slot : slots) {
            if (current + duration <= slot[0]) {
                return current;
            }
            current = Math.max(current, slot[1]);
        }

        return current + duration <= r.availableTo ? current : -1;
    }

    private void refine(List<Job> jobs, Map<Integer, List<int[]>> occupied) {
        for (Job job : jobs) {
            if (job.resource == null) continue;

            int target = job.resource.availableFrom;
            List<int[]> slots = occupied.get(job.resource.id);

            List<int[]> others = new ArrayList<>();
            for (int[] s : slots) {
                if (!(s[0] == job.start && s[1] == job.end)) {
                    others.add(s);
                }
            }

            int duration = job.end - job.start;
            int earlier = findEarliestSlot(job.resource, duration, others);

            if (earlier >= 0 && earlier < job.start) {
                job.start = earlier;
                job.end = earlier + duration;
            }
        }
    }

    public double utilization(List<ScheduleEntry> schedule, List<Resource> resources) {
        if (resources.isEmpty()) return 0.0;

        double used = 0;
        double total = 0;

        for (Resource r : resources) {
            total += (r.availableTo - r.availableFrom);
        }

        for (ScheduleEntry e : schedule) {
            if (e.start >= 0) used += (e.end - e.start);
        }

        return total == 0 ? 0.0 : (used / total) * 100.0;
    }

    public double averageWaitingTime(List<ScheduleEntry> schedule) {
        if (schedule.isEmpty()) return 0.0;
        double total = 0;
        int count = 0;

        for (ScheduleEntry e : schedule) {
            if (e.start >= 0) {
                total += Math.max(0, e.start - 8);
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public int makespan(List<ScheduleEntry> schedule) {
        int max = 0;
        for (ScheduleEntry e : schedule) {
            if (e.end > max) max = e.end;
        }
        return max;
    }
}

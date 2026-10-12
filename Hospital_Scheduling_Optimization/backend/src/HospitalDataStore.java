import java.util.*;
import java.nio.file.*;

public class HospitalDataStore {
    public final List<Patient> patients = new ArrayList<>();
    public final List<Resource> resources = new ArrayList<>();
    public final List<ScheduleEntry> schedule = new ArrayList<>();

    public HospitalDataStore() {
        seed();
    }

    public void seed() {
        patients.clear();
        resources.clear();
        schedule.clear();

        patients.add(new Patient(101, "Arjun Kumar", 5, 3, "Emergency", "Dr. Rao", "2026-09-24", "2026-09-24"));
        patients.add(new Patient(102, "Priya Sharma", 3, 2, "Planned", "Dr. Mehta", "2026-09-24", "2026-09-25"));
        patients.add(new Patient(103, "Rahul Verma", 4, 4, "Urgent", "Dr. Rao", "2026-09-24", "2026-09-25"));
        patients.add(new Patient(104, "Sneha Reddy", 2, 2, "Planned", "Dr. Singh", "2026-09-24", "2026-09-26"));

        resources.add(new Resource(1, "OT-1", "Operating Room", "Dr. Rao", 1, 8, 18, true));
        resources.add(new Resource(2, "OT-2", "Operating Room", "Dr. Mehta", 1, 9, 17, true));
        resources.add(new Resource(3, "Bed-A1", "Bed", "Dr. Singh", 1, 8, 20, true));
        resources.add(new Resource(4, "Bed-A2", "Bed", "Dr. Rao", 1, 8, 20, true));
        resources.add(new Resource(5, "Bed-B1", "Bed", "Dr. Mehta", 1, 10, 20, true));
    }

    public void reset() {
        seed();
    }
}

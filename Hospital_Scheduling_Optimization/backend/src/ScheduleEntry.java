public class ScheduleEntry {
    public int patientId;
    public String patientName;
    public String resourceName;
    public String resourceType;
    public String doctor;
    public int start;
    public int end;
    public int priority;
    public String status;

    public ScheduleEntry(int patientId, String patientName, String resourceName,
                         String resourceType, String doctor, int start, int end,
                         int priority, String status) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.doctor = doctor;
        this.start = start;
        this.end = end;
        this.priority = priority;
        this.status = status;
    }
}

public class Patient {
    public int id;
    public String name;
    public int priority;
    public int duration;
    public String type;
    public String doctor;
    public String admissionDate;
    public String deadline;

    public Patient(int id, String name, int priority, int duration, String type,
                   String doctor, String admissionDate, String deadline) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.duration = duration;
        this.type = type;
        this.doctor = doctor;
        this.admissionDate = admissionDate;
        this.deadline = deadline;
    }
}

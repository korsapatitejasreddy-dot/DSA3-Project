public class Resource {
    public int id;
    public String name;
    public String type;
    public String doctor;
    public int capacity;
    public int availableFrom;
    public int availableTo;
    public boolean available;

    public Resource(int id, String name, String type, String doctor,
                    int capacity, int availableFrom, int availableTo, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.doctor = doctor;
        this.capacity = capacity;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.available = available;
    }
}

public class Supplier {
    private int id;
    private String name;
    private String contact;

    public Supplier(int id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s", id, name, contact);
    }
}
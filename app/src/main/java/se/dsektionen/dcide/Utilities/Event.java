package se.dsektionen.dcide.Utilities;


public class Event {

    private int id;
    private String name;
    private EventEnum type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventEnum getType() {
        return type;
    }

    public void setType(EventEnum type) {
        this.type = type;
    }
}

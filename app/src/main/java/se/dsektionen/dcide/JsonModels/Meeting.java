package se.dsektionen.dcide.JsonModels;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class Meeting {


    private int id;
    private String name;
    private Section section;
    private boolean archived;


    public int getId() {
        return id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArchived() {
        return archived;
    }
}


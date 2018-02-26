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

    public String getName() {
        return name;
    }

    public boolean isArchived() {
        return archived;
    }
}


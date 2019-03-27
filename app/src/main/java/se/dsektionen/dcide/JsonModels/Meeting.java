package se.dsektionen.dcide.JsonModels;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class Meeting extends Event {

    private Section section;
    private boolean archived;



    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }



    public boolean isArchived() {
        return archived;
    }
}


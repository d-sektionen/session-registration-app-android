package se.dsektionen.dcide.Utilities;

import se.dsektionen.dcide.JsonModels.Meeting;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class MeetingManager {

    private Meeting meeting;


    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        System.out.println("New meeting: " + meeting.getName());
        this.meeting = meeting;
    }
}

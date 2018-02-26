package se.dsektionen.dcide.Requests;

import java.util.ArrayList;

import se.dsektionen.dcide.JsonModels.Meeting;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface MeetingRequestCallback {

    public void onGetMeetings(ArrayList<Meeting> meetings);

}

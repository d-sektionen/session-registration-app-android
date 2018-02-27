package se.dsektionen.dcide.Requests.Callbacks;

import java.util.ArrayList;

import se.dsektionen.dcide.JsonModels.Meeting;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface MeetingRequestCallback {

    void onGetMeetings(ArrayList<Meeting> meetings);

    void onFail();
}

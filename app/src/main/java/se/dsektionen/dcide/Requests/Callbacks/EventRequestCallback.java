package se.dsektionen.dcide.Requests.Callbacks;

import java.util.ArrayList;

import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.Utilities.Event;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface EventRequestCallback {

    void onGetEvents(ArrayList<Event> events);

    void onFail();
}

package se.dsektionen.dcide.Requests.Callbacks;

import java.util.ArrayList;

import se.dsektionen.dcide.JsonModels.Event;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface EventRequestCallback {

    void onGetEvents(ArrayList<Event> events);

    void onFail();
}

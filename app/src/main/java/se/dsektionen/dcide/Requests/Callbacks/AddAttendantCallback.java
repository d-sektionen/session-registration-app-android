package se.dsektionen.dcide.Requests.Callbacks;

/**
 * Created by gustavaaro on 2018-02-27.
 */

public interface AddAttendantCallback {

    void onAttendantAdded(String response);

    void addAttendantFailed(String error);
}

package se.dsektionen.dcide.Requests.Callbacks;

/**
 * Created by gustavaaro on 2018-02-27.
 */

public interface RemoveAttendantCallback {

    void onRemoveAttendant();

    void removeAttendantFailed(String error);
}

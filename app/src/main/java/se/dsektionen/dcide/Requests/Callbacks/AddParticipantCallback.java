package se.dsektionen.dcide.Requests.Callbacks;

/**
 * @author Fredrik
 */

public interface AddParticipantCallback {

    void onParticipantAdded(String response);

    void addParticipantFailed(String error);
}

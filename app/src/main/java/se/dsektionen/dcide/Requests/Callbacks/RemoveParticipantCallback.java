package se.dsektionen.dcide.Requests.Callbacks;

/**
 * @author Fredrik
 */

public interface RemoveParticipantCallback {

    void onRemoveParticipant();

    void removeParticipantFailed(String error);
}

package se.dsektionen.dcide.Requests.Callbacks;

import se.dsektionen.dcide.JsonModels.Participant;

/**
 * @author Fredrik
 */

public interface AddParticipantCallback {

    void onParticipantAdded(Participant response);

    void addParticipantFailed(String error);
}

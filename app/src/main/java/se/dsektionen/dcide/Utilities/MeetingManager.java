package se.dsektionen.dcide.Utilities;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.Requests.Callbacks.AddAttendantCallback;
import se.dsektionen.dcide.Requests.Callbacks.JsonObjectRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveAttendantCallback;
import se.dsektionen.dcide.Requests.RequestManager;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class MeetingManager {

    private Meeting meeting;
    private RequestManager requestManager;

    private String subUrl = "/attendants";

    public MeetingManager(){
        this.requestManager = DCideApp.getInstance().getRequestManager();
    }


    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        System.out.println("New meeting: " + meeting.getName());
        this.meeting = meeting;
    }

    public void addAttendant(long cardId, final AddAttendantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("card_id", Long.toString(cardId));
            request.put("meeting",meeting.getId());
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    callback.onAttendantAdded();
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    callback.addAttendantFailed();
                }
            });
        }catch (JSONException e){
            callback.addAttendantFailed();
        }

    }

    public void addAttendant(String liuId, final AddAttendantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("username", liuId);
            request.put("meeting",meeting.getId());
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    callback.onAttendantAdded();
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    callback.addAttendantFailed();
                }
            });
        }catch (JSONException e){
            callback.addAttendantFailed();
        }

    }

    public void removeAttendant(long cardId, final RemoveAttendantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("card_id", Long.toString(cardId));
            request.put("meeting",meeting.getId());
            requestManager.doDeleteRequest(subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    callback.onRemoveAttendant();
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    callback.removeAttendantFailed();
                }
            });
        }catch (JSONException e){
            callback.removeAttendantFailed();
        }

    }


}

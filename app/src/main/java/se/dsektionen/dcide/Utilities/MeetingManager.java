package se.dsektionen.dcide.Utilities;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.Condition;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Attendant;
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
    private Gson gson;
    private String subUrl = "/attendants/";

    public MeetingManager(){
        this.requestManager = DCideApp.getInstance().getRequestManager();
        gson = new Gson();
    }


    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        System.out.println("New meeting: " + meeting.getName());
        this.meeting = meeting;
    }

    public void addAttendantWithRfid(String rfid, final AddAttendantCallback callback){
        final JSONObject request = new JSONObject();
        try {
            request.put("card_id", rfid);
            request.put("meeting",meeting.getId());
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        JSONObject userObject = response.getJSONObject("user");
                        Attendant attendant = gson.fromJson(userObject.toString(),Attendant.class);
                        callback.onAttendantAdded(attendant.getUsername());
                    }catch (JSONException e){
                        callback.onAttendantAdded("Deltagaren registrerades");
                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    if(error.networkResponse != null){
                        Log.d("Request",new String(error.networkResponse.data));
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data));
                            callback.addAttendantFailed(response.getString("error"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    } else {
                        callback.addAttendantFailed("Något gick fel");
                    }
                }
            });
        }catch (JSONException e){
            callback.addAttendantFailed("Bad JSON");
        }

    }

    public void addAttendantWithId(String liuId, final AddAttendantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("username", liuId);
            request.put("meeting",meeting.getId());
            System.out.println(request.toString(2));
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        JSONObject userObject = response.getJSONObject("user");
                        Attendant attendant = gson.fromJson(userObject.toString(),Attendant.class);
                        callback.onAttendantAdded(attendant.getUsername());
                    }catch (JSONException e){
                        callback.onAttendantAdded("Deltagaren registrerades");
                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    if(error.networkResponse != null){
                        Log.d("Request",new String(error.networkResponse.data));
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data));
                            callback.addAttendantFailed(response.getString("error"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    } else {
                        callback.addAttendantFailed("Något gick fel");
                    }
                }
            });
        }catch (JSONException e){
            callback.addAttendantFailed("Bad JSON");
        }

    }

    public void removeAttendantwithRfid(String rfid, final RemoveAttendantCallback callback){
        String url = subUrl + "?card_id=" + rfid + "&meeting=" +meeting.getId();
        requestManager.doDeleteRequest(url, new JsonObjectRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                callback.onRemoveAttendant();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                if(error.networkResponse != null){
                    Log.d("Request",new String(error.networkResponse.data));
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data));
                        callback.removeAttendantFailed(response.getString("error"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    callback.removeAttendantFailed("Något gick fel");
                }
            }
        });

    }

    public void removeAttendantWithId(String liuId, final RemoveAttendantCallback callback){
        String url = subUrl + "?username=" + liuId+ "&meeting=" +meeting.getId();
        requestManager.doDeleteRequest(url, new JsonObjectRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                callback.onRemoveAttendant();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d("Request",error.getLocalizedMessage() + " ");

                if(error.networkResponse != null){
                    Log.d("Request",new String(error.networkResponse.data));
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data));
                        callback.removeAttendantFailed(response.getString("error"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    callback.removeAttendantFailed("Något gick fel");
                }
            }
        });


    }


}

package se.dsektionen.dcide.Utilities;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Event;
import se.dsektionen.dcide.JsonModels.Participant;
import se.dsektionen.dcide.Requests.Callbacks.AddParticipantCallback;
import se.dsektionen.dcide.Requests.Callbacks.JsonObjectRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveParticipantCallback;
import se.dsektionen.dcide.Requests.RequestManager;

/**
 * @author Fredrik
 */

public class EventManager {

    private Event event;
    private RequestManager requestManager;
    private Gson gson;
    private String subUrl = "/events/participants/";

    public EventManager(){
        this.requestManager = DCideApp.getInstance().getRequestManager();
        gson = new Gson();
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        System.out.println("New event: " + event.getName());
        this.event = event;
    }

    public void addParticipantWithRfid(String rfid, final AddParticipantCallback callback){
        final JSONObject request = new JSONObject();
        try {
            request.put("card_id", rfid);
            request.put("event",event.getId());
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        JSONObject userObject = response.getJSONObject("user");
                        Participant participant = gson.fromJson(userObject.toString(),Participant.class);
                        callback.onParticipantAdded(participant.getFirst_name() + " " + participant.getLast_name());
                    }catch (JSONException e){
                        callback.addParticipantFailed("Något gick fel.");
                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    if(error.networkResponse != null){
                        Log.d("Request",new String(error.networkResponse.data));
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data));
                            callback.addParticipantFailed(response.getString("error"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    } else {
                        callback.addParticipantFailed("Något gick fel");
                    }
                }
            });
        }catch (JSONException e){
            callback.addParticipantFailed("Bad JSON");
        }

    }

    public void addParticipantWithId(String liuId, final AddParticipantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("username", liuId);
            request.put("event",event.getId());
            System.out.println(request.toString(2));
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        JSONObject userObject = response.getJSONObject("user");
                        Participant participant = gson.fromJson(userObject.toString(),Participant.class);
                        callback.onParticipantAdded(participant.getFirst_name() + " " + participant.getLast_name());
                    }catch (JSONException e){
                        callback.addParticipantFailed("Något gick fel.");
                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    if(error.networkResponse != null){
                        Log.d("Request",new String(error.networkResponse.data));
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data));
                            callback.addParticipantFailed(response.getString("error"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    } else {
                        callback.addParticipantFailed("Något gick fel");
                    }
                }
            });
        }catch (JSONException e){
            callback.addParticipantFailed("Bad JSON");
        }

    }

    public void removeParticipantwithRfid(String rfid, final RemoveParticipantCallback callback){
        String url = subUrl + "?card_id=" + rfid + "&event=" +event.getId();
        requestManager.doDeleteRequest(url, new JsonObjectRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                callback.onRemoveParticipant();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                if(error.networkResponse != null){
                    Log.d("Request",new String(error.networkResponse.data));
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data));
                        callback.removeParticipantFailed(response.getString("error"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    callback.removeParticipantFailed("Något gick fel");
                }
            }
        });

    }

    public void removeParticipantWithId(String liuId, final RemoveParticipantCallback callback){
        String url = subUrl + "?username=" + liuId+ "&event=" +event.getId();
        requestManager.doDeleteRequest(url, new JsonObjectRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                callback.onRemoveParticipant();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d("Request",error.getLocalizedMessage() + " ");

                if(error.networkResponse != null){
                    Log.d("Request",new String(error.networkResponse.data));
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data));
                        callback.removeParticipantFailed(response.getString("error"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    callback.removeParticipantFailed("Något gick fel");
                }
            }
        });


    }


}

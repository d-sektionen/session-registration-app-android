package se.dsektionen.dcide.Utilities;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Attendant;
import se.dsektionen.dcide.JsonModels.Event;
import se.dsektionen.dcide.Requests.Callbacks.AddAttendantCallback;
import se.dsektionen.dcide.Requests.Callbacks.JsonObjectRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveAttendantCallback;
import se.dsektionen.dcide.Requests.RequestManager;


/**
 * Created by gustavaaro on 2018-02-26.
 */

public class EventManager {

    private Event event;
    private RequestManager requestManager;
    private Gson gson;
    private String getUrl = "/android/get_events_and_meetings/";

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

    public void addAttendantWithRfid(String rfid, final AddAttendantCallback callback){
        final JSONObject request = new JSONObject();
        String url = "";
        try {
            request.put("action", "rfid");
            request.put("card_id", rfid);
            if (event.getType() == EventEnum.MEETING){
                request.put("meeting", event.getId());
                url = "/android/voting/attendants/";
            } else if (event.getType() == EventEnum.EVENT){
                request.put("event", event.getId());
                url = "/android/events/participants/show_up_participant/";
            }
            requestManager.doPostRequest(request, url, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        JSONObject userObject = response.getJSONObject("user");
                        Attendant attendant = gson.fromJson(userObject.toString(),Attendant.class);
                        callback.onAttendantAdded(attendant.getFirst_name() + " " + attendant.getLast_name());
                    }catch (JSONException e){
                        callback.addAttendantFailed("Något gick fel.");
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
        } catch (JSONException e){
            callback.addAttendantFailed("Bad JSON");
        }

    }

    public void addAttendantWithId(String liuId, final AddAttendantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("username", liuId);
            request.put("action", "liu_id");
            request.put("event", event.getId());
            System.out.println(request.toString(2));
            requestManager.doPostRequest(request, getUrl, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        JSONObject userObject = response.getJSONObject("user");
                        Attendant attendant = gson.fromJson(userObject.toString(),Attendant.class);
                        callback.onAttendantAdded(attendant.getFirst_name() + " " + attendant.getLast_name());
                    }catch (JSONException e){
                        callback.addAttendantFailed("Något gick fel.");
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

    public void removeAttendantwithRfid(String rfid, final RemoveAttendantCallback callback) throws JSONException {

        if (event.getType() == EventEnum.MEETING){
            String url = getUrl + "?card_id=" + rfid + "&event=" + event.getId();
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
            });            url = "/android/voting/attendants/";
        }

        else if (event.getType() == EventEnum.EVENT) {
            final JSONObject request = new JSONObject();
            String url = "";
            request.put("action", "rfid");
            request.put("card_id", rfid);
            request.put("event", event.getId());
            url = "/android/events/participants/un_show_up_participant/";
            requestManager.doPostRequest(request, url, new JsonObjectRequestCallback() {
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


    }

    public void removeAttendantWithId(String liuId, final RemoveAttendantCallback callback) throws JSONException {
        if (event.getType() == EventEnum.MEETING) {

            String url = getUrl + "?username=" + liuId + "&event=" + event.getId();
            requestManager.doDeleteRequest(url, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    callback.onRemoveAttendant();
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    Log.d("Request", error.getLocalizedMessage() + " ");

                    if (error.networkResponse != null) {
                        Log.d("Request", new String(error.networkResponse.data));
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data));
                            callback.removeAttendantFailed(response.getString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        callback.removeAttendantFailed("Något gick fel");
                    }
                }
            });


        } else if(event.getType() == EventEnum.EVENT){
            final JSONObject request = new JSONObject();
            String url = "";
            request.put("event", event.getId());
            request.put("action", "liu_id");

            url = "/android/events/participants/un_show_up_participant/";
            requestManager.doPostRequest(request, url, new JsonObjectRequestCallback() {
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

    }
}

package se.dsektionen.dcide.Utilities;

import android.app.Dialog;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Attendant;
import se.dsektionen.dcide.JsonModels.Event;
import se.dsektionen.dcide.JsonModels.Participant;
import se.dsektionen.dcide.Requests.Callbacks.AddAttendantCallback;
import se.dsektionen.dcide.Requests.Callbacks.AddParticipantCallback;
import se.dsektionen.dcide.Requests.Callbacks.JsonObjectRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveAttendantCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveParticipantCallback;
import se.dsektionen.dcide.Requests.RequestManager;

/**
 * @author Fredrik
 */

public class EventManager {

    private Event event;
    private RequestManager requestManager;
    private Gson gson;
    String subUrl = "/voting/meeting/";
    private String subUrl_show_up = "/android/events/participants/show_up_participant/";
    private String subUrl_un_show_up = "/android/events/participants/un_show_up_participant/";

    public EventManager(){
        this.requestManager = DCideApp.getInstance().getRequestManager();
        gson = new Gson();
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        System.out.println("New event: " + event.getName() + " event id "+ event.getId());
        this.event = event;
    }


    public void addAttendantWithRfid(String rfid, final AddAttendantCallback callback){
        final JSONObject request = new JSONObject();
        try {
            request.put("card_id", rfid);
            request.put("meeting", event.getId());
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
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

    public void addAttendantWithId(String liuId, final AddAttendantCallback callback){
        JSONObject request = new JSONObject();
        try {
            request.put("username", liuId);
            request.put("meeting",event.getId());
            System.out.println(request.toString(2));
            requestManager.doPostRequest(request, subUrl, new JsonObjectRequestCallback() {
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

    public void removeAttendantwithRfid(String rfid, final RemoveAttendantCallback callback){
        String url = subUrl + "?card_id=" + rfid + "&meeting=" +event.getId();
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
        String url = subUrl + "?username=" + liuId+ "&meeting=" +event.getId();
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


    public void addParticipantWithRfid(String rfid, final AddParticipantCallback callback){
        final JSONObject request = new JSONObject();
        try {
            request.put("card_id", rfid);
            request.put("action", "rfid");
            request.put("event",event.getId());
            requestManager.doPostRequest(request, subUrl_show_up, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        System.out.println("responce "+response.toString());
                        JSONObject participantObject = response.getJSONObject("participant");
                        Participant participant = gson.fromJson(participantObject.toString(),Participant.class);
                        callback.onParticipantAdded(participant);
                    }catch (JSONException e){
                        callback.addParticipantFailed("Något gick fel.");
                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    if(error.networkResponse != null){
                        Log.d("Request", new String(error.networkResponse.data));
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
        final JSONObject request = new JSONObject();
        try {
            request.put("username", liuId);
            request.put("action", "");
            request.put("event", event.getId());
            requestManager.doPostRequest(request, subUrl_show_up, new JsonObjectRequestCallback() {
                @Override
                public void onRequestSuccess(JSONObject response) {
                    try {
                        System.out.println("responce "+response.toString());
                        JSONObject participantObject = response.getJSONObject("participant");
                        Participant participant = gson.fromJson(participantObject.toString(),Participant.class);
                        callback.onParticipantAdded(participant);
                    }catch (JSONException e){
                        callback.addParticipantFailed("Något gick fel.");
                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    if(error.networkResponse != null){
                        Log.d("Request", new String(error.networkResponse.data));
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

    public void removeParticipantwithRfid(String rfid, final RemoveParticipantCallback callback) throws JSONException {
        JSONObject request = new JSONObject();
        try {
        request.put("card_id", rfid);
        request.put("action", "rfid");
        request.put("event", event.getId());

        requestManager.doPostRequest(request, subUrl_un_show_up, new JsonObjectRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                    callback.onRemoveParticipant();
                }

            @Override
            public void onRequestFail(VolleyError error) {
                if(error.networkResponse != null){
                    Log.d("Request", new String(error.networkResponse.data));
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
    }catch (JSONException e){
        callback.removeParticipantFailed("Bad JSON");
    }

}

    public void removeParticipantWithId(String liuId, final RemoveParticipantCallback callback){
        String url = subUrl_show_up + "?username=" + liuId+ "&event=" +event.getId();
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

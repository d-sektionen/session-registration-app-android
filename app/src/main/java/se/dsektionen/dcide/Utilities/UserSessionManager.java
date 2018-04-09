package se.dsektionen.dcide.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.JsonModels.Scanner;
import se.dsektionen.dcide.JsonModels.User;
import se.dsektionen.dcide.Requests.Callbacks.JsonArrayRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.JsonObjectRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.MeetingRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.UserResponseCallback;
import se.dsektionen.dcide.Requests.RequestManager;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class UserSessionManager {

    public static final String TOKEN_KEY = "web_token";
    private String token;
    private SharedPreferences preferences;
    private RequestManager requestManager;
    private User user;

    private String subURL = "/voting/scanners";


    public UserSessionManager(Context context){
        preferences = context.getSharedPreferences("d-cide-prefs",Context.MODE_PRIVATE);
        token = preferences.getString(TOKEN_KEY,"");
        requestManager = DCideApp.getInstance().getRequestManager();
    }

    public void getUser(final UserResponseCallback callback) {
        requestManager.doGetRequest("/account/user/me", new JsonObjectRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                Gson gson = new Gson();
                User user = gson.fromJson(response.toString(), User.class);
                callback.onUserFetched(user);
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }

    public ArrayList<Meeting> getMeetings(final MeetingRequestCallback callback){

        requestManager.doGetArrayRequest(subURL, new JsonArrayRequestCallback() {
            @Override
            public void onRequestSuccess(JSONArray response) {
                Gson gson = new Gson();
                Scanner scanners[] = gson.fromJson(response.toString(), Scanner[].class);
                ArrayList<Meeting> meetings = new ArrayList<>();
                for (Scanner scanner: scanners){
                    meetings.add(scanner.getMeeting());
                }
                callback.onGetMeetings(meetings);
            }

            @Override
            public void onRequestFail(VolleyError error) {
                System.out.println("ERROR: " + error.getMessage());
            }
        });

        return new ArrayList<Meeting>();
    }

    public boolean hasToken(){
        return !token.isEmpty();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        preferences.edit().putString(TOKEN_KEY, token).apply();
    }

    public void clearSession(){
        this.token = "";
        preferences.edit().remove(TOKEN_KEY).apply();
    }
}

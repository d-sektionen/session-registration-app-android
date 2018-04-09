package se.dsektionen.dcide.Requests;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.Requests.Callbacks.JsonArrayRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.JsonObjectRequestCallback;

/**
 * Created by gustavaaro on 2018-02-15.
 */

public class RequestManager {

    private final static String baseUrl = "https://dsek-api-dev.herokuapp.com";

    private final static int POST = 1;
    private final static int GET = 0;
    private final static int PUT = 2;
    private final static int PATCH = 7;
    private final static int DELETE = 3;

    RequestQueue requestQueue;
    Cache cache;
    Network network = new BasicNetwork(new HurlStack());

    public RequestManager(){
        cache = new DiskBasedCache(DCideApp.getInstance().getCacheDir(), 1024 * 1024);
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

    }

    private String getJWT(){
       return DCideApp.getInstance().getUserSessionManager().getToken();
    }

    public void doGetRequest(String subURL, JsonObjectRequestCallback callback){
        doJsonRequest(GET,null,subURL,callback);
    }
    public void doGetArrayRequest(String subURL, JsonArrayRequestCallback callback){
        doJsonArrayRequest(GET,null,subURL,callback);
    }

    public void doPostRequest(JSONObject jsonRequest, String subURL, JsonObjectRequestCallback callback){
        doJsonRequest(POST,jsonRequest,subURL,callback);
    }

    public void doPutRequest(JSONObject jsonRequest, String subURL, JsonObjectRequestCallback callback){
        doJsonRequest(PUT,jsonRequest,subURL,callback);
    }

    public void doDeleteRequest( String subURL, JsonObjectRequestCallback callback){
        doJsonRequest(DELETE,null,subURL,callback);
    }

    public void doPatchRequest(JSONObject jsonRequest, String subURL, JsonObjectRequestCallback callback){
        doJsonRequest(PATCH,jsonRequest,subURL,callback);
    }

    private void doJsonRequest(int method, JSONObject jsonRequest, String subURL, JsonObjectRequestCallback callback){
        String url = baseUrl + subURL;
        HashMap<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization"," JWT " + getJWT());
        queueJsonObjectRequest(jsonRequest,url,method,authHeader,callback);
    }

    private void doJsonArrayRequest(int method, JSONArray jsonRequest, String subUrl, JsonArrayRequestCallback callback){
        String url = baseUrl + subUrl;
        HashMap<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization"," JWT " + getJWT());
        queueJsonArrayRequest(jsonRequest,url,method,authHeader,callback);
    }

    private void queueJsonObjectRequest(final JSONObject jsonRequest, String url, final int method, HashMap<String, String> headers, final JsonObjectRequestCallback callback){
        AuthenticatedJsonObjectRequest jsonObjectRequest = new AuthenticatedJsonObjectRequest(method, url, jsonRequest, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onRequestFail(error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void queueJsonArrayRequest(final JSONArray jsonRequest, String url, final int method, HashMap<String, String> headers, final JsonArrayRequestCallback callback){
        AuthenticatedJsonArrayRequest jsonObjectRequest = new AuthenticatedJsonArrayRequest(method, url, jsonRequest, headers, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onRequestFail(error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }



}

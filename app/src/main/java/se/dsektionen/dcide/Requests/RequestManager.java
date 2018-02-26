package se.dsektionen.dcide.Requests;
import android.util.Base64;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import org.json.JSONObject;

import java.util.HashMap;

import se.dsektionen.dcide.DCideApp;

/**
 * Created by gustavaaro on 2018-02-15.
 */

public class RequestManager {

    private final static String baseUrl = "";
    private final static String authURL = baseUrl + "";

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
        //return DCideApp.getInstance().getSessionManager().getJWTtoken();
        return "";
    }

    public void doGetRequest(String subURL, RequestCallback callback){
        doJsonRequest(GET,null,subURL,callback);
    }

    public void doPostRequest(JSONObject jsonRequest, String subURL, RequestCallback callback){
        doJsonRequest(POST,jsonRequest,subURL,callback);
    }

    public void doPutRequest(JSONObject jsonRequest, String subURL, RequestCallback callback){
        doJsonRequest(PUT,jsonRequest,subURL,callback);
    }

    public void doDeleteRequest(String subURL, RequestCallback callback){
        doJsonRequest(DELETE,null,subURL,callback);
    }

    public void doPatchRequest(JSONObject jsonRequest, String subURL, RequestCallback callback){
        doJsonRequest(PATCH,jsonRequest,subURL,callback);
    }

    public void doBasicAuthRequest(String username, String password, RequestCallback callback){
        HashMap<String, String> params = new HashMap<>();
        String creds = String.format("%s:%s",username, password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        queueRequest(null,authURL,GET,params,callback);
    }


    private void doJsonRequest(int method, JSONObject jsonRequest, String subURL, RequestCallback callback){
        String url = baseUrl + subURL;
        HashMap<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization","Bearer " + getJWT());
        queueRequest(jsonRequest,url,method,authHeader,callback);
    }

    private void queueRequest(final JSONObject jsonRequest, String url, final int method, HashMap<String, String> headers, final RequestCallback callback){
        AuthenticatedRequest jsonObjectRequest = new AuthenticatedRequest(method, url, jsonRequest, headers, new Response.Listener<JSONObject>() {
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

        try {
            System.out.println(jsonObjectRequest.getHeaders().toString());
        }catch (Exception e){
            e.fillInStackTrace();
        }
        requestQueue.add(jsonObjectRequest);
    }



}

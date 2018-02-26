package se.dsektionen.dcide.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class AuthenticatedJsonObjectRequest extends JsonObjectRequest {

    private HashMap<String, String> headers;

    public AuthenticatedJsonObjectRequest(int method, String url, JSONObject jsonRequest, HashMap<String, String> headers, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.headers = headers;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }



}

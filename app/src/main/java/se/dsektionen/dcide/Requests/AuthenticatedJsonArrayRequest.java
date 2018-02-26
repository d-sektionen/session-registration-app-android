package se.dsektionen.dcide.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class AuthenticatedJsonArrayRequest extends JsonArrayRequest {

    private HashMap<String, String> headers;

    public AuthenticatedJsonArrayRequest(int method, String url, JSONArray jsonRequest, HashMap<String, String> headers, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.headers = headers;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }



}

package se.dsektionen.dcide.Requests.Callbacks;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface JsonObjectRequestCallback {

    void onRequestSuccess(JSONObject response);

    void onRequestFail(VolleyError error);

}

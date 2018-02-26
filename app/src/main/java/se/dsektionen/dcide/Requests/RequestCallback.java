package se.dsektionen.dcide.Requests;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface RequestCallback {

    void onRequestSuccess(JSONObject response);

    void onRequestFail(VolleyError error);

}

package se.dsektionen.dcide.Requests;

import com.android.volley.VolleyError;

import org.json.JSONArray;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public interface JsonArrayRequestCallback {

    public void onRequestSuccess(JSONArray array);

    public void onRequestFail(VolleyError error);
}

package se.dsektionen.dcide.Requests.Callbacks;

import com.android.volley.VolleyError;

import se.dsektionen.dcide.JsonModels.User;

/**
 * Created by gustavaaro on 2018-04-09.
 */

public interface UserResponseCallback {

    void onUserFetched(User user);

    void onFail(VolleyError error);
}

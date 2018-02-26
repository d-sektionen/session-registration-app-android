package se.dsektionen.dcide.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class UserSessionManager {

    private String token;
    private SharedPreferences preferences;
    public static final String TOKEN_KEY = "web_token";


    public UserSessionManager(Context context){
        preferences = context.getSharedPreferences("d-cide-prefs",Context.MODE_PRIVATE);
        token = preferences.getString(TOKEN_KEY,"");
    }

    public void setToken(String token) {
        this.token = token;
        preferences.edit().putString(TOKEN_KEY,token).apply();
    }

    public boolean hasToken(){
        return !token.isEmpty();
    }

    public String getToken() {
        return token;
    }


    public void clearSession(){
        this.token = "";
        preferences.edit().remove(TOKEN_KEY).apply();
    }
}

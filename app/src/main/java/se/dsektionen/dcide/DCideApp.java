package se.dsektionen.dcide;

import android.app.Application;

import se.dsektionen.dcide.Requests.RequestManager;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class DCideApp extends Application {

    private static DCideApp instance;
    private RequestManager requestManager;



    public static DCideApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestManager = new RequestManager();
    }


    public RequestManager getRequestManager() {
        return requestManager;
    }
}

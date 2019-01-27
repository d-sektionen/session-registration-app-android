package se.dsektionen.dcide;

import android.app.Application;

import se.dsektionen.dcide.Requests.RequestManager;
import se.dsektionen.dcide.Utilities.EventManager;
import se.dsektionen.dcide.Utilities.MeetingManager;
import se.dsektionen.dcide.Utilities.UserSessionManager;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class DCideApp extends Application {

    private static DCideApp instance;
    private RequestManager requestManager;
    private UserSessionManager userSessionManager;
    private MeetingManager meetingManager;
    private EventManager eventManager;



    public static DCideApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestManager = new RequestManager();
        userSessionManager = new UserSessionManager(this);
        meetingManager = new MeetingManager();
        eventManager = new EventManager();

    }


    public RequestManager getRequestManager() {
        return requestManager;
    }

    public UserSessionManager getUserSessionManager() {
        return userSessionManager;
    }

    public MeetingManager getMeetingManager() {
        return meetingManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}

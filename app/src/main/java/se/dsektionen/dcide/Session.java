package se.dsektionen.dcide;

/**
 * Created by gustavaaro on 2016-12-02.
 */

public class Session {

    private String sessionID;
    private String adminToken;

    public Session(String sessionID, String adminToken){
        this.sessionID = sessionID;
        this.adminToken = adminToken;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public String getSessionID() {
        return sessionID;
    }
}

package se.dsektionen.dcide.Utilities;

/**
 * Created by gustavaaro on 2016-12-02.
 */

public class Session {

    private String sessionID;
    private String adminToken;
    private String section;

    public Session(String sessionID, String adminToken, String section){
        this.sessionID = sessionID;
        this.adminToken = adminToken;
        this.section = section;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getSection() {
        return section;
    }
}

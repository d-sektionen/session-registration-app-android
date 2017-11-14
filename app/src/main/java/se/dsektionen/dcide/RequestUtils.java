package se.dsektionen.dcide;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by gustavaaro on 2016-12-02.
 */

class RequestUtils {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_NOT_VALID = 3;



    private final static String registerURL = "https://d-sektionen.se/api/voting/registration";
    private final static String validationURL = "https://d-sektionen.se/api/voting/session";

    static void registerUser(final Session session, final String userID, final ResultHandler handler){
            makeRequest(session,userID,POST,handler);
    }

    static void deleteUser(final Session session, final String userID, final ResultHandler handler ){
        makeRequest(session,userID,DELETE,handler);
    }

    static void validateSession(final String sessionID, final ResultHandler handler){
        final Thread validationThread = new Thread(){
            InputStream in = null;

            @Override
            public void run(){
                try {
                    URL url = new URL(validationURL);
                    JSONObject headerJSON = new JSONObject();
                    headerJSON.put("session_id",sessionID);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Authorization", headerJSON.toString());
                    urlConnection.setRequestProperty("Content-Type","application/json");
                    urlConnection.setUseCaches(false);

                    in = new BufferedInputStream(urlConnection.getInputStream());

                    String response = "";
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    JSONObject responseJSON = new JSONObject(response);
                    String status = responseJSON.getJSONObject("data").getString("status");
                    String section = responseJSON.getJSONObject("data").getString("section");
                    if(status.equalsIgnoreCase("valid")){
                        handler.onResult(section,STATUS_OK);
                    } else {
                        handler.onResult(null,STATUS_NOT_VALID);
                    }

                }catch(IOException e1){
                    e1.printStackTrace();
                    handler.onResult("Något gick fel.",STATUS_ERROR);
                    Log.e("REQ", "Bad request.");
                } catch (JSONException e2){
                    Log.e("REQ","Bad JSON response.");
                    handler.onResult("Något gick fel.",STATUS_ERROR);
                }
            }
        };

        validationThread.start();
    }


    static private void makeRequest(final Session session, final String userID,final String method, final ResultHandler handler){
        final Thread requestThread = new Thread() {


            InputStream in = null;
            @Override
            public void run() {

                System.out.println(userID);

                try {
                    URL url = new URL(registerURL);

                    JSONObject headerJSON = new JSONObject();
                    JSONObject dataJSON = new JSONObject();
                    headerJSON.put("session_id",session.getSessionID());
                    headerJSON.put("admin_token",session.getAdminToken());
                    dataJSON.put("id",userID);
                    if(method.equals(DELETE)){
                        dataJSON.put("variant","single");
                    }

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(method);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Authorization", headerJSON.toString());
                    urlConnection.setRequestProperty("Content-Type","application/json");
                    urlConnection.setUseCaches(false);

                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    wr.writeBytes(dataJSON.toString());
                    wr.flush();
                    wr.close();

                    int statuscode = urlConnection.getResponseCode();
                    System.out.println(statuscode);

                    in = new BufferedInputStream(urlConnection.getInputStream());


                    if(method.equals(DELETE)){
                        handler.onResult("user deleted",STATUS_OK);
                    } else if(method.equals(POST)){
                        String response = "";
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                        JSONObject responseJSON = new JSONObject(response);
                        String status = responseJSON.getJSONObject("data").getString("status");
                        String userID = responseJSON.getString("liu_id");
                        handler.onResult(status + " " + userID,STATUS_OK);
                    }


                } catch (IOException e){
                    e.printStackTrace();
                    handler.onResult("Något gick fel.",STATUS_ERROR);
                    Log.e("REQ", "Bad request.");
                } catch (JSONException e2){
                    Log.e("REQ","Bad JSON response.");
                    handler.onResult("Något gick fel.",STATUS_ERROR);
                }

            }
        };

        requestThread.start();
    }

}

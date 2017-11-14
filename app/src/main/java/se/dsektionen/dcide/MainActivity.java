package se.dsektionen.dcide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity{

    private final static int PERMISSION_CAMERA = 1;
    private Session currentSession;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.NFC};

    NFCForegroundUtil nfcForegroundUtil = null;

    private TextView resultOkView;
    private TextView resultFailView;
    private EditText idField;
    private Button registerButton;
    private boolean isInRegistrationMode = true;
    public final static int NEW_SESSION_REQUEST = 20;

    private SharedPreferences preferences;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getPreferences(MODE_PRIVATE);

        String lastSessionID = preferences.getString("last_used_session_id","");
        String lastAdminToken = preferences.getString("last_used_admin_token","");

        if(lastAdminToken.isEmpty() || lastSessionID.isEmpty()){
            Intent newSessionIntent = new Intent(this, NewSessionActivity.class);
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
            startActivityForResult(newSessionIntent,NEW_SESSION_REQUEST,bundle);
        } else {
            currentSession = new Session(lastSessionID,lastAdminToken,null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_SESSION_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(this,data.getStringExtra("session_id") + " " + data.getStringExtra("admin_token"),Toast.LENGTH_LONG).show();
            currentSession = new Session(data.getStringExtra("session_id"),data.getStringExtra("admin_token"),data.getStringExtra("section"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent newSessionIntent = new Intent(this, NewSessionActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
        startActivityForResult(newSessionIntent,NEW_SESSION_REQUEST,bundle);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

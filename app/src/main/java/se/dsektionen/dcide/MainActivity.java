package se.dsektionen.dcide;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private final String angmanKort = "4187140526";
    public final static int NEW_SESSION_REQUEST = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newSessionIntent = new Intent(this, NewSessionActivity.class);
        startActivityForResult(newSessionIntent,NEW_SESSION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_SESSION_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(this,data.getStringExtra("session_id") + " " + data.getStringExtra("admin_token"),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

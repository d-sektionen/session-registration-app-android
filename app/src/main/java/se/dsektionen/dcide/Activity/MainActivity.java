package se.dsektionen.dcide.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Vibrator;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import se.dsektionen.dcide.Utilities.NFCForegroundUtil;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Requests.DownloadImageTask;
import se.dsektionen.dcide.Requests.RequestUtils;
import se.dsektionen.dcide.Requests.ResultHandler;
import se.dsektionen.dcide.Utilities.Session;

/**
 * Created by gustavaaro on 2016-11-29.
 */



public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    private final static int PERMISSION_CAMERA = 1;
    private Session currentSession;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.NFC};

    NFCForegroundUtil nfcForegroundUtil = null;


    private TextView resultOkView;
    private TextView resultFailView;
    private TextView currentSessionTV;
    private EditText idField;
    private Button registerButton;
    private ImageView sectionIcon;
    private NfcAdapter mNfcAdapter;


    private boolean isInRegistrationMode = true;
    public final static int NEW_SESSION_REQUEST = 20;

    private SharedPreferences preferences;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcForegroundUtil = new NFCForegroundUtil(this);
        resultFailView = findViewById(R.id.resultFailView);
        resultOkView = findViewById(R.id.resultOkView);
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        sectionIcon = findViewById(R.id.sectionIcon);
        idField = findViewById(R.id.id_field);
        currentSessionTV = findViewById(R.id.currentSessionTV);
        RadioGroup actionGroup = findViewById(R.id.action_group);
        actionGroup.setOnCheckedChangeListener(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        preferences = getSharedPreferences("Prefs",MODE_PRIVATE);

        String lastSessionID = preferences.getString("last_used_session_id","");
        String lastAdminToken = preferences.getString("last_used_admin_token","");
        String lastSection = preferences.getString("last_used_section","");


        if(lastAdminToken.isEmpty() || lastSessionID.isEmpty()){
            Intent newSessionIntent = new Intent(this, NewSessionActivity.class);
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
            startActivityForResult(newSessionIntent,NEW_SESSION_REQUEST,bundle);
        } else {
            currentSession = new Session(lastSessionID,lastAdminToken,lastSection);
            currentSessionTV.setText("Nuvarande session: " + currentSession.getSessionID());
            DownloadImageTask imageTask = new DownloadImageTask(sectionIcon);
            imageTask.execute("https://d-sektionen.se/downloads/logos/"+ currentSession.getSection() + "-sek_logo.png");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcForegroundUtil != null && mNfcAdapter != null){
            nfcForegroundUtil.disableForeground();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_SESSION_REQUEST && resultCode == RESULT_OK){
            currentSession = new Session(data.getStringExtra("session_id"),data.getStringExtra("admin_token"),data.getStringExtra("section"));
            preferences.edit().putString("last_used_session_id",data.getStringExtra("session_id")).apply();
            preferences.edit().putString("last_used_admin_token",data.getStringExtra("admin_token")).apply();
            preferences.edit().putString("last_used_section",data.getStringExtra("section")).apply();
            currentSessionTV.setText("Nuvarande session: " + currentSession.getSessionID());
            DownloadImageTask imageTask = new DownloadImageTask(sectionIcon);
            imageTask.execute("https://d-sektionen.se/downloads/logos/"+ currentSession.getSection() + "-sek_logo.png");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Visar status-meddelande en kort stund
    private void showResult(String message, int status){
        if(status == RequestUtils.STATUS_OK){
            resultOkView.setText(message);
            resultOkView.setVisibility(View.VISIBLE);
            resultOkView.postDelayed(new Runnable() { public void run() { resultOkView.setVisibility(View.GONE); resultOkView.setText(""); } }, 1500);

        } else {
            resultFailView.setText(message);
            resultFailView.setVisibility(View.VISIBLE);
            resultFailView.postDelayed(new Runnable() { public void run() { resultFailView.setVisibility(View.GONE); resultFailView.setText(""); } }, 1500);

        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    // Konverterar bytes till hexadecimal
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    // Används för att då fram rätt kort-id:n som ligger lagrade som en bakvänd hex-sträng
    static String bin2int(byte[] data) {
        byte[] reverse = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            reverse[data.length-i-1] = data[i];
        }

        return Long.toString(Long.valueOf(bytesToHex(reverse),16));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcForegroundUtil != null && mNfcAdapter != null && currentSession != null){
            nfcForegroundUtil.enableForeground();

          if (!nfcForegroundUtil.getNfc().isEnabled())
            {
                Toast.makeText(getApplicationContext(), "Aktivera NFC och tryck på tillbaka.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Avsluta");
        builder.setMessage("Vill du verkligen avsluta?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Nej", null);
        builder.show();
    }

    @Override
    public void onClick(View v) {

        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(), 0);

        ResultHandler handler = new ResultHandler() {
            @Override
            public void onResult(final String response,final int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status == RequestUtils.STATUS_OK) idField.setText("");
                        showResult(response,status);
                    }
                });
            }
        };
        String regex = "[A-za-z]{5}[0-9]{3}";
        boolean validID = idField.getText().toString().matches(regex);


        if(validID){
            if(isInRegistrationMode){
                RequestUtils.registerUser(currentSession,idField.getText().toString().toLowerCase(),handler);
            }else {
                RequestUtils.deleteUser(currentSession,idField.getText().toString().toLowerCase(),handler);
            }
        } else{
            showResult("Inte ett giltigt Liu-ID",RequestUtils.STATUS_ERROR);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(30);

        String rfid = bin2int(tag.getId());
        ResultHandler handler = new ResultHandler() {
            @Override
            public void onResult(final String response,final int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showResult(response,status);
                    }
                });
            }
        };

        if(isInRegistrationMode){
            RequestUtils.registerUser(currentSession, rfid, handler);
        } else {
            RequestUtils.deleteUser(currentSession, rfid, handler);
        }

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        isInRegistrationMode = id == R.id.radioAdd;
        if(isInRegistrationMode) {
            registerButton.setText("Registrera");
        } else{
            registerButton.setText("Ta bort");
        }
    }
}

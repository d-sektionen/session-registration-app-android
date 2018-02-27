package se.dsektionen.dcide.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.Requests.Callbacks.AddAttendantCallback;
import se.dsektionen.dcide.Utilities.MeetingManager;
import se.dsektionen.dcide.Utilities.NFCForegroundUtil;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Requests.DownloadImageTask;

/**
 * Created by gustavaaro on 2016-11-29.
 */



public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{


    NFCForegroundUtil nfcForegroundUtil = null;


    private TextView resultOkView;
    private TextView resultFailView;
    private TextView currentSessionTV;
    private TextView nfcWarningTV;
    private EditText idField;
    private Button registerButton;
    private ImageView sectionIcon;
    private NfcAdapter mNfcAdapter;
    private Meeting currentMeeting;


    private MeetingManager meetingManager;

    private boolean isInRegistrationMode = true;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(Build.VERSION.SDK_INT >= 18 && action != null){
                if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                    updateNFCView();
                }
            }
        }
    };
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
        nfcWarningTV = findViewById(R.id.nfc_warning_view);

        meetingManager = DCideApp.getInstance().getMeetingManager();

        RadioGroup actionGroup = findViewById(R.id.action_group);
        actionGroup.setOnCheckedChangeListener(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        chooseMeeting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcForegroundUtil != null && mNfcAdapter != null){
            nfcForegroundUtil.disableForeground();
        }
        this.unregisterReceiver(mReceiver);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Visar status-meddelande en kort stund
    private void showResult(String message, boolean success){
        if(success){
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
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        if(nfcForegroundUtil != null && mNfcAdapter != null && currentMeeting != null){
            nfcForegroundUtil.enableForeground();
        }
        currentMeeting = DCideApp.getInstance().getMeetingManager().getMeeting();
        Log.d("TAG", " Is null: " + Boolean.toString(currentMeeting == null));

        if(currentMeeting != null){
            currentSessionTV.setText("Nuvarande session: " + currentMeeting.getName() + " för " + currentMeeting.getSection().getName());
            DownloadImageTask imageTask = new DownloadImageTask(sectionIcon);
            imageTask.execute("https://d-sektionen.se/downloads/logos/"+ currentMeeting.getSection().getName().substring(0,1).toLowerCase()+ "-sek_logo.png");
        }

        updateNFCView();
    }


    private void updateNFCView(){
        if(nfcForegroundUtil.getNfc() != null){
            if (!nfcForegroundUtil.getNfc().isEnabled()) {
                nfcWarningTV.setVisibility(View.VISIBLE);
            } else {
                nfcWarningTV.setVisibility(View.GONE);
            }
        }
    }


    private void chooseMeeting(){
        Intent newSessionIntent = new Intent(this, ChooseMeetingActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
        startActivity(newSessionIntent,bundle);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        chooseMeeting();
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

        String regex = "[A-za-z]{5}[0-9]{3}";
        boolean validID = idField.getText().toString().matches(regex);


        if(validID){
            if(isInRegistrationMode){
                meetingManager.addAttendant(idField.getText().toString().toLowerCase(), new AddAttendantCallback() {
                    @Override
                    public void onAttendantAdded() {
                        showResult("Deltagare tillagd",true);
                    }

                    @Override
                    public void addAttendantFailed() {
                        showResult("Misslyckades att lägga till deltagare",false);

                    }
                });
            }else {
                //TODO: deleta user somehow
            }
        } else{
            showResult("Inte ett giltigt Liu-ID",false);
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
        if(isInRegistrationMode){
            meetingManager.addAttendant(rfid, new AddAttendantCallback() {
                @Override
                public void onAttendantAdded() {
                    showResult("Deltagare tillagd",true);
                }

                @Override
                public void addAttendantFailed() {
                    showResult("Misslyckades att lägga till deltagare",false);
                }
            });
        } else {
            //TODO: deleta user somehow
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

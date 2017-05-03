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


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcForegroundUtil = new NFCForegroundUtil(this);
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        resultFailView = (TextView) findViewById(R.id.resultFailView);
        resultOkView = (TextView) findViewById(R.id.resultOkView);
        RadioGroup actionGroup = (RadioGroup) findViewById(R.id.action_group);
        actionGroup.setOnCheckedChangeListener(this);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        idField = (EditText) findViewById(R.id.id_field);

        currentSession = null;


        if (mNfcAdapter == null) {
            Toast.makeText(this, "Den här appen kräver en enhet med NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA);
        } else {
            Intent intent = new Intent(this,QRActivity.class);
            startActivityForResult(intent,2);
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        if(nfcForegroundUtil != null){
            nfcForegroundUtil.enableForeground();

            if (!nfcForegroundUtil.getNfc().isEnabled())
            {
                Toast.makeText(getApplicationContext(), "Aktivera NFC och tryck på tillbaka.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcForegroundUtil != null){
            nfcForegroundUtil.disableForeground();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("NFC","Intent received");
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
    public void onClick(View view) {

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
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    static String bin2int(byte[] data) {
        byte[] reverse = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            reverse[data.length-i-1] = data[i];
        }

        return Long.toString(Long.valueOf(bytesToHex(reverse),16));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data.hasCategory("QR")){
            try {
                JSONObject sessionJSON = new JSONObject(data.getStringExtra("QRresult"));
                currentSession = new Session(sessionJSON.getString("session_id"),sessionJSON.getString("admin_token"));
            }catch (JSONException e){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Inte en giltig QR-kod. Försök igen.");
                dialogBuilder.setPositiveButton("Okej", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(),QRActivity.class);
                        intent.putExtra("noresult",true);
                        startActivityForResult(intent,2);
                    }
                });
                dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent intent = new Intent(getApplicationContext(),QRActivity.class);
                        intent.putExtra("noresult",true);
                        startActivityForResult(intent,2);
                    }
                });
                dialogBuilder.show();
            }
        } else if(data == null){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage("Vänligen skanna sessionens QR-kod för att registrera användare.");
            dialogBuilder.setPositiveButton("Okej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),QRActivity.class);
                    intent.putExtra("noresult",true);
                    startActivityForResult(intent,2);
                }
            });
            dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Intent intent = new Intent(getApplicationContext(),QRActivity.class);
                    intent.putExtra("noresult",true);
                    startActivityForResult(intent,2);
                }
            });
            dialogBuilder.show();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(this,QRActivity.class);
                    startActivityForResult(intent,2);
                }
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

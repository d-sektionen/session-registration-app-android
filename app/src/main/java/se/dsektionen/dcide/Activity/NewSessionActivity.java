package se.dsektionen.dcide.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.Fragment.ManualInputFragment;
import se.dsektionen.dcide.Fragment.MethodPickerFragment;
import se.dsektionen.dcide.R;

/**
 * Created by Gustav on 2017-11-13.
 */

public class NewSessionActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, OnClickListener{

    FragmentManager fragmentManager;
    MethodPickerFragment pickerFragment;
    ImageButton close;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private final static int PERMISSION_CAMERA = 1;
    private final static int QR_REQUEST = 2;
    SharedPreferences preferences;
    boolean appHasSavedSession = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_session_dialog);
        setTitle("");
        pickerFragment = new MethodPickerFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer,pickerFragment).commit();
        fragmentManager.addOnBackStackChangedListener(this);
        close = findViewById(R.id.close_button);
        close.setOnClickListener(this);
        preferences = getSharedPreferences("Prefs", Activity.MODE_PRIVATE);

        String lastSessionID = preferences.getString("last_used_session_id","");
        String lastAdminToken = preferences.getString("last_used_admin_token","");
        System.out.println(lastAdminToken);
        System.out.println(lastSessionID);

        if(lastSessionID.isEmpty() || lastAdminToken.isEmpty()) {
            close.setVisibility(View.GONE);
        } else {
            appHasSavedSession = true;
        }

        System.out.println("In new activity");
        this.setFinishOnTouchOutside(false);
    }

    public void onMethodManual(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
        transaction.replace(R.id.fragmentContainer,new ManualInputFragment(),"manual");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @SuppressLint("RestrictedApi")
    public void onMethodQR(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA);
        } else {
            Intent intent = new Intent(this,QRActivity.class);
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
            startActivityForResult(intent,QR_REQUEST,bundle);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data.hasCategory("QR")){
            try {
                JSONObject sessionJSON = new JSONObject(data.getStringExtra("QRresult"));
                onSessionInfoComplete(sessionJSON.getString("session_id"),sessionJSON.getString("admin_token"),sessionJSON.getString("section"));
            }catch (JSONException e) {
               pickerFragment.showQRFailedView();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(this,QRActivity.class);
                    startActivityForResult(intent,QR_REQUEST);
                }
        }
    }

    public void onSessionInfoComplete(String sessionID, String adminToken, String section){
        Intent result = new Intent();
        System.out.println(sessionID + " " + adminToken + " " +section);
        result.putExtra("session_id",sessionID);
        result.putExtra("admin_token",adminToken);
        result.putExtra("section",section);
        setResult(MainActivity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }else if(appHasSavedSession){
            finish();
        }


    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == close.getId()){
            finish();
        }

    }
}

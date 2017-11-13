package se.dsektionen.dcide;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gustav on 2017-11-13.
 */

public class NewSessionActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener{

    FragmentManager fragmentManager;
    MethodPickerFragment pickerFragment;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private final static int PERMISSION_CAMERA = 1;
    private final static int QR_REQUEST = 2;



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

    public void onMethodQR(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA);
        } else {
            Intent intent = new Intent(this,QRActivity.class);
            startActivityForResult(intent,QR_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data.hasCategory("QR")){
            try {
                JSONObject sessionJSON = new JSONObject(data.getStringExtra("QRresult"));
                onSessionInfoComplete(sessionJSON.getString("session_id"),sessionJSON.getString("admin_token"));
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

    public void onSessionInfoComplete(String sessionID, String adminToken){
        Intent result = new Intent();
        result.putExtra("session_id",sessionID);
        result.putExtra("admin_token",adminToken);
        setResult(MainActivity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount() > 0) fragmentManager.popBackStack();

    }

    @Override
    public void onBackStackChanged() {
    }
}

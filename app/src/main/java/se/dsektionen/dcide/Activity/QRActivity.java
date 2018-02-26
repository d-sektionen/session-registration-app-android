package se.dsektionen.dcide.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import se.dsektionen.dcide.R;

/**
 * Created by gustavaaro on 2016-11-29.
 */

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.startCamera();          // Start camera on resume
            }
        },400);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra("QRresult",result.getText());
        intent.addCategory("QR");
        this.setResult(RESULT_OK, intent);
        finish();
        this.overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

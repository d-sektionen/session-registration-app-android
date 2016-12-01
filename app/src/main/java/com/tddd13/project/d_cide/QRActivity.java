package com.tddd13.project.d_cide;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        if(!getIntent().getBooleanExtra("noresult",false)){
            dialogBuilder.setMessage("Skanna sessionens QR-kod för att börja registrera användare.");
            dialogBuilder.setPositiveButton("Okej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialogBuilder.show();
        }
        // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
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

    }
}

package com.tddd13.project.d_cide;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_CAMERA = 1;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA);
        } else {
            Intent intent = new Intent(this,QRActivity.class);
            startActivityForResult(intent,2);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data.hasCategory("QR")){
            Toast.makeText(this,data.getStringExtra("QRresult"),Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage("Vänligen skanna en QR-kod innehållande sessions-id.");
            dialogBuilder.setPositiveButton("Okej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),QRActivity.class);
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


}

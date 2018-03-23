package com.usesi.mobile;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.zxing.Result;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class Barcode extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        showPermissionDialog();
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
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
        Log.d("tag", "ƒ√: " + result.getText());
        Log.d("tag", "bar code ==  " + result.getBarcodeFormat().toString());
        mScannerView.resumeCameraPreview(this);

        final String barCodeData = result.getText().toString();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(barCodeData);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String result = barCodeData;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",result);
                setResult(MainActivity.RESULT_OK,returnIntent);
                finish();

            }
        });
        alertDialog.create().show();
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {


        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            finish();
        }


    };

    private void showPermissionDialog() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

}

package com.otongsutardjoe.androidwithcamera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.otongsutardjoe.androidwithcamera.databinding.ActivityBarcodeBinding;

public class BarcodeActivity extends AppCompatActivity {
    ActivityBarcodeBinding mBinding;
    private CaptureManager capture;

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityBarcodeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == CUSTOMIZED_REQUEST_CODE) {
            Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
        }

        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

        if (result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                Toast.makeText(this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("MainActivity", "Scanned");
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    }
}
package com.otongsutardjoe.androidwithcamera;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class ScanBarcodeFragment extends Fragment {
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    CompoundBarcodeView barcodeView;

    public ScanBarcodeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_barcode, container, false);
        barcodeView = view.findViewById(R.id.barcode_scanner);
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        barcodeView.initializeFromIntent(integrator.createScanIntent());
        barcodeView.decodeContinuous(callback);
        barcodeView.getViewFinder().setLaserVisibility(false);
        barcodeView.getViewFinder().setMaskColor(getActivity().getColor(R.color.zxing_transparent));
        barcodeView.setStatusText(null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "kosong!", Toast.LENGTH_SHORT).show();
            }

            //Do something with code result
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
}
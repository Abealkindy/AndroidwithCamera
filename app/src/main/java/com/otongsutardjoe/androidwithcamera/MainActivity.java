package com.otongsutardjoe.androidwithcamera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.otongsutardjoe.androidwithcamera.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;
    private PermissionHelper permissionHelper;
//    private int REQUEST_CODE_PERMISSIONS = 101;
//    private String[] REQUIRED_PERMISSION = new String[]{
//            "android.permission.CAMERA",
//            "android.permission.WRITE_EXTERNAL_STORAGE"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        permissionHelper = new PermissionHelper(this);
        mBinding.buttonAnother.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        });
        mBinding.buttonOpenCamera.setOnClickListener(v -> {
            if (checkCameraHardware(this)) {
                mBinding.layoutCamera.setVisibility(View.VISIBLE);
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    openCamera(CameraX.LensFacing.FRONT);
                } else {
                    permissionHelper.check(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                            .onSuccess(this::onSuccessAskPermission)
                            .onDenied(this::onDeniedPermission)
                            .onNeverAskAgain(this::onNeverAskAgainPermission)
                            .run();
                }
            } else {
                Toast.makeText(this, "nggak ada kamera!", Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.buttonOpenScanBarcode.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BarcodeActivity.class));
            finish();
        });
        mBinding.buttonOpenScanBarcodeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.linearBiasa.setVisibility(View.GONE);
                mBinding.frameContainer.setVisibility(View.VISIBLE);
                ScanBarcodeFragment newFragment = new ScanBarcodeFragment();
                setFragment(newFragment);

            }
        });
        mBinding.buttonTakeBack.setOnClickListener(v -> {
            if (checkCameraHardware(this)) {
                mBinding.layoutCamera.setVisibility(View.VISIBLE);
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    openCamera(CameraX.LensFacing.FRONT);
                } else {
                    permissionHelper.check(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                            .onSuccess(this::onSuccessAskPermission)
                            .onDenied(this::onDeniedPermission)
                            .onNeverAskAgain(this::onNeverAskAgainPermission)
                            .run();
                }
            } else {
                Toast.makeText(this, "nggak ada kamera!", Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.buttonTakeFront.setOnClickListener(v -> {
            if (checkCameraHardware(this)) {
                mBinding.layoutCamera.setVisibility(View.VISIBLE);
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    openCamera(CameraX.LensFacing.FRONT);
                } else {
                    permissionHelper.check(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                            .onSuccess(this::onSuccessAskPermission)
                            .onDenied(this::onDeniedPermission)
                            .onNeverAskAgain(this::onNeverAskAgainPermission)
                            .run();
                }
            } else {
                Toast.makeText(this, "nggak ada kamera!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }
    @SuppressLint("RestrictedApi")
    private void openCamera(CameraX.LensFacing lensFacing) {
        CameraX.unbindAll();
        Rational aspectRatio = new Rational(mBinding.frameCamera.getWidth(), mBinding.frameCamera.getHeight());
        Size screenSize = new Size(mBinding.frameCamera.getWidth(), mBinding.frameCamera.getHeight());
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(lensFacing)
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screenSize)
                .build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) mBinding.frameCamera.getParent();
            parent.removeView(mBinding.frameCamera);
            parent.addView(mBinding.frameCamera);
            mBinding.frameCamera.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform();
        });
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setLensFacing(lensFacing)
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetRotation(
                        getWindowManager()
                                .getDefaultDisplay()
                                .getRotation()
                )
                .build();
        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
        mBinding.buttonTakePicture.setOnClickListener(v -> {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg");
            imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                @Override
                public void onImageSaved(@NonNull File file) {
                    Picasso.get().load(file).into(mBinding.imagePreview);
                    Toast.makeText(MainActivity.this, "Saved at " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                    Toast.makeText(MainActivity.this, "Gagal wey!", Toast.LENGTH_SHORT).show();
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                }
            });
        });
        CameraX.bindToLifecycle(this, imageCapture, preview);
    }

    public static File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.WEBP, 70, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        int numCameras = Camera.getNumberOfCameras();
        return numCameras > 0;
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();
        float width = mBinding.frameCamera.getMeasuredWidth();
        float height = mBinding.frameCamera.getMeasuredHeight();

        float cx = width / 2f;
        float cy = height / 2f;

        int rotationDegree;
        int rotation = (int) mBinding.frameCamera.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDegree = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegree = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegree = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegree = 270;
                break;
            default:
                return;
        }
        matrix.postRotate((float) rotationDegree, cx, cy);
        mBinding.frameCamera.setTransform(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onNeverAskAgainPermission() {
        onNeverAskAgainPermission();
        Toast.makeText(this, "Disallowed!", Toast.LENGTH_SHORT).show();
    }

    protected void onSuccessAskPermission() {
        openCamera(CameraX.LensFacing.FRONT);
    }


    protected void onDeniedPermission() {
        Toast.makeText(this, "Disallowed!", Toast.LENGTH_SHORT).show();
    }
}
package com.example.bbg_webview;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.*;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Source;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public  class QrCodeScanner extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor {

    Camera mCamera;
    SurfaceView previewCam;
    TextView txtResultMac,txtResultQRurl;
    ImageView home;
    BarcodeDetector detectQR;
    CameraSource sourceCam;
    Integer RequestCameraPermissionID = 1001;
    String url,getUrl;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scanner);

        getUrl= getIntent().getStringExtra("item");

        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.CAMERA
                }, RequestCameraPermissionID);

        previewCam = findViewById(R.id.cameraPreview);
        txtResultMac = findViewById(R.id.txtMacAddress);
        txtResultQRurl = findViewById(R.id.txtQR_urk);
        home= findViewById(R.id.imgLogo);

        previewCam.setFocusable(true);
        previewCam.requestFocus();
        previewCam.getHolder().addCallback(this);

        try {
            mCamera = Camera.open();
        }catch (RuntimeException ex){}

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
        mCamera.startPreview();

        try {
            mCamera.setPreviewDisplay(previewCam.getHolder());
            //mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        detectQR = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        sourceCam = new CameraSource.Builder(this, detectQR)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

        mCamera.stopPreview();
        mCamera.release();

        detectQR.setProcessor(this);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("item", getUrl);
            startActivity(intent);
        });
    }
    public void onPause(){
        //sourceCam.stop();
        super.onPause();
    }

    public void onResume(){
        // if(mCamera!=null){
        //     mCamera.open();}
        super.onResume();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.CAMERA
                    }, RequestCameraPermissionID);
            return;
        }try {
            sourceCam.start(previewCam.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        sourceCam.stop();
    }

    @Override
    public void release() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void receiveDetections(Detector.Detections detections) {
        //throw new NotImplementedException();

        SparseArray qrcodes = detections.getDetectedItems();

        if (qrcodes.size() != 0)
        {
            txtResultQRurl.post(() ->
            {
                sourceCam.stop();
                //Permite identificar o Mac Address (utilizavel para android infevior a 6.0)
                WifiManager macAddress = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                String m_macAddress = macAddress.getConnectionInfo().getMacAddress();

                //Valores inseridos nas variaveis ao detetar o QRcode
                txtResultQRurl.setText(((Barcode)qrcodes.valueAt(0)).rawValue);
                txtResultMac.setText((String)m_macAddress);

                //url =  ((Barcode)qrcodes.valueAt(0)).rawValue;
                url=((Barcode)qrcodes.valueAt(0)).rawValue;
                System.out.println("qr code detectado");

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("item", url);

                startActivity(intent);

            });
        }
    }   
}

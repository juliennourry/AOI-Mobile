package fr.java.aoitechnicien;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;


public class ScanFragment extends Fragment {

    private HomeActivity homeAct;
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Boolean cameraPerm = false;
    private Boolean controlCameraPerm = false;
    private Boolean qrScan = true;
    private Button btnSearchItemUUID;
    private ProgressBar progressBarSearchItem;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 100001;
    private final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 200002;
    private GestureDetector gestureDetector;
    ToastHelper toastHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // -- TO GET FRAGMENT AS ACTIVITY
        View fRoot = inflater.inflate(R.layout.fragment_scan, container, false);

        cameraView = (SurfaceView) fRoot.findViewById(R.id.camera_view);
        barcodeInfo = (TextView) fRoot.findViewById(R.id.code_info);
        btnSearchItemUUID = (Button) fRoot.findViewById(R.id.btnSearchItemUUID);
        progressBarSearchItem = (ProgressBar) fRoot.findViewById(R.id.progress_bar_search_item);

        // -- DB
        final DatabaseHelper databaseHelper;
        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        // -- CALL PERMISSION LOCATION
        checkLocationPermission(fRoot);

        barcodeDetector =
                new BarcodeDetector.Builder(fRoot.getContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        cameraSource = new CameraSource
                .Builder(fRoot.getContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 640)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraPerm = checkCameraPermission(fRoot);
                    if(cameraPerm) {
                        controlCameraPerm = controlPermissions(fRoot);
                        if(controlCameraPerm){
                            cameraSource.start(cameraView.getHolder());
                        } else {
                        }
                    } else {
                    }
                } catch (IOException ie) {
                    Log.d("CAMERA SOURCE", ie.getMessage());
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && qrScan) {
                    barcodeInfo.post(new Runnable() {
                        public void run() {
                            barcodeInfo.setText(barcodes.valueAt(0).displayValue);
                            // -- SIMULATE CLICK BUTTON
                            btnSearchItemUUID.setVisibility(View.GONE);
                            progressBarSearchItem.setVisibility(View.VISIBLE);
                            btnSearchItemUUID.performClick();
                            qrScan = false;
                        }
                    });
                }
            }
        });

        btnSearchItemUUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(databaseHelper.checkUuidItem(database, barcodeInfo.getText().toString())) {
                    toastHelper.LoadToasted(fRoot.getResources().getString(R.string.access));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnSearchItemUUID.setVisibility(View.VISIBLE);
                            progressBarSearchItem.setVisibility(View.GONE);
                            qrScan = true;
                        }
                    }, 2000);
                    Intent intent = new Intent(getActivity(), ItemActivity.class);
                    intent.putExtra("uuid", barcodeInfo.getText().toString().trim());
                    startActivity(intent);
                } else {
                    toastHelper.LoadToasted(fRoot.getResources().getString(R.string.no_access));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnSearchItemUUID.setVisibility(View.VISIBLE);
                            progressBarSearchItem.setVisibility(View.GONE);
                            qrScan = true;
                        }
                    }, 2000);
                }

            }
        });

        // -- RETURN FRAGMENT
        return fRoot;
    }

    private boolean checkCameraPermission(View fRoot) {

        if (ContextCompat.checkSelfPermission(fRoot.getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) fRoot.getContext(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
        return true;
    }

    private boolean controlPermissions(View fRoot) {
        String requiredPermission = Manifest.permission.CAMERA;
        int checkVal = fRoot.getContext().checkCallingOrSelfPermission(requiredPermission);
        Log.d("DEBUG_PERMISSION", String.valueOf(checkVal));
        Log.d("DEBUG_PERMISSION", String.valueOf(PackageManager.PERMISSION_GRANTED));
        if (checkVal==PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private boolean checkLocationPermission(View fRoot) {

        if (ContextCompat.checkSelfPermission(fRoot.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) fRoot.getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
        return true;
    }

}
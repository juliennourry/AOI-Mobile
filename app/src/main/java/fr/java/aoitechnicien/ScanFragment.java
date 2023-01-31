package fr.java.aoitechnicien;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


public class ScanFragment extends Fragment {

    private HomeActivity homeAct;
    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Boolean cameraPerm = false;
    private Boolean controlCameraPerm = false;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 100001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // -- TO GET FRAGMENT AS ACTIVITY
        View fRoot = inflater.inflate(R.layout.fragment_scan, container, false);

        cameraView = (SurfaceView) fRoot.findViewById(R.id.camera_view);
        barcodeInfo = (TextView) fRoot.findViewById(R.id.code_info);

        barcodeDetector =
                new BarcodeDetector.Builder(fRoot.getContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        cameraSource = new CameraSource
                .Builder(fRoot.getContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();



        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraPerm = checkCameraPermission(fRoot);
                    if(cameraPerm) {
                        controlCameraPerm = controlPermissions(fRoot);
                        if(controlCameraPerm){
                            Log.d("PERMISSIONADO", "yes perm");
                            cameraSource.start(cameraView.getHolder());
                        } else {
                            Log.d("PERMISSIONADO", "no perm");
                        }
                    } else {
                        Log.d("PERMISSIONADO", "no perm 2");
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
                if (barcodes.size() != 0) {
                    barcodeInfo.post(new Runnable() {
                        public void run() {
                            barcodeInfo.setText(    // Update the TextView
                                    barcodes.valueAt(0).displayValue
                            );
                        }
                    });
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
        Log.d("PERMISSIONADO", "no perm 3");
        String requiredPermission = Manifest.permission.CAMERA;
        int checkVal = fRoot.getContext().checkCallingOrSelfPermission(requiredPermission);
        Log.d("PERMISSIONADO", String.valueOf(checkVal));
        Log.d("PERMISSIONADO", String.valueOf(PackageManager.PERMISSION_GRANTED));
        if (checkVal==PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;


    }
}
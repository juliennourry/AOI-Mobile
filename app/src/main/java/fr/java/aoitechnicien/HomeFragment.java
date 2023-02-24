package fr.java.aoitechnicien;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    private final int CAMERA_PERMISSION_REQUEST_CODE = 100001;
    private final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 200002;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRoot = inflater.inflate(R.layout.fragment_home, container, false);

        checkCameraPermission(fRoot);

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

}
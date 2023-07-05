package fr.java.aoitechnicien;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class InformationFragment extends Fragment {

    private Cursor cursor_user, cursor_sync, cursor_api;
    private String serialNumber, androidId, version, name, sync, syncDate, apiurl, model, sdk;
    private Button btnDeconnexion;
    private TextView dataDevice, dataDatabase, dataUser, dataSync, dataUrl, dataModel, dataSdk;
    ToastHelper toastHelper;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper;
    public static final String sessionKey = "sessionKey";
    public static final String tokenKey = "tokenKey";
    public static final String loginKey = "loginKey";
    public static final String pswKey = "pswKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRoot = inflater.inflate(R.layout.fragment_information, container, false);

        // -- DB
        final DatabaseHelper databaseHelper;

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        // -- SESSION INFORMATION
        sharedhelper = new SharedHelper(getActivity(), sessionKey);

        dataDevice = fRoot.findViewById(R.id.dataDevice);
        dataDatabase = fRoot.findViewById(R.id.dataDatabase);
        dataUser = fRoot.findViewById(R.id.dataUser);
        dataSync = fRoot.findViewById(R.id.dataSync);
        dataUrl = fRoot.findViewById(R.id.dataUrl);
        dataModel = fRoot.findViewById(R.id.dataModel);
        dataSdk = fRoot.findViewById(R.id.dataSdk);
        btnDeconnexion = fRoot.findViewById(R.id.btnDeconnexion);

        // -- DATA
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        version = String.valueOf(database.getVersion());
        model = String.valueOf(Build.MANUFACTURER + " " + Build.MODEL);
        sdk = String.valueOf(Build.VERSION.SDK_INT);
        name = null;


        Log.d("DEBUG_INFORMATION", " :: 1 :: ");

        cursor_user = databaseHelper.getInfoUser(database, sharedhelper.getParam(loginKey));
        if (cursor_user.moveToFirst()) {
            Log.d("DEBUG_INFORMATION", " :: 2 :: ");
            name = cursor_user.getString(cursor_user.getColumnIndexOrThrow("name")) + " " + cursor_user.getString(cursor_user.getColumnIndexOrThrow("lastname"));

        }
        cursor_sync = databaseHelper.getSync(database);
        sync = null;
        syncDate = null;
        Log.d("DEBUG_INFORMATION", " :: 3 :: ");
        if (cursor_sync.moveToFirst()) {
            Log.d("DEBUG_INFORMATION", " :: 4 :: ");
            sync = cursor_sync.getString(cursor_sync.getColumnIndexOrThrow("createdAt"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(Long.parseLong(sync));
            syncDate = dateFormat.format(date);
        }
        apiurl = "undefined";
        Log.d("DEBUG_INFORMATION", " :: 5 :: ");
        cursor_api = databaseHelper.getAPI(database);
        if (cursor_api.moveToFirst()) {
            Log.d("DEBUG_INFORMATION", " :: 6 :: ");
            apiurl = cursor_api.getString(cursor_api.getColumnIndexOrThrow("url"));
        }
        dataDevice.setText(androidId);
        dataDatabase.setText("v." + version);
        dataUser.setText(name);
        dataSync.setText(syncDate);
        dataUrl.setText(apiurl);
        dataModel.setText(model);
        dataSdk.setText(sdk);

        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeconnectDialog(fRoot.getContext());
            }
        });



        return fRoot;
    }

    public void showDeconnectDialog(Context context) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.logout))
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        sharedhelper.stockParam(tokenKey, "");
                        sharedhelper.stockParam(loginKey, "");
                        sharedhelper.stockParam(pswKey, "");

                        databaseHelper.verifyAndInsert(database, "0");


                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("extra_sync", "false");
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
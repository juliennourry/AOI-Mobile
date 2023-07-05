package fr.java.aoitechnicien;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class SignatureFragment extends DialogFragment {
    private SignView signView;
    private TextView clearSign, confirmSign, withoutSign;
    private ImageView closeSign;
    public EditText clientName;
    ToastHelper toastHelper;
    Bitmap signatureBitmap;
    Bundle bundle;
    Map<String, String> mapIntervention;
    Button btnRetourIntervention;
    Fragment bgFrag;

    // -- DB
    private static DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fRoot = inflater.inflate(R.layout.fragment_signature, container, false);
        clearSign = (TextView) fRoot.findViewById(R.id.clear_sign);
        confirmSign = (TextView) fRoot.findViewById(R.id.confirm_sign);
        withoutSign = (TextView) fRoot.findViewById(R.id.without_sign);

        closeSign = (ImageView) fRoot.findViewById(R.id.end_sign);

        clientName = (EditText) fRoot.findViewById(R.id.client_name);

        signView = (SignView) fRoot.findViewById(R.id.signature_view);
        signView.setDrawingCacheEnabled(true);

        // -- TAKE INOFORMATION FROM ANOTHER FRAGMENT
        bgFrag = (Fragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_layout_item);
        View bgFragView = bgFrag.getView();
        btnRetourIntervention = bgFragView.findViewById(R.id.btnRetourIntervention);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        bundle = getArguments();
        mapIntervention = (Map<String, String>) bundle.getSerializable("mapIntervention");


        clearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signView.clearSignature();
            }
        });
        closeSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        withoutSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mapIntervention.put("signData", "");
                mapIntervention.put("signName", String.valueOf(clientName.getText()));
                    showSignNotDialog(fRoot.getContext(), database);
            }
        });

        confirmSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // -- GET Base64 sign
                //Bitmap bitmap = Bitmap.createBitmap(signView.getWidth(), signView.getHeight(), Bitmap.Config.ARGB_8888);
                //signatureBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                signatureBitmap = signView.getDrawingCache();
                //Log.e("DEBUG_sBitMap", signatureBitmap.toString());
                // Convert the Bitmap to a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                //Log.e("DEBUG_sBitMap", byteArray.toString());
                // Encode the byte array as Base64
                String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
                //Log.e("DEBUG_sBitMap", base64String);
                mapIntervention.put("signData", base64String);
                mapIntervention.put("signName", String.valueOf(clientName.getText()));


                if(clientName.getText().length() == 0 && base64String.length() == 0) {
                    toastHelper.LoadToasted(getResources().getString(R.string.add_name_sign));
                } else {
                    showSignConfirmationDialog(fRoot.getContext(), database);
                }
            }
        });
        return fRoot;
    }

    public void dismiss() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    public void showSignConfirmationDialog(Context context, SQLiteDatabase database) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.save_sign))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.btn_oui), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform OK action
                        if(databaseHelper.insertIntervention(database, mapIntervention)) {
                            Log.i("DEBUG_INTERVENTION", "Insert new Intervention SUCCESS");
                            dismiss();
                            redirectionBtnPage();
                        } else {
                            Log.e("DEBUG_INTERVENTION", "Insert new Intervention FAIL");
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.btn_non), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void showSignNotDialog(Context context, SQLiteDatabase database) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.save_without_sign))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.btn_oui), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform the positive OK
                        if(databaseHelper.insertIntervention(database, mapIntervention)) {
                            Log.i("DEBUG_INTERVENTION", "Insert new Intervention SUCCESS");
                            dismiss();
                            redirectionBtnPage();
                        } else {
                            Log.e("DEBUG_INTERVENTION", "Insert new Intervention FAIL");
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.btn_non), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void redirectionBtnPage() {
        toastHelper.LoadToasted(getResources().getString(R.string.save_intervention));
        btnRetourIntervention.performClick();
//        Activity currentActivity = getActivity();
//        Intent intent = new Intent(currentActivity, ItemActivity.class);
//        startActivity(intent);

    }
}
package fr.java.aoitechnicien;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
                    showSignNotDialog(fRoot.getContext());
            }
        });

        confirmSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signatureBitmap = signView.getSignatureBitmap();
                if(clientName.getText().length() == 0) {
                    toastHelper.LoadToasted("Veuillez renseigner le nom et la signature du client.");
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
        builder.setMessage("Enregistrer la signature?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform OK action
                        mapIntervention.put("signName", String.valueOf(clientName.getText()));
                        if(databaseHelper.insertIntervention(database, mapIntervention)) {
                            Log.i("DEBUG_INTERVENTION", "Insert new Intervention SUCCESS");
                        } else {
                            Log.e("DEBUG_INTERVENTION", "Insert new Intervention FAIL");
                        }
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


    public void showSignNotDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Enregistrer sans signature?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform the positive action, such as deleting the item
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
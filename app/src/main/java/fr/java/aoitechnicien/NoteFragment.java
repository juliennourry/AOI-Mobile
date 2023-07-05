package fr.java.aoitechnicien;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;


public class NoteFragment extends DialogFragment {

    private EditText edit_text_note_maintenance;
    private ImageView closeNote;
    private Button btn_valid_maintenance;
    Bundle bundle;
    Map<String, String> mapMaintenance;
    Integer control_form;
    ToastHelper toastHelper;
    TextView text_note_maintenance;
    Button btnRetourMaintenance;
    Fragment bgFrag;
    CheckBox checkComplete;

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
        View fRoot = inflater.inflate(R.layout.fragment_note, container, false);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        edit_text_note_maintenance = (EditText) fRoot.findViewById(R.id.edit_text_note_maintenance);
        btn_valid_maintenance = (Button) fRoot.findViewById(R.id.btn_valid_maintenance);
        text_note_maintenance = (TextView) fRoot.findViewById(R.id.text_note_maintenance);
        checkComplete = fRoot.findViewById(R.id.checkboxComplete);

        bundle = getArguments();
        mapMaintenance = (Map<String, String>) bundle.getSerializable("mapMaintenance");

        closeNote = (ImageView) fRoot.findViewById(R.id.end_note);

        // -- TAKE INOFORMATION FROM ANOTHER FRAGMENT
        bgFrag = (Fragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_layout_item);
        View bgFragView = bgFrag.getView();
        btnRetourMaintenance = bgFragView.findViewById(R.id.btnRetourMaintenance);

        // -- CLOSE
        closeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_valid_maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                control_form = 0;
                if(edit_text_note_maintenance.getText().length() == 0){
                    control_form++;
                    text_note_maintenance.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.red));
                }

                if(control_form > 0) {
                    toastHelper.LoadToasted(fRoot.getResources().getString(R.string.add_note_maintenance));
                } else {
                    mapMaintenance.put("note", String.valueOf(edit_text_note_maintenance.getText()));
                    if(checkComplete.isChecked()){
                        mapMaintenance.put("complete", (String) "1");
                    } else {
                        mapMaintenance.put("complete", (String) "0");
                    }
                    Log.e("DEBUG_MAP_MAINTENANCE", mapMaintenance.toString());
                    showNoteConfirmationDialog(fRoot.getContext(), database);
                }
            }
        });

        return fRoot;
    }


    public void dismiss() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    public void showNoteConfirmationDialog(Context context, SQLiteDatabase database) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.save_close_maintenance))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.btn_oui), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform OK action
                        if(databaseHelper.validMaintenance(database, mapMaintenance)) {
                            Log.i("DEBUG_MAINTENANCE", "Insert new Intervention SUCCESS");
                            dismiss();
                            redirectionBtnPage();
                        } else {
                            Log.e("DEBUG_MAINTENANCE", "Insert new Intervention FAIL");
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.btn_non), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the popup
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void redirectionBtnPage() {
        toastHelper.LoadToasted(getResources().getString(R.string.save_maintenance));
        btnRetourMaintenance.performClick();
//        Activity currentActivity = getActivity();
//        Intent intent = new Intent(currentActivity, ItemActivity.class);
//        startActivity(intent);

    }

}
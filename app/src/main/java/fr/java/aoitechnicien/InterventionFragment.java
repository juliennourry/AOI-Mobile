package fr.java.aoitechnicien;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fr.java.aoitechnicien.Function.CalendarPicker;
import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;


public class InterventionFragment extends Fragment {

    String[] item = {"Réparation", "Dépannage"};
    String itemSelected = "";
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    Button btnValidIntervention;
    TextView startDateIntervention, endDateIntervention, textNoteIntervention, editTextNoteIntervention;
    CalendarPicker calendarPicker;
    Integer control_form;
    ToastHelper toastHelper;
    TextInputLayout hintTypeIntervention;
    Map<String, String> mapIntervention;
    CheckBox checkstop;
    Bundle bundle;

    // -- DB
    private static DatabaseHelper databaseHelper;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper_user, sharedhelper_appareil;
    public static final String appareilKey = "appareilKey";
    public static final String idsyncKey = "idsyncKey";
    public static final String coordKey = "coordKey";
    // --
    public static final String sessionKey = "sessionKey";
    public static final String loginKey = "loginKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fRoot = inflater.inflate(R.layout.fragment_intervention, container, false);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- SESSION LOAD
        sharedhelper_user = new SharedHelper(getActivity(), sessionKey);
        sharedhelper_appareil = new SharedHelper(getActivity(), appareilKey);

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        btnValidIntervention = fRoot.findViewById(R.id.btn_valid_intervention);
        autoCompleteTextView = fRoot.findViewById(R.id.auto_complete_text_view);
        startDateIntervention = fRoot.findViewById(R.id.date_start_intervention);
        endDateIntervention = fRoot.findViewById(R.id.date_end_intervention);
        textNoteIntervention = fRoot.findViewById(R.id.text_note_intervention);
        editTextNoteIntervention = fRoot.findViewById(R.id.edit_text_note_intervention);
        hintTypeIntervention = fRoot.findViewById(R.id.hint_type_intervention);
        checkstop = fRoot.findViewById(R.id.checkbox);

        adapterItems = new ArrayAdapter<String>(fRoot.getContext(), R.layout.list_item, item);

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemSelected = adapterView.getItemAtPosition(i).toString();
            }
        });

        startDateIntervention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarPicker = new CalendarPicker(fRoot.getContext(), fRoot);
                calendarPicker.showDateTimePicker("01");
            }
        });

        endDateIntervention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarPicker = new CalendarPicker(fRoot.getContext(), fRoot);
                calendarPicker.showDateTimePicker("02");
            }
        });

        btnValidIntervention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_form = 0;
                if(editTextNoteIntervention.getText().length() == 0){
                    control_form++;
                    textNoteIntervention.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.red));
                }
                if(startDateIntervention.getText().equals("Date de début *")){
                    control_form++;
                    startDateIntervention.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.red));
                }
                if(endDateIntervention.getText().equals("Date de fin *")){
                    control_form++;
                    endDateIntervention.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.red));
                }
                Log.e("DEBUG_HINTINTERVENTION", String.valueOf(hintTypeIntervention.getHint()));
                if(itemSelected.equals("Type d'intervention *")){
                    control_form++;
                    hintTypeIntervention.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(fRoot.getContext(), R.color.red)));
                }
                if(control_form > 0) {
                    toastHelper.LoadToasted("Veuillez contrôler les champs obligatoires (*)");
                } else {
                    mapIntervention = new HashMap<>();
                    mapIntervention.put("fkUser", databaseHelper.getIdSyncUser(database, sharedhelper_user.getParam("loginKey")));
                    mapIntervention.put("fkItemsite", sharedhelper_appareil.getParam("idsyncKey"));
                    CharSequence editText = editTextNoteIntervention.getText();
                    String textNote = editText.toString();
                    mapIntervention.put("note", textNote);
                    mapIntervention.put("type", itemSelected);
                    if(checkstop.isChecked()){
                        mapIntervention.put("stop", (String) "1");
                    } else {
                        mapIntervention.put("stop", (String) "0");
                    }
                    mapIntervention.put("startDate", (String) startDateIntervention.getText());
                    mapIntervention.put("endDate", (String) endDateIntervention.getText());
                    mapIntervention.put("coordinates", sharedhelper_appareil.getParam("coordKey"));
                    bundle = new Bundle();
                    bundle.putSerializable("mapIntervention", (Serializable) mapIntervention);
                    SignatureFragment fragment = new SignatureFragment();
                    fragment.setArguments(bundle);
                    fragment.show(getParentFragmentManager(), "SignFrag");
                }
            }
        });

        return fRoot;
    }




}
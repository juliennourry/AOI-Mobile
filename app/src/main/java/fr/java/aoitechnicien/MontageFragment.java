package fr.java.aoitechnicien;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Models.ModelApiMountageDone;
import fr.java.aoitechnicien.Models.ModelItem;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class MontageFragment extends Fragment {

    Cursor cursor;
    private List<ModelItem> listItem;
    LinearLayout linear_layout_mountage;
    TextView mountageClosed;
    EditText hiddenInputMountage, hiddenInputAppareil;
    Button btnValidMountage, btnRetourMontage;
    String ids;
    ToastHelper toastHelper;
    ModelApiMountageDone modelMountageDone;
    Map<String, Integer> tagLabelMap = new HashMap<>();
    Map<String, Integer> tagMountageMap = new HashMap<>();


    // -- DB
    private static DatabaseHelper databaseHelper;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper_appareil;
    public static final String appareilKey = "appareilKey";
    public static final String idsyncKey = "idsyncKey";
    public static final String uuidKey = "uuidKey";
    public static final String fkCategoryKey = "fkCategoryKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRoot = inflater.inflate(R.layout.fragment_montage, container, false);

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- SESSION LOAD
        sharedhelper_appareil = new SharedHelper(getActivity(), appareilKey);

        // Create a LinearLayout to hold the tasks and input fields
        linear_layout_mountage = fRoot.findViewById(R.id.linear_layout_mountage);
        hiddenInputMountage = fRoot.findViewById(R.id.hidden_input_mountage);
        hiddenInputAppareil = fRoot.findViewById(R.id.hidden_input_appareil);
        btnValidMountage = fRoot.findViewById(R.id.btnValidMountage);
        btnRetourMontage = fRoot.findViewById(R.id.btnRetourMontage);
        mountageClosed = fRoot.findViewById(R.id.mountage_closed);



        // -- Get Montage
        ids = "";
        Integer control = 0;
        hiddenInputAppareil.setText(sharedhelper_appareil.getParam("idsyncKey"));
        Cursor cursor = databaseHelper.getMontage(database, sharedhelper_appareil.getParam("fkCategoryKey"));
        if (cursor.moveToFirst()) {
            do {

                if (cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")) > 0) {
                    if (ids.isEmpty()){
                        ids += cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"));
                    } else {
                        ids += "," + cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"));
                    }
                }

                hiddenInputMountage.setText(ids);

                Float estimated_time = cursor.getFloat(cursor.getColumnIndexOrThrow("estimated_time"));

                String taskName = cursor.getString(cursor.getColumnIndexOrThrow("label"));

                TextView taskView = new TextView(fRoot.getContext());
                taskView.setText(taskName);
                int newIdLabel = fRoot.generateViewId();
                String label_name = "label_" + cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"));
                taskView.setId(newIdLabel);
                taskView.setTag(label_name);
                tagLabelMap.put(label_name, newIdLabel);
                linear_layout_mountage.addView(taskView);

                // Get the input field name from the database
                String inputFieldName = "En heure (0.5 équivaut à 30min.)";

                // Create an EditText for the input field
                //  // -- Read Data if exists and put on inputfield
                Cursor cursorMountageDone = databaseHelper.getMontageDone(database, String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id"))), sharedhelper_appareil.getParam("idsyncKey"));
                Float dataField = 0f;
                if (cursorMountageDone.moveToFirst()) {
                    do {
                        dataField = cursorMountageDone.getFloat(cursorMountageDone.getColumnIndexOrThrow("doneTime"));
                    } while (cursorMountageDone.moveToNext());
                }
                EditText inputField = new EditText(fRoot.getContext());
                inputField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                inputField.setHint(inputFieldName);
                int newIdMountage = fRoot.generateViewId();
                String montage_name = "mountage_" + cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"));
                inputField.setId(newIdMountage);
                inputField.setTag(montage_name);
                if(dataField > 0){
                    inputField.setText(String.valueOf(dataField));
                    inputField.setKeyListener(null);
                } else {
                    control++;
                }
                tagMountageMap.put(montage_name, newIdMountage);
                linear_layout_mountage.addView(inputField);
            } while (cursor.moveToNext());
        }

        if(control == 0){
            btnValidMountage.setVisibility(View.GONE);
            mountageClosed.setVisibility(View.VISIBLE);
        } else {
            btnValidMountage.setVisibility(View.VISIBLE);
            mountageClosed.setVisibility(View.GONE);
        }

        cursor.close();
        database.close();

        btnValidMountage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenInputMountage.getText().length() > 0) {
                    String ids = String.valueOf(hiddenInputMountage.getText());
                    Log.e("DEBUG_IDS_MOUNTAGE", ids);
                    String[] idArray = ids.split(",");
                    Boolean ipControl = false;
                    for (int i = 0; i < idArray.length; i++) {
                        // -- Recup and construc
                        String id = idArray[i];
                        String name_mountage = "mountage_" + id;
                        String name_label = "label_" + id;
                        int id_set = fRoot.getContext().getResources().getIdentifier(String.valueOf(tagMountageMap.get(name_mountage)), "id", fRoot.getContext().getPackageName());
                        int id_label_set = fRoot.getContext().getResources().getIdentifier(String.valueOf(tagLabelMap.get(name_label)), "id", fRoot.getContext().getPackageName());
                        EditText editText = fRoot.findViewById(id_set);
                        TextView textView = fRoot.findViewById(id_label_set);

                        // -- get Current date
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        String currentTime = formatter.format(new Date());

                        // -- Put on contentValue
                        String ipValue = String.valueOf(editText.getText());
                        if (ipValue != null && !ipValue.isEmpty()) {
                            try {
                                double number = Double.parseDouble(ipValue);
                                // -- nothing
                            } catch (NumberFormatException e) {
                                ipControl = false;
                                //toastHelper.LoadToasted(fRoot.getResources().getString(R.string.error_control_input_mount));
                                break;
                            }
                        } else {
                            ipControl = false;
                            //toastHelper.LoadToasted(fRoot.getResources().getString(R.string.error_control_input_mount));
                            break;
                        }
                        modelMountageDone = new ModelApiMountageDone(0, "", (float) 0, "", "", "", "");
                        modelMountageDone.setId(0);
                        modelMountageDone.setLabel(String.valueOf(textView.getText()));
                        modelMountageDone.setDoneAt(currentTime);
                        modelMountageDone.setCreatedAt(currentTime);
                        modelMountageDone.setFkMountageStep(id);
                        modelMountageDone.setFkItemsite(String.valueOf(hiddenInputAppareil.getText()));
                        modelMountageDone.setDoneTime(Float.valueOf(String.valueOf(editText.getText())));


                        SQLiteDatabase database = databaseHelper.getWritableDatabase();
                        Boolean insertMountageStep = databaseHelper.verifyAndInsertMountageDone(database, modelMountageDone, false);

                        if(insertMountageStep) {
                            ipControl = true;
                        } else {
                            ipControl = false;
                            //toastHelper.LoadToasted(fRoot.getResources().getString(R.string.error_save_mount));
                            break;
                        }

                    }
                    // -- Retour
                    if(ipControl){
                        toastHelper.LoadToasted(fRoot.getResources().getString(R.string.save_mount));
                    }else{
                        toastHelper.LoadToasted(fRoot.getResources().getString(R.string.error_control_input_mount));
                    }
                    btnRetourMontage.performClick();
                }else{
                    toastHelper.LoadToasted(fRoot.getResources().getString(R.string.no_config_mount));
                }
            }
        });

        btnRetourMontage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- default Fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout_item,new ItemButtonFragment());
                fragmentTransaction.addToBackStack("01");
                fragmentTransaction.commit();
                fragmentManager.popBackStack("01", 0);
            }
        });

        return fRoot;
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}
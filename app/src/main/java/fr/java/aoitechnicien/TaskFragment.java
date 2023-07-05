package fr.java.aoitechnicien;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.java.aoitechnicien.Function.MyExpandableListAdapter;
import fr.java.aoitechnicien.Function.RealMadridDate;
import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;


public class TaskFragment extends Fragment {

    MyExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    int idFrequency, idCategory, idSyncItem, nDay;
    String d_start, d_end, out_start, out_end, labelArg, dateFrequency, idSyncRMF;
    TextView tvFrequency;
    Map<String, List<Map<String, String>>> taskDataComplete;
    List<String> groupTitles;
    List<String> groupIds;
    Map<String, String> groupsMap;
    Map<String, String> map_III;
    List<Map<String, String>> map_II;
    Long idMaint;
    Date onAt, dateFrequencyFormat;
    RealMadridDate rmDate;
    Map<String, String> mapDate;
    Button btnValidMaintenance, btnRetourMaintenance;

    // -- DB
    private static DatabaseHelper databaseHelper;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper_appareil, sharedhelper_maintenance;
    public static final String appareilKey = "appareilKey";
    public static final String maintenanceKey = "maintenanceKey";
    public static final String idsyncKey = "idsyncKey";
    public static final String uuidKey = "uuidKey";
    public static final String fkCategoryKey = "fkCategoryKey";
    public static final String dateServiceKey = "dateServiceKey";
    public static final String frequencyKey = "frequencyKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        List<String> groupTitles = Arrays.asList("Cabine", "Paliers", "Portes", "Machinerie", "Gaine", "Cuvette");

        Map<String, List<String>> taskData = new HashMap<>();
        taskData.put("Cabine", Arrays.asList("Boutons", "Eclairage", "Voyants", "Alarmes", "Téléalarme", "Précision d'arrêt", "Inspection", "Signalisations"));
        taskData.put("Paliers", Arrays.asList("Boutons", "Indicateurs", "Flèches", "Voyants", "Arrêts à niveaux", "Appel pompier"));
        taskData.put("Portes", Arrays.asList("Fonctionnement", "Réouverture", "Contact de choc", "Contrôle mécanique", "Contrôle électrique", "déverrouillage manuel", "Fonctionnement barrière cellule"));
        taskData.put("Machinerie", Arrays.asList("Treuil", "Flexible Hy", "Centrale Hy(nivx)", "Présence bte rouge", "Eclairage"));
        taskData.put("Gaine", Arrays.asList("Eclairage", "Contrôle contre-poids"));
        taskData.put("Cuvette", Arrays.asList("Stop", "Poulie couroie"));



        View fRoot = inflater.inflate(R.layout.fragment_task, container, false);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- SESSION LOAD
        sharedhelper_appareil = new SharedHelper(getActivity(), appareilKey);
        sharedhelper_maintenance = new SharedHelper(getActivity(), maintenanceKey);

        idFrequency = getArguments().getInt("id_frequency");
        labelArg = getArguments().getString("label");
        idCategory = getArguments().getInt("id_category");
        dateFrequency = getArguments().getString("dateFrequency");
        idSyncItem = getArguments().getInt("id_sync_item");
        idMaint = getArguments().getLong("id_maint");

        tvFrequency = fRoot.findViewById(R.id.frequency_name_task);
        tvFrequency.setText(labelArg);

        // -- BUTTON
        btnValidMaintenance = fRoot.findViewById(R.id.btnValidMaintenance);
        btnRetourMaintenance = fRoot.findViewById(R.id.btnRetourMaintenance);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            onAt = format.parse(sharedhelper_appareil.getParam("dateServiceKey"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Cursor cursorNDay = databaseHelper.getFrequencyById(database, String.valueOf(idFrequency));
        if (cursorNDay.moveToFirst()) {
            do {
                nDay = cursorNDay.getInt(cursorNDay.getColumnIndexOrThrow("n_day"));
            } while (cursorNDay.moveToNext());
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFrequencyFormat = formatter.parse(dateFrequency);
        } catch (ParseException e) {
            Log.e("DEBUG_CURRENTDATE", "Aucune date pour la fiche maintenance n'est appelée");
        }

        // -- ANNULED
        rmDate = new RealMadridDate(fRoot.getContext(), fRoot, nDay, onAt, dateFrequencyFormat);
        mapDate = rmDate.calculatePeriod();
//        d_start = mapDate.get("start");
//        d_end = mapDate.get("end");

        Cursor cursorLastRMF = databaseHelper.getLastRMFrequency(database, String.valueOf(idSyncItem), String.valueOf(idFrequency));
        if (cursorLastRMF.moveToFirst()) {
            do {
                idSyncRMF = String.valueOf(cursorLastRMF.getInt(cursorLastRMF.getColumnIndexOrThrow("id_sync")));
                d_start = cursorLastRMF.getString(cursorLastRMF.getColumnIndexOrThrow("startDate"));
                d_start = rmDate.formattedDate(d_start);
                d_end = cursorLastRMF.getString(cursorLastRMF.getColumnIndexOrThrow("endDate"));
                d_end = rmDate.formattedDate(d_end);
            } while (cursorLastRMF.moveToNext());

        }


        // -- CREATE LIST GROUPS TASKS with FREQUENCY ID and CATEGORY ID :: 1

        Cursor cursor = databaseHelper.getGroups(database, String.valueOf(idFrequency), String.valueOf(idCategory));
        groupTitles = new ArrayList<>();
        groupIds = new ArrayList<>();
        groupsMap = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                groupTitles.add(cursor.getString(cursor.getColumnIndexOrThrow("label")));
                groupIds.add(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"))));
                groupsMap.put(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"))), cursor.getString(cursor.getColumnIndexOrThrow("label")));
            } while (cursor.moveToNext());
        }
        // -- END :: 1

        // -- CREATE MAP 2 & 3 :: 2
        taskDataComplete = new HashMap<>();
        Cursor cursor_t;
        for (String idgroup : groupIds) {
            map_II = new ArrayList<>();
            cursor_t = databaseHelper.getTasks(database, String.valueOf(idFrequency), idgroup);
            if (cursor_t.moveToFirst()) {
                do {
                    map_III = new HashMap<>();
                    map_III.put("id", String.valueOf(cursor_t.getInt(cursor_t.getColumnIndexOrThrow("id_sync"))));
                    map_III.put("label", cursor_t.getString(cursor_t.getColumnIndexOrThrow("label")));
                    // -- Check Task done
                    // // -- Check RMFrequency --> id RMF

                    // convert to correct format date
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date_start = null;
                    try {
                        date_start = inputFormat.parse(d_start);
                    } catch (ParseException e) {
                        Log.e("DEBUG_CONVERT_DATE", "Convert start date :: error");
                    }
                    String out_start = outputFormat.format(date_start);
                    Date date_end = null;
                    try {
                        date_end = inputFormat.parse(d_end);
                    } catch (ParseException e) {
                        Log.e("DEBUG_CONVERT_DATE", "Convert end date :: error");
                    }
                    String out_end = outputFormat.format(date_end);

                    Log.i("DEBUG_TASKDONESTARTED", out_start + "|" + out_end + "|" + sharedhelper_appareil.getParam("idsyncKey")+ "|" + String.valueOf(idFrequency));
                    Cursor cursor_rmf = databaseHelper.getRMFrequency(database, out_start, out_end, sharedhelper_appareil.getParam("idsyncKey"), String.valueOf(idFrequency));
                    // -- CONTROLE TO NEW RMF
                    if(cursor_rmf.getCount() == 0) {
                        Log.e("DEBUG_CONTROL_RMF", "CORRECTION NEEDED");
                        // -- CREATE RMF AND REMOTE SELECT RMF
                        Map<String, String> mapRMF = new HashMap<>();
                        mapRMF.put("id_sync", "0");
                        mapRMF.put("startDate", out_start);
                        mapRMF.put("endDate", out_end);
                        mapRMF.put("fk_itemsite", sharedhelper_appareil.getParam("idsyncKey"));
                        mapRMF.put("fk_frequency", String.valueOf(idFrequency));
                        // -- ANNULED
                        //cursor_rmf = databaseHelper.createRMF(database, mapRMF);
                    }
                    Log.e("DEBUG_CONTROL_RMF", "CORRECTION OFF");
                    map_III.put("check", "0");
                    map_III.put("lock", "0");
                    map_III.put("local_realmadridfrequency", "0");
                    map_III.put("local_maintenanceform", String.valueOf(idMaint));
                    if (cursor_rmf.moveToFirst()) {
                        do {
                            map_III.put("local_realmadridfrequency", String.valueOf(cursor_rmf.getInt(cursor_rmf.getColumnIndexOrThrow("id"))));
                            Cursor cursor_taskdone = databaseHelper.getCountTaskDone(database, String.valueOf(idMaint), String.valueOf(cursor_rmf.getInt(cursor_rmf.getColumnIndexOrThrow("id"))), String.valueOf(cursor_t.getInt(cursor_t.getColumnIndexOrThrow("id"))));
                            if(cursor_taskdone.getCount() > 0){
                                if (cursor_taskdone.moveToFirst()) {
                                    do {
                                        map_III.put("check", "1");
                                        Integer idlocalMaintenance = cursor_taskdone.getColumnIndexOrThrow("local_maintenanceform") > 0 ? cursor_taskdone.getColumnIndexOrThrow("local_maintenanceform") : 0;
                                        if(cursor_taskdone.getInt(idlocalMaintenance) != idMaint){
                                            map_III.put("lock", "1");
                                        };
                                    }
                                    while (cursor_taskdone.moveToNext());
                                }
                            }
                        } while (cursor_rmf.moveToNext());
                    }
                    // // -- Take All TaskDone in the RMF but different maintForm
                    map_II.add(map_III);

                } while (cursor_t.moveToNext());
            }

            // -- CONSTRUCT MAP 1 (final) :: 3
            String nameGroup = groupsMap.get(idgroup);
            taskDataComplete.put(nameGroup, map_II);
            // -- END :: 3
        }
        // -- END :: 2

        // -- PRINT CONTENT


        expandableListView = fRoot.findViewById(R.id.groupTask);
        expandableListAdapter = new MyExpandableListAdapter(fRoot.getContext(), groupTitles, taskData, taskDataComplete, dateFrequency);
        expandableListView.setAdapter(expandableListAdapter);

        btnValidMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Object, Object> mapMaintenance = new HashMap<>();
                mapMaintenance.put("idMaint", sharedhelper_maintenance.getParam("idMaintenance"));
                mapMaintenance.put("idSyncRMF", idSyncRMF);
                mapMaintenance.put("idSyncFreq", String.valueOf(idFrequency));
                Bundle bundle = new Bundle();
                bundle.putSerializable("mapMaintenance", (Serializable) mapMaintenance);
                NoteFragment fragment = new NoteFragment();
                fragment.setArguments(bundle);
                fragment.show(getParentFragmentManager(), "NoteFrag");
            }
        });

        btnRetourMaintenance.setOnClickListener(new View.OnClickListener() {
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
}
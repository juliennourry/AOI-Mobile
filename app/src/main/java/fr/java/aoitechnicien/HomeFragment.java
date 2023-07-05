package fr.java.aoitechnicien;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.java.aoitechnicien.Function.CheckCurrentMonth;
import fr.java.aoitechnicien.Function.CheckWithin7Days;
import fr.java.aoitechnicien.Function.RealMadridDate;
import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;
import fr.java.aoitechnicien.Service.ConnexionHelper;


public class HomeFragment extends Fragment {

    private final int CAMERA_PERMISSION_REQUEST_CODE = 100001;
    private final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 200002;
    private BarChart chart;
    private Runnable runThread, updateTimeRunnable;
    CardView c_hs, c_late;
    Bundle bundle;
    Map<String, String> mapBundle, mapDate;
    Cursor cursor_hs, cursor_late, cursor_intervention, cursor_maintenance;
    Integer idUser, count_hs, count_late, count_intervention, count_month_intervention, count_maintenance, count_month_maintenance;
    TextView number_hs, number_late, number_intervention, number_month_intervention, number_maintenance, number_month_maintenance, currentTimeTextView;
    CheckCurrentMonth checkCurrentMonth;
    RealMadridDate rmDate;
    CheckWithin7Days checkWithin7Days;
    String d_start, d_end;
    Handler handlerTime, handlerService;
    ImageView statusService;

    // -- SESSION INFORMATION
    SharedHelper sharedhelper;
    public static final String sessionKey = "sessionKey";
    public static final String loginKey = "loginKey";

    // -- DB
    private static DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRoot = inflater.inflate(R.layout.fragment_home, container, false);

        // -- SESSION LOAD
        sharedhelper = new SharedHelper(getActivity(), sessionKey);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- CALL ELEMENTS
        number_hs = fRoot.findViewById(R.id.number_hs);
        number_late = fRoot.findViewById(R.id.number_late);
        number_intervention = fRoot.findViewById(R.id.number_intervention);
        number_month_intervention = fRoot.findViewById(R.id.number_month_intervention);
        number_maintenance = fRoot.findViewById(R.id.number_maintenance);
        number_month_maintenance = fRoot.findViewById(R.id.number_month_maintenance);
        c_hs = fRoot.findViewById(R.id.card_hs);
        c_late = fRoot.findViewById(R.id.card_late);
        mapBundle = new HashMap<>();
        currentTimeTextView = fRoot.findViewById(R.id.current_time_text_view);
        statusService = fRoot.findViewById(R.id.status_service);

        // -- PERMISSION CAM
        checkCameraPermission(fRoot);

        // Créer un Handler pour mettre à jour l'heure toutes les secondes
        handlerTime = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Créer une instance de Calendar pour récupérer l'heure actuelle
                Calendar calendar = Calendar.getInstance();

                // Créer une instance de SimpleDateFormat pour formatter l'heure
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                // Mettre à jour le TextView avec l'heure actuelle
                currentTimeTextView.setText(timeFormat.format(calendar.getTime()));

                // Planifier la prochaine mise à jour dans 1 seconde
                handlerTime.postDelayed(this, 1000);
            }
        };
        handlerTime.postDelayed(updateTimeRunnable, 0);

        // -- CALL BDD INFORMATIONS
        // // -- CALL IDUSER
        Log.e("DEBUG_USER_SHAREDPREFERENCE", String.valueOf(database));
        idUser = Integer.valueOf(DatabaseHelper.getIdSyncUser(database, sharedhelper.getParam("loginKey")));
        // // -- CALL INTERVENTION
        cursor_intervention = DatabaseHelper.getLocalIntervention(database, idUser);
        count_intervention = cursor_intervention.getCount();
        number_intervention.setText(String.valueOf(count_intervention));
        count_month_intervention = 0;
        if (cursor_intervention.moveToFirst()) {
            do {
                checkCurrentMonth = new CheckCurrentMonth( cursor_intervention.getString(cursor_intervention.getColumnIndexOrThrow("startDate")));
                Boolean checkDate = checkCurrentMonth.checkMonthDate();
                count_month_intervention++;
            } while (cursor_intervention.moveToNext());
        }
        number_month_intervention.setText(String.valueOf(count_month_intervention));

        // // -- CALL MAINTENANCE
        cursor_maintenance = DatabaseHelper.getLocalMaintenance(database, idUser);
        count_maintenance = cursor_maintenance.getCount();
        number_maintenance.setText(String.valueOf(count_maintenance));
        count_month_maintenance = 0;
        if (cursor_maintenance.moveToFirst()) {
            do {
                checkCurrentMonth = new CheckCurrentMonth( cursor_maintenance.getString(cursor_maintenance.getColumnIndexOrThrow("startDate")));
                Boolean checkDate = checkCurrentMonth.checkMonthDate();
                count_month_maintenance++;
            } while (cursor_maintenance.moveToNext());
        }
        number_month_maintenance.setText(String.valueOf(count_month_maintenance));
        // // -- CALL HS APPAREIL
        cursor_hs = DatabaseHelper.getHS(database, idUser);
        count_hs = cursor_hs.getCount();
        number_hs.setText(String.valueOf(count_hs));
        // // -- CALL RETARD
        count_late = 0;
        Cursor cursor_late = DatabaseHelper.getRetard(database, idUser);
        if (cursor_late.moveToFirst()) {
            do {

                Date currentDate = new Date();
                Integer appidsync = cursor_late.getInt(cursor_late.getColumnIndexOrThrow("apps_idsync"));
                String appLabel = cursor_late.getString(cursor_late.getColumnIndexOrThrow("apps_label"));
                String appOnAt = cursor_late.getString(cursor_late.getColumnIndexOrThrow("apps_onAt"));
                String siteLabel = cursor_late.getString(cursor_late.getColumnIndexOrThrow("site_label"));
                Integer nDay = cursor_late.getInt(cursor_late.getColumnIndexOrThrow("nday"));
                Integer fId = cursor_late.getInt(cursor_late.getColumnIndexOrThrow("fid"));
                String freqLabel = cursor_late.getString(cursor_late.getColumnIndexOrThrow("freq_label"));

                // -- convert onAt to Date

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date onAt = new Date();
                try {
                    onAt = format.parse(appOnAt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                // -- CHECK IF LATE
                rmDate = new RealMadridDate(fRoot.getContext(), fRoot, nDay, onAt, currentDate);
                mapDate = rmDate.calculatePeriod();

                d_start = mapDate.get("start");
                d_end = mapDate.get("end");

                // -- CHECK IF LATE
                Boolean allChecked = false;
                Integer idLocalRMF = 0;
                Integer idSyncFreq = 0;
                List<String> tMaint, tDone;
                tMaint = new ArrayList<String>();
                tDone = new ArrayList<String>();


                SimpleDateFormat formator = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
                Date dd_start = null;
                try {
                    dd_start = inputFormat.parse(d_start);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dd_end = null;
                try {
                    dd_end = inputFormat.parse(d_end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                String f_d_start = formator.format(dd_start);
                String f_d_end = formator.format(dd_end);

                Cursor cursorRMF = databaseHelper.getRMFrequency(database, f_d_start, f_d_end, String.valueOf(appidsync), String.valueOf(fId));
                if (cursorRMF.getCount() > 0) {
                    if (cursorRMF.moveToFirst()) {
                        do {
                            idLocalRMF = cursorRMF.getInt(cursorRMF.getColumnIndexOrThrow("id"));
                            idSyncFreq = cursorRMF.getInt(cursorRMF.getColumnIndexOrThrow("fk_frequency"));
                        } while (cursorRMF.moveToNext());
                    }
                    // -- GET LIST MAINTTASK
                    Cursor cursorTask = databaseHelper.getTasksByFreq(database, String.valueOf(idSyncFreq));
                    if (cursorTask.getCount() > 0) {
                        if (cursorTask.moveToFirst()) {
                            do {
                                tMaint.add(String.valueOf(cursorTask.getInt(cursorTask.getColumnIndexOrThrow("id_sync"))));
                            } while (cursorTask.moveToNext());
                        }
                    } else {
                        allChecked = false;
                    }
                    // -- GET LIST TASKDONE
                    Cursor cursorTDRMF = databaseHelper.getTDRMF(database, String.valueOf(idLocalRMF));
                    if (cursorTDRMF.getCount() > 0) {
                        if (cursorTDRMF.moveToFirst()) {
                            do {
                                tDone.add(String.valueOf(cursorTDRMF.getInt(cursorTDRMF.getColumnIndexOrThrow("fk_mainttask"))));
                            } while (cursorTDRMF.moveToNext());
                        }
                    } else {
                        allChecked = false;
                    }
                    if(allChecked) {
                        if(tDone.containsAll(tMaint)){
                            allChecked = true;
                        }
                    }
                } else {
                    allChecked = false;
                }

                if(!allChecked) {
                    checkWithin7Days = new CheckWithin7Days(currentDate, d_end);
                    Boolean check7Days = checkWithin7Days.check7Days();
                    if (check7Days) {
                        count_late++;
                    }
                }
            } while (cursor_late.moveToNext());
        }
        number_late.setText(String.valueOf(count_late));

        // -- Handler Thread Service
        handlerService = new Handler();
        runThread = new Runnable() {
            @Override
            public void run() {
                Boolean connexionAlive = ConnexionHelper.isAliveThread();
                if(connexionAlive){
                    statusService.setImageResource(R.drawable.ic_baseline_phonelink_ring_24);
                } else {
                    statusService.setImageResource(R.drawable.ic_baseline_phonelink_erase_24);
                }

                handlerService.postDelayed(this, 10000);
            }
        };
        handlerService.postDelayed(runThread, 10000);

        // -- Click Actions
        c_hs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapBundle.put("title", (String) getString(R.string.h_s));
                mapBundle.put("type", (String) "HS");
                mapBundle.put("user", (String) sharedhelper.getParam("loginKey"));
                bundle = new Bundle();
                bundle.putSerializable("mapBundle", (Serializable) mapBundle);
                ListFragment fragment = new ListFragment();
                fragment.setArguments(bundle);
                fragment.show(getParentFragmentManager(), "HSFrag");
            }
        });
        c_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapBundle.put("title", (String) getString(R.string.late_maintenance));
                mapBundle.put("type", (String) "MAINTENANCE");
                mapBundle.put("user", (String) sharedhelper.getParam("loginKey"));
                bundle = new Bundle();
                bundle.putSerializable("mapBundle", (Serializable) mapBundle);
                ListFragment fragment = new ListFragment();
                fragment.setArguments(bundle);
                fragment.show(getParentFragmentManager(), "MAINTENANCEFrag");
            }
        });

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
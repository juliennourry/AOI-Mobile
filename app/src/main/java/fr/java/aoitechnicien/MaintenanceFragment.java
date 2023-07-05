package fr.java.aoitechnicien;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.java.aoitechnicien.Function.ConvertFrenchDate;
import fr.java.aoitechnicien.Function.MyExpandableListAdapter;
import fr.java.aoitechnicien.Function.RealMadridDate;
import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.Function.ToastHelper;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class MaintenanceFragment extends Fragment {

    MyExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView, expandableListView2, expandableListView3 ;
    LinearLayout linear_layout_maintenance, dated;
    Map<String, String> mapDate, mapMaintenance;
    RealMadridDate rmDate;
    Date onAt, currentDate;
    ToastHelper toastHelper;
    String d_start, d_end, dateArg, idSyncRMF, idSyncFreq;
    Long idMaint;
    Button btnValidMaintenance, btnRetourMaintenance;
    Bundle bundle;

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
    public static final String idMaintenance = "idMaintenance";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Map<Integer, String> frequencies = new HashMap<>();
//        frequencies.put(1, "Toutes les 6 semaines");
//        frequencies.put(2, "Visite semestrielle");
//        frequencies.put(3, "Visite annuelle");

        View fRoot = inflater.inflate(R.layout.fragment_maintenance, container, false);

        // -- TOAST PREPARED
        toastHelper = new ToastHelper(getActivity());

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- SESSION LOAD
        sharedhelper_appareil = new SharedHelper(getActivity(), appareilKey);
        sharedhelper_maintenance = new SharedHelper(getActivity(), maintenanceKey);

        // -- CONTENT DYNAMIC LOADER
        linear_layout_maintenance = fRoot.findViewById(R.id.linear_layout_maintenance);

        // -- BUTTON
        btnValidMaintenance = fRoot.findViewById(R.id.btnValidMaintenance);
        btnRetourMaintenance = fRoot.findViewById(R.id.btnRetourMaintenance);

        // -- Argument DATE CURRENT OR maintenance DATE
        dateArg = getArguments().getString("date");
        idMaint = getArguments().getLong("id_maint");

        sharedhelper_maintenance.stockParam(idMaintenance, String.valueOf(idMaint));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            onAt = format.parse(sharedhelper_appareil.getParam("dateServiceKey"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Cursor cursor = databaseHelper.getFrequency(database, sharedhelper_appareil.getParam("fkCategoryKey"));
        if (cursor.moveToFirst()) {
            do {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    currentDate = formatter.parse(dateArg);
                } catch (ParseException e) {
                    Log.e("DEBUG_CURRENTDATE", "Aucune date pour la fiche maintenance n'est appelée");
                }
                // -- ANNULED
                rmDate = new RealMadridDate(fRoot.getContext(), fRoot, cursor.getInt(cursor.getColumnIndexOrThrow("n_day")), onAt, currentDate);
                mapDate = rmDate.calculatePeriod();
//                d_start = mapDate.get("start");
//                d_end = mapDate.get("end");
                idSyncFreq = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                Cursor cursorLastRMF = databaseHelper.getLastRMFrequency(database, sharedhelper_appareil.getParam("idsyncKey"), String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"))));
                if (cursorLastRMF.moveToFirst()) {
                    do {
                        idSyncRMF = String.valueOf(cursorLastRMF.getInt(cursorLastRMF.getColumnIndexOrThrow("id_sync")));
                        d_start = cursorLastRMF.getString(cursorLastRMF.getColumnIndexOrThrow("startDate"));
                        d_start = rmDate.formattedDate(d_start);
                        d_end = cursorLastRMF.getString(cursorLastRMF.getColumnIndexOrThrow("endDate"));
                        d_end = rmDate.formattedDate(d_end);
                    } while (cursorLastRMF.moveToNext());
                }


                // ------
                CardView cardView = new CardView(fRoot.getContext());
                cardView.setRadius(10);
                cardView.setCardBackgroundColor(Color.WHITE);
                Object[] tags = new Object[6];
                tags[0] = cursor.getInt(cursor.getColumnIndexOrThrow("id_sync"));
                tags[1] = cursor.getString(cursor.getColumnIndexOrThrow("label"));
                tags[2] = Integer.valueOf(sharedhelper_appareil.getParam("fkCategoryKey"));
                tags[3] = dateArg;
                tags[4] = Integer.valueOf(sharedhelper_appareil.getParam("idsyncKey"));
                tags[5] = idMaint;

                cardView.setTag(tags);

                // -- HORIZONTAL CONTENT
                LinearLayout linearLayoutH = new LinearLayout(fRoot.getContext());
                linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);

                // -- VERTICAL CONTENT
                LinearLayout linearLayoutV = new LinearLayout(fRoot.getContext());
                linearLayoutV.setOrientation(LinearLayout.VERTICAL);
                linearLayoutV.setPadding(20, 20, 20, 20);
                LinearLayout.LayoutParams layoutParamsV = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.8f
                );
                linearLayoutV.setLayoutParams(layoutParamsV);

                // -- VERTICAL CONTENT FOR ICON
                LinearLayout linearLayoutIcon = new LinearLayout(fRoot.getContext());
                linearLayoutIcon.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParamsIcon = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                linearLayoutIcon.setLayoutParams(layoutParamsIcon);
                ImageView imageView = new ImageView(fRoot.getContext());
                imageView.setImageResource(R.drawable.ic_baseline_arrow_right_24);
                linearLayoutIcon.addView(imageView);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 55, 0, 0);
                layoutParams.gravity = Gravity.CENTER;
                imageView.setLayoutParams(layoutParams);

                // -- TITLE FREQUENCY
                TextView titleTextView = new TextView(fRoot.getContext());
                titleTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("label")));
                titleTextView.setTextSize(20);
                titleTextView.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.theme_color));

                // -- DATES INFORMATIONS
                TextView datesTextView = new TextView(fRoot.getContext());
                datesTextView.setText(d_start + " au " + d_end);
                datesTextView.setTextSize(12);
                datesTextView.setTextColor(Color.GRAY);

                // -- CONSTRUCT LAYOUTS
                linearLayoutV.addView(titleTextView);
                linearLayoutV.addView(datesTextView);
                linearLayoutH.addView(linearLayoutV);
                linearLayoutH.addView(linearLayoutIcon);
                cardView.addView(linearLayoutH);

                // -- CARDVIEW PARAMS
                LinearLayout.LayoutParams layoutParamsCard = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParamsCard.setMargins(20, 20, 20, 20);
                cardView.setLayoutParams(layoutParamsCard);

                // -- ADD TO PARENT CONTENT
                linear_layout_maintenance.addView(cardView);

                // -- EVENT CLICK
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object[] viewTags = (Object[]) v.getTag();
                        Integer tag1;
                        String tag2;
                        Integer tag3;
                        String tag4;
                        Integer tag5;
                        Long tag6;
                        tag1 = 0;
                        tag2 = "no label";
                        tag3 = 0;
                        tag4 = "1970-12-12 00:00:00";
                        tag5 = 0;
                        tag6 = 0L;
                        if (viewTags != null && viewTags.length > 0) {
                            tag1 = (Integer) viewTags[0];
                            tag2 = (String) viewTags[1];
                            tag3 = (Integer) viewTags[2];
                            tag4 = (String) viewTags[3];
                            tag5 = (Integer) viewTags[4];
                            tag6 = (Long) viewTags[5];
                        }
                        sharedhelper_maintenance.stockParam(frequencyKey, String.valueOf(tag1));
                        replaceFragment(new TaskFragment(), tag1, tag2, tag3, tag4, tag5, tag6);
                    }
                });

            } while (cursor.moveToNext());
        }
        //createTextViewsFromMap(frequencies, fRoot);

        btnValidMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapMaintenance = new HashMap<>();
                mapMaintenance.put("idMaint", sharedhelper_maintenance.getParam("idMaintenance"));
                mapMaintenance.put("idSyncRMF", idSyncRMF);
                mapMaintenance.put("idSyncFreq", idSyncFreq);
                bundle = new Bundle();
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

    private void replaceFragment(Fragment fragment, Integer id, String label, Integer id_category, String dateFrequency, Integer id_sync_item, Long id_maint) {

        Bundle bundle = new Bundle();
        bundle.putInt("id_frequency", id);
        bundle.putString("label", label);
        bundle.putInt("id_category", id_category);
        bundle.putString("dateFrequency", dateFrequency);
        bundle.putInt("id_sync_item", id_sync_item);
        bundle.putLong("id_maint", id_maint);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_item, fragment);
        fragmentTransaction.addToBackStack("03");
        fragmentTransaction.commit();
        fragmentManager.popBackStack("03", 0);
    }

    private void createTextViewsFromMap(Map<Integer, String> freq, View fRoot) {
        LinearLayout layout = fRoot.findViewById(R.id.linear_layout_frequency);

        for (Map.Entry<Integer, String> entry : freq.entrySet()) {
            TextView textView = new TextView(fRoot.getContext());
            textView.setText(entry.getValue());
            textView.setId(entry.getKey());
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.theme_color));
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new TaskFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", entry.getKey());
                    bundle.putString("label", entry.getValue());
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout_item,fragment);
                    fragmentTransaction.addToBackStack("02");
                    fragmentTransaction.commit();
                    fragmentManager.popBackStack("02", 0);
                }
            });


            layout.addView(textView);
        }
    }

}
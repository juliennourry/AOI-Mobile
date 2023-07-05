package fr.java.aoitechnicien;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.java.aoitechnicien.Function.CheckWithin7Days;
import fr.java.aoitechnicien.Function.RealMadridDate;
import fr.java.aoitechnicien.Requester.DatabaseHelper;

public class ListFragment extends DialogFragment {

    Bundle bundle;
    Map<String, String> mapBundle, mapDate;
    LinearLayout listLayout;
    TextView tableTitleList;
    Integer idUser;
    String title, type, d_start, d_end;
    Cursor cursor;
    CardView cardView;
    RealMadridDate rmDate;
    CheckWithin7Days checkWithin7Days;

    // -- DB
    private static DatabaseHelper databaseHelper;

    @Override
    public void onStart() {
        super.onStart();

        // Set the window dimensions to match the screen size
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(params);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRoot = inflater.inflate(R.layout.fragment_list, container, false);

        // -- START CREATE DB
        databaseHelper = new DatabaseHelper(fRoot.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // -- CALL ELEMENT
        bundle = getArguments();
        mapBundle = (Map<String, String>) bundle.getSerializable("mapBundle");
        listLayout = fRoot.findViewById(R.id.layout_list);
        tableTitleList = fRoot.findViewById(R.id.title_list);
        idUser = Integer.valueOf(DatabaseHelper.getIdSyncUser(database, mapBundle.get("user")));
        title = String.valueOf(mapBundle.get("title"));
        type = String.valueOf(mapBundle.get("type"));


        // -- CHANGE TITLE
        tableTitleList.setText(title);

        if (type.equals("HS")) {
            // -- DATA HS APPAREIL
            Cursor cursor = DatabaseHelper.getHS(database, idUser);
            if (cursor.moveToFirst()) {
                do {
                    String appLabel = cursor.getString(cursor.getColumnIndexOrThrow("apps_label"));
                    String siteLabel = cursor.getString(cursor.getColumnIndexOrThrow("site_label"));
                    String dateString = cursor.getString(cursor.getColumnIndexOrThrow("off_start"));

                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());
                    Date inputDate = null;
                    try {
                        inputDate = inputDateFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
                    String offtimeDate = outputDateFormat.format(inputDate);


                    cardView = new CardView(fRoot.getContext());
                    cardView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    cardView.setCardElevation(10f);
                    cardView.setRadius(10f);
                    cardView.setCardBackgroundColor(ContextCompat.getColor(fRoot.getContext(), R.color.white));
                    cardView.setMaxCardElevation(12f);
                    cardView.setPreventCornerOverlap(true);
                    cardView.setUseCompatPadding(true);
                    cardView.setId(R.id.card_intervention);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.weight = 1;
                    params.setMargins(10, 10, 10, 10);
                    cardView.setLayoutParams(params);

                    LinearLayout linearLayoutH = new LinearLayout(fRoot.getContext());
                    linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutH.setLayoutParams(params);

                    LinearLayout linearLayoutV = new LinearLayout(fRoot.getContext());
                    linearLayoutV.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                    linearLayoutV.setOrientation(LinearLayout.VERTICAL);
                    linearLayoutV.setGravity(Gravity.TOP | Gravity.LEFT);
                    linearLayoutV.setPadding(10, 10, 10, 10);

                    LinearLayout linearLayoutD = new LinearLayout(fRoot.getContext());
                    linearLayoutD.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                    linearLayoutD.setOrientation(LinearLayout.VERTICAL);
                    linearLayoutD.setPadding(0, 10, 0, 0);

                    // -- APPAREIL NAME
                    TextView textName = new TextView(fRoot.getContext());
                    textName.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    textName.setText(appLabel);
                    textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                    textName.setTypeface(textName.getTypeface(), Typeface.BOLD);
                    textName.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));

                    // -- SITE NAME
                    TextView textSite = new TextView(fRoot.getContext());
                    textSite.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    textSite.setText(siteLabel);
                    textSite.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
                    textSite.setTypeface(textSite.getTypeface(), Typeface.BOLD);
                    textSite.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));


                    // -- SEPARATOR DATE
                    TextView textSep = new TextView(fRoot.getContext());
                    textSep.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    textSep.setText("Depuis le");
                    textSep.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
                    textSep.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));

                    // -- DATE HS
                    TextView textDate = new TextView(fRoot.getContext());
                    textName.setTypeface(textName.getTypeface(), Typeface.BOLD);
                    textDate.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    ));
                    textDate.setText(offtimeDate);
                    textDate.setGravity(Gravity.TOP);

                    linearLayoutD.addView(textSep);
                    linearLayoutD.addView(textDate);
                    linearLayoutV.addView(textName);
                    linearLayoutV.addView(textSite);

                    linearLayoutH.addView(linearLayoutV);
                    linearLayoutH.addView(linearLayoutD);

                    cardView.addView(linearLayoutH);

//                    listLayout.removeAllViews();
                    listLayout.addView(cardView);

                } while (cursor.moveToNext());
            }
        } else {
            // -- DATA RETARD APPAREIL
            Cursor cursor = DatabaseHelper.getRetard(database, idUser);
            if (cursor.moveToFirst()) {
                do {

                    Date currentDate = new Date();

                    Integer appidsync = cursor.getInt(cursor.getColumnIndexOrThrow("apps_idsync"));
                    String appLabel = cursor.getString(cursor.getColumnIndexOrThrow("apps_label"));
                    String appOnAt = cursor.getString(cursor.getColumnIndexOrThrow("apps_onAt"));
                    String siteLabel = cursor.getString(cursor.getColumnIndexOrThrow("site_label"));
                    Integer nDay = cursor.getInt(cursor.getColumnIndexOrThrow("nday"));
                    Integer fId = cursor.getInt(cursor.getColumnIndexOrThrow("fid"));
                    String freqLabel = cursor.getString(cursor.getColumnIndexOrThrow("freq_label"));

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
                        // -- CHECK 7 DAYS
                        checkWithin7Days = new CheckWithin7Days(currentDate, d_end);
                        Boolean check7Days = checkWithin7Days.check7Days();
                        if (check7Days) {
                            // -- SHOW CARDS LATE
                            cardView = new CardView(fRoot.getContext());
                            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            cardView.setCardElevation(10f);
                            cardView.setRadius(10f);
                            cardView.setCardBackgroundColor(ContextCompat.getColor(fRoot.getContext(), R.color.white));
                            cardView.setMaxCardElevation(12f);
                            cardView.setPreventCornerOverlap(true);
                            cardView.setUseCompatPadding(true);
                            cardView.setId(R.id.card_intervention);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.weight = 1;
                            params.setMargins(10, 10, 10, 10);
                            cardView.setLayoutParams(params);

                            LinearLayout linearLayoutH = new LinearLayout(fRoot.getContext());
                            linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayoutH.setLayoutParams(params);

                            LinearLayout linearLayoutV = new LinearLayout(fRoot.getContext());
                            linearLayoutV.setLayoutParams(new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                            linearLayoutV.setOrientation(LinearLayout.VERTICAL);
                            linearLayoutV.setGravity(Gravity.TOP | Gravity.LEFT);
                            linearLayoutV.setPadding(10, 10, 10, 10);

                            LinearLayout linearLayoutD = new LinearLayout(fRoot.getContext());
                            linearLayoutD.setLayoutParams(new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                            linearLayoutD.setOrientation(LinearLayout.VERTICAL);
                            linearLayoutD.setPadding(0, 0, 0, 0);

                            // -- APPAREIL NAME
                            TextView textName = new TextView(fRoot.getContext());
                            textName.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            textName.setText(appLabel);
                            textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                            textName.setTypeface(textName.getTypeface(), Typeface.BOLD);
                            textName.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));

                            // -- SITE NAME
                            TextView textSite = new TextView(fRoot.getContext());
                            textSite.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            textSite.setText(siteLabel);
                            textSite.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
                            textSite.setTypeface(textSite.getTypeface(), Typeface.BOLD);
                            textSite.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));


                            // -- FREQUENCY
                            TextView textFreq = new TextView(fRoot.getContext());
                            textFreq.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            textFreq.setText(freqLabel);
                            textFreq.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
                            textFreq.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));

                            // -- SEPARATOR DATE
                            TextView textSep = new TextView(fRoot.getContext());
                            textSep.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            textSep.setText("Date limite le");
                            textSep.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
                            textSep.setTextColor(ContextCompat.getColor(fRoot.getContext(), R.color.gray));

                            // -- DATE HS
                            TextView textDate = new TextView(fRoot.getContext());
                            textName.setTypeface(textName.getTypeface(), Typeface.BOLD);
                            textDate.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT
                            ));
                            textDate.setText(d_end);
                            textDate.setGravity(Gravity.TOP);

                            linearLayoutD.addView(textFreq);
                            linearLayoutD.addView(textSep);
                            linearLayoutD.addView(textDate);
                            linearLayoutV.addView(textName);
                            linearLayoutV.addView(textSite);

                            linearLayoutH.addView(linearLayoutV);
                            linearLayoutH.addView(linearLayoutD);

                            cardView.addView(linearLayoutH);
//
//                            listLayout.removeAllViews();
                            listLayout.addView(cardView);
                        } else {
                            Log.e("DEBUG_7_DAYS", "FALSE");
                        }
                    }
                    Log.e("DEBUG_COUNT_POS", "--1--");
                } while (cursor.moveToNext());
            }
        }




        return fRoot;
    }
}
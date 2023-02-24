package fr.java.aoitechnicien.Function;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.java.aoitechnicien.R;

public class CalendarPicker {

    private Context context;
    private View viewer;
    private Calendar date;
    private Boolean boolDate = false;
    TextView dateChange;

    public CalendarPicker(Context context, View viewer) {
        this.context = context;
        this.viewer = viewer;
    }

    public void showDateTimePicker(String type) {
        final Calendar currentDate = Calendar.getInstance();
        //currentDate.set(year,monthOfYear,dayOfMonth,hourOfDay,minute,0);
        date = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                        // Get the date in the desired format
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String selectedDateTime = sdf.format(date.getTime());
                        Log.e("DEBUG_SHOWDATE", selectedDateTime);

                        if(type == "01") {
                            dateChange = (TextView) viewer.findViewById(R.id.date_start_intervention);
                        } else if(type == "02") {
                            dateChange = (TextView) viewer.findViewById(R.id.date_end_intervention);
                        }
                        dateChange.setText(selectedDateTime);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }



}

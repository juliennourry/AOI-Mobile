package fr.java.aoitechnicien.Function;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RealMadridDate {
    private Context context;
    private View viewer;
    private Date currentDate, onAt;
    private long recurrence;
    Map<String, String> mapDate;

    public RealMadridDate(Context context, View viewer, long recurrence, Date onAt, Date currentDate) {
        this.context = context;
        this.viewer = viewer;
        this.recurrence = recurrence;
        this.onAt = onAt;
        this.currentDate = currentDate;
    }

    public Map<String, String> calculatePeriod() {
        mapDate = new HashMap<>();
        long diff = currentDate.getTime() - onAt.getTime();
        if(diff <= 86400000){
            diff = 86400000;
        }

        long days = (long) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        long periods_start = days / recurrence;
        long periods_end = (long) Math.ceil((double) days / (double) recurrence);

        Date periodStartDate = new Date((long) onAt.getTime() + recurrence * periods_start * 24 * 60 * 60 * 1000);
        Date periodEndDate = new Date((long) onAt.getTime() + recurrence * periods_end * 24 * 60 * 60 * 1000);

        SimpleDateFormat outputFormatDate = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr", "FR"));
        String outputStartDate = outputFormatDate.format(periodStartDate);
        String outputEndDate = outputFormatDate.format(periodEndDate);
        mapDate.put("start", outputStartDate);
        mapDate.put("end", outputEndDate);

        return mapDate;
    }

    public String formattedDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormatDate = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr", "FR"));

        try {
            Date parsedDate = inputFormat.parse(date);
            String outputDate = outputFormatDate.format(parsedDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parse exception accordingly
        }

        return "";
    }

}

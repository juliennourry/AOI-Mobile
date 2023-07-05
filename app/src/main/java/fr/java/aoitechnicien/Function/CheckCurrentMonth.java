package fr.java.aoitechnicien.Function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class CheckCurrentMonth {

    private String compareDate;

    public CheckCurrentMonth(String compareDate) {
        this.compareDate = compareDate;
    }

    public Boolean checkMonthDate() {
        Boolean checkedDate = false;
        String dateString = compareDate; // example date string
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.parse(dateString, formatter);
        }
        LocalDate now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDate.now();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean isCurrentMonth = date.getMonth().equals(now.getMonth())
                    && date.getYear() == now.getYear();
        }

        return checkedDate;
    }

}

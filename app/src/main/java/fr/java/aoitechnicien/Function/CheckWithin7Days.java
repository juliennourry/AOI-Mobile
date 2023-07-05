package fr.java.aoitechnicien.Function;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class CheckWithin7Days {

    private Date customDate;
    private String fixedDate;

    public CheckWithin7Days(Date customDate, String fixedDate) {

        this.customDate = customDate;
        this.fixedDate = fixedDate;
    }

    public Boolean check7Days() {
        Boolean isWithin7Days = false;

        // Custom date
        LocalDate customDateCheck = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            customDateCheck = customDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        // Fixed date
        LocalDate fixedDateCheck = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("fr"));
            fixedDateCheck = LocalDate.parse(fixedDate, formatter);
        }

        // Check if the custom date is within 7 days of the fixed date
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isWithin7Days = ChronoUnit.DAYS.between(customDateCheck, fixedDateCheck) <= 7 && ChronoUnit.DAYS.between(customDateCheck, fixedDateCheck) >= 0;
        }

        return isWithin7Days;
    }

}

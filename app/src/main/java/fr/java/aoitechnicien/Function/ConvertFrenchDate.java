package fr.java.aoitechnicien.Function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConvertFrenchDate {

    private String inputDateToConvert;

    public ConvertFrenchDate(String inputDateToConvert) {
        this.inputDateToConvert = inputDateToConvert;
    }

    public String convertDate() {
        String outputDateString = "Aucune date de mise en service";
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());
            Date inputDate = inputDateFormat.parse(inputDateToConvert);

            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy 'à' HH'h'mm", Locale.FRENCH);
            outputDateString = outputDateFormat.format(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDateString;
    }

}

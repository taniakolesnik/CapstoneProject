package uk.co.taniakolesnik.capstoneproject.ui_tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HelpMethods {

    public static String getUserFreindlyDate (String dateOld){
        Date date = new Date();
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            date = oldDateFormat.parse(dateOld);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
        return newDateFormat.format(date);
    }
}

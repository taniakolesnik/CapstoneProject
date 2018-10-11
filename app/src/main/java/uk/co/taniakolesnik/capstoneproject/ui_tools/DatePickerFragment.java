package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DateListener mListener;

    public interface DateListener{
        void setDate (int year, int month, int day);
    }

    public void setListener(DateListener dateListener) {
        mListener = dateListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (savedInstanceState!=null){
                Timber.i("onCreateDialog savedInstanceState has workshop_date_for_date_dialog_bundle_key ");
                // Use saved date as the default date in the picker
                String dateString = savedInstanceState.getString(getString(R.string.workshop_date_for_date_dialog_bundle_key));
                Timber.i("onCreateDialog dateString is %s ", dateString);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                try {
                    Date date = simpleDateFormat.parse(dateString);
                    String parsedYear = new SimpleDateFormat("yyyy", Locale.UK).format(date);
                    String parsedMonth = new SimpleDateFormat("MM", Locale.UK).format(date);
                    String parsedDay = new SimpleDateFormat("dd", Locale.UK).format(date);

                    year =  Integer.parseInt(parsedDay);
                    month =  Integer.parseInt(parsedMonth);
                    day =  Integer.parseInt(parsedYear);

                    Timber.i("onCreateDialog year month day %d %d2 %d3 ", year, month, day);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (mListener!=null){
            mListener.setDate(year, month + 1, day);
        }
    }
}
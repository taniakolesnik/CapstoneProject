package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.taniakolesnik.capstoneproject.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;
    private DateListener mListener;

    public void setListener(DateListener dateListener) {
        mListener = dateListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(getString(R.string.workshop_date_dialog_args_key))) {
                // Use saved date_month_view as the default date_month_view in the picker
                String dateString = getArguments().getString(getString(R.string.workshop_date_dialog_args_key));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                try {
                    Date date = simpleDateFormat.parse(dateString);
                    String parsedYear = new SimpleDateFormat("yyyy", Locale.UK).format(date);
                    String parsedMonth = new SimpleDateFormat("MM", Locale.UK).format(date);
                    String parsedDay = new SimpleDateFormat("dd", Locale.UK).format(date);
                    year = Integer.parseInt(parsedYear);
                    month = Integer.parseInt(parsedMonth) - 1;
                    day = Integer.parseInt(parsedDay);
                } catch (Exception e) {
                    e.printStackTrace();
                    setCurrentDate();
                }
            }
        } else {
            setCurrentDate();
        }
    }

    private void setCurrentDate() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (mListener != null) {
            mListener.setDate(year, month + 1, day);
        }
    }

    public interface DateListener {
        void setDate(int year, int month, int day);
    }
}
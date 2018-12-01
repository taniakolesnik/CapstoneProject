package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private int hour;
    private int minute;
    private TimeListener mListener;

    public interface TimeListener {
        void setTime(int hourOfDay, int minute);
    }

    public void setListener(TimeListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(getString(R.string.workshop_time_dialog_args_key))) {
                // Use saved date_month_view as the default date_month_view in the picker
                String timeString = getArguments().getString(getString(R.string.workshop_time_dialog_args_key));
                Timber.i("timeString is %s", timeString);
                try {
                    LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
                    hour = localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
                    minute = localTime.get(ChronoField.MINUTE_OF_HOUR);
                } catch (Exception e) {
                    e.printStackTrace();
                    setCurrentTime();
                }
            }
        } else {
            setCurrentTime();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mListener != null) mListener.setTime(hourOfDay, minute);
    }

    private void setCurrentTime(){
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }
}

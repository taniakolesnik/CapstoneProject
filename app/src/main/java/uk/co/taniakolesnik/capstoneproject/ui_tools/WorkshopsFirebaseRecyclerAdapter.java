package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.activities.WorkshopDetailsActivity;
import uk.co.taniakolesnik.capstoneproject.models.User;
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;

public class WorkshopsFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Workshop, WorkshopViewHolder> {

    private Context mContext;
    private ProgressBar progressBar;
    private User loggedUser;

    public WorkshopsFirebaseRecyclerAdapter(FirebaseRecyclerOptions<Workshop> options, Context context, ProgressBar view) {
        super(options);
        mContext = context;
        progressBar = view;

        TinyDB tinyDB = new TinyDB(mContext);
        loggedUser = new User();

        try {
            loggedUser = tinyDB.getObject(mContext.getString(R.string.firebase_user_tinyDb_key), User.class);
        } catch (NullPointerException e){
            Timber.i(e);
        }

    }

    @Override
    public void onDataChanged() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final WorkshopViewHolder holder, final int position, @NonNull final Workshop model) {

        final Workshop workshop = getItem(position);
        final String id = getRef(position).getKey();

        holder.time.setText(mContext.getString(R.string.day_time_string, getDayOfWeek(workshop.getDate()), workshop.getTime()));
        holder.date.setText(getDate(workshop.getDate()));
        holder.month.setText(getMonth(workshop.getDate()));
        holder.name.setText(workshop.getName());
        holder.address.setText(workshop.getAddress());

        try {
            Map<String, User> users = workshop.getValue();
            for (User user : users.values()) {
                if (user.getEmail().equals(loggedUser.getEmail())){
                   holder.attendingBox.setBackgroundColor(mContext.getColor(R.color.colorWhite));
                   holder.attendingBox.setBackground(mContext.getDrawable(R.drawable.attendance_status_shape_white));
                   holder.attendingImage.setVisibility(View.VISIBLE);
                   holder.attendingInfoTextView.setVisibility(View.GONE);
                   holder.cardView.setCardBackgroundColor(mContext.getColor(R.color.colorAccent));
                   holder.dateLayout.setBackground(mContext.getDrawable(R.drawable.date_icon_layout_border_shape));
                   holder.time.setTextColor(mContext.getColor(R.color.colorWhite));
                   holder.name.setTextColor(mContext.getColor(R.color.colorWhite));
                   holder.address.setTextColor(mContext.getColor(R.color.colorWhite));
               }
            }

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyDB tinyDB = new TinyDB(mContext);
                tinyDB.putObject(mContext.getString(R.string.workshop_tinydb_key), workshop);
                Intent intent = new Intent(mContext, WorkshopDetailsActivity.class);
                intent.putExtra(mContext.getString(R.string.open_workshop_details_intent_key),
                        WorkshopDetailsActivity.INTENT_OPEN_UPDATE_WORKSHOP_DETAILS);
                intent.putExtra(mContext.getString(R.string.current_workshop_id_key), id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public WorkshopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workshop_rv_item, viewGroup, false);
        return new WorkshopViewHolder(itemView);
    }

    private String getDayOfWeek(String dateOld){
        Date date = new Date();
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            date = oldDateFormat.parse(dateOld);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("EEEE", Locale.UK);
        return newDateFormat.format(date);
    }


    private String getMonth(String dateOld){
        Date date = new Date();
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            date = oldDateFormat.parse(dateOld);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMM", Locale.UK);
        return newDateFormat.format(date);
    }

    private String getDate(String dateOld){
        Date date = new Date();
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            date = oldDateFormat.parse(dateOld);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd", Locale.UK);
        return newDateFormat.format(date);
    }


}

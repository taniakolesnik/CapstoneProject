package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui.WorkshopDetailsActivity;

public class WorkshopsFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Workshop, WorkshopViewHolder> {

    private Context mContext;

    public WorkshopsFirebaseRecyclerAdapter(FirebaseRecyclerOptions<Workshop> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final WorkshopViewHolder holder, final int position, @NonNull final Workshop model) {
        final Workshop workshop = getItem(position);
        final String id = getRef(position).getKey();
        Timber.i("workshop id is %s", id);
        holder.date.setText(workshop.getDate());
        holder.description.setText(workshop.getDescription());
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyDB tinyDB = new TinyDB(mContext);
                tinyDB.putObject(mContext.getString(R.string.workshop_tinydb_key), workshop);

                Intent intent = new Intent(mContext, WorkshopDetailsActivity.class);
                intent.putExtra(mContext.getString(R.string.open_workshop_details_intent_key),
                        WorkshopDetailsActivity.INTENT_OPEN_UPDATE_WORKSHOP_DETAILS);
                intent.putExtra(mContext.getString(R.string.workshop_id_tinydb_key), id);

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
}

package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui.WorkshopDetailsActivity;

public class WorkshopRecyclerViewAdapter extends RecyclerView.Adapter<WorkshopRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Workshop> mWorkshops;

    public WorkshopRecyclerViewAdapter(Context context, List<Workshop> workshops) {
        mContext = context;
        mWorkshops = workshops;
    }

    public void updateAdapter(List<Workshop> workshops){
        mWorkshops = workshops;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.workshop_rv_item, viewGroup, false);
        return new WorkshopRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Workshop workshop = mWorkshops.get(i);
        viewHolder.date.setText(workshop.getDate());
        viewHolder.venue.setText(String.valueOf(workshop.getDescription()));

    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.workshop_date_tv) TextView date;
        @BindView(R.id.workshop_description_tv) TextView venue;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TinyDB tinydb = new TinyDB(mContext);
            Workshop workshop = mWorkshops.get(getAdapterPosition());
            Intent intent = new Intent(mContext, WorkshopDetailsActivity.class);
            intent.putExtra(mContext.getString(R.string.open_workshop_details_intent_key),
                    WorkshopDetailsActivity.INTENT_OPEN_UPDATE_WORKSHOP_DETAILS);
            tinydb.putObject(mContext.getString(R.string.workshop_tinydb_key), workshop);
            mContext.startActivity(intent);
        }
    }
}

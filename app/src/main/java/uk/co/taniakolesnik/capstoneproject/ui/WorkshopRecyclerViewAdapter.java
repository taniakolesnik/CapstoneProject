package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Context;
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
        viewHolder.venue.setText(String.valueOf(workshop.getName()));

    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.workshop_date_tv) TextView date;
        @BindView(R.id.workshop_venue_tv) TextView venue;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

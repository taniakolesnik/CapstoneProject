package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;

public class WorkshopsFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Workshop, WorkshopViewHolder> {

    public WorkshopsFirebaseRecyclerAdapter(FirebaseRecyclerOptions<Workshop> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkshopViewHolder holder, int position, @NonNull Workshop model) {
        Workshop workshop = getItem(position);
        holder.date.setText(workshop.getDate());
        holder.description.setText(workshop.getDescription());
    }

    @Override
    public WorkshopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workshop_rv_item, viewGroup, false);
        return new WorkshopViewHolder(itemView);
    }
}

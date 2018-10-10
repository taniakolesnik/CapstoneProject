package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;

public class WorkshopViewHolder extends RecyclerView.ViewHolder {

    View mView;

    @BindView(R.id.workshop_date_tv) TextView date;
    @BindView(R.id.workshop_description_tv) TextView description;

    public WorkshopViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mView = itemView;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

}
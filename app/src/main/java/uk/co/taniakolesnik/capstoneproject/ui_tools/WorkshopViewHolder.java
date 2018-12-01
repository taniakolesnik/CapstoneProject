package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;

public class WorkshopViewHolder extends RecyclerView.ViewHolder {

    View mView;

    @BindView(R.id.workshop_day_time_tv) TextView time;
    @BindView(R.id.date_tv) TextView date;
    @BindView(R.id.month_tv) TextView month;
    @BindView(R.id.workshop_description_tv) TextView description;
    @BindView(R.id.workshop_address_tv) TextView address;
    @BindView(R.id.workshop_item_card) CardView cardView;
    @BindView(R.id.attendance_info_tv) TextView attendingInfoTextView;
    @BindView(R.id.attendance_box_rl) RelativeLayout attendingBox;
    @BindView(R.id.attendance_attending_rl) RelativeLayout attendingImage;
    @BindView(R.id.workshop_month_date_rl) RelativeLayout dateLayout;


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
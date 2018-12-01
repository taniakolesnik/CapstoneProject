package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    View mView;

    @BindView(R.id.user_image_iv) ImageView image;
    @BindView(R.id.user_name_tv) TextView name;
    @BindView(R.id.user_email_tv) TextView email;


    public UserViewHolder(View itemView) {
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
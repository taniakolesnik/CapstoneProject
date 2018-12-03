package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder> {

    private ArrayList<String> mData;
    private LayoutInflater mInflater;

    public UserInfoAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public UserInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_info_rv_item, parent, false);
        return new UserInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserInfoViewHolder holder, int position) {
        String value = mData.get(position);
        holder.item.setText(value);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class UserInfoViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.user_info_tv) TextView item;

        UserInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
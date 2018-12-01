package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.models.User;

public class UserFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<User, UserViewHolder> {

    private Context mContext;
    private ProgressBar progressBar;

    public UserFirebaseRecyclerAdapter(FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final UserViewHolder holder, final int position, @NonNull final User model) {

        final User user = getItem(position);

        holder.name.setText(user.getDisplayName());
        holder.email.setText(user.getEmail());
        Picasso.get().load(user.getPhotoUrl()).into(holder.image);
    }

    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_rv_item, viewGroup, false);
        return new UserViewHolder(itemView);
    }

}

package uk.co.taniakolesnik.capstoneproject.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.UserInfoAdapter;
import uk.co.taniakolesnik.capstoneproject.ui_tools.UserInfoLoader;

public class UserInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Map<String, Object>> {

    public static final int LOADER_ID = 1;
    @BindView(R.id.user_info_rv) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int i, @Nullable Bundle bundle) {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        String mAccessToken = "";
        try {
            mAccessToken = tinyDB.getString("token");
        } catch (NullPointerException e){
            Timber.i(e);
        }

        return new UserInfoLoader(this, mAccessToken);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> stringStringMap) {
        ArrayList<String> list = new ArrayList<>();
        for(Map.Entry<String, Object> entry: stringStringMap.entrySet()) {
            list.add(entry.getKey() + " :  " + entry.getValue());
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        UserInfoAdapter adapter = new UserInfoAdapter(this, list);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }
}

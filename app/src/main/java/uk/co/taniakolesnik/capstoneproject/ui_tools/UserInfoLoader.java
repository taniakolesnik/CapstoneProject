package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.Map;

public class UserInfoLoader extends AsyncTaskLoader<Map<String, Object>> {

    private String mAccessToken;

    public UserInfoLoader(Context context, String mAccessToken) {
        super(context);
        this.mAccessToken = mAccessToken;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public Map<String, Object> loadInBackground() {
        if (mAccessToken == null) {
            return null;
        }
        return UserInfoUtil.fetchUserInfo(mAccessToken, getContext());
    }
}
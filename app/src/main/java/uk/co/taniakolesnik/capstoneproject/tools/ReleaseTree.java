package uk.co.taniakolesnik.capstoneproject.tools;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class ReleaseTree extends Timber.Tree {

    // return only WARN, ERROR, WTF
    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG){
            return false;
        }
        return true;
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message,
                       @Nullable Throwable t) {
        if (isLoggable(tag, priority)){
            Log.i(tag, message);
        }

    }
}

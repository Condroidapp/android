package cz.quinix.condroid.abstracts;

import android.app.Activity;
/** @deprecated */
public interface AsyncTaskListener {

    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task);

    Activity getActivity();
}

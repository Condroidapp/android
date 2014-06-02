package cz.quinix.condroid.abstracts;

import android.app.Activity;

/**
 * Created by Jan on 13. 4. 2014.
 */
public interface ITaskListener {

    public void onTaskCompleted(AListenedAsyncTask<?, ?> task);

    Activity getActivity();
}

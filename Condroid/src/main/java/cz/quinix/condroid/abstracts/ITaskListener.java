package cz.quinix.condroid.abstracts;

import android.app.Activity;

public interface ITaskListener {

	public void onTaskCompleted(AListenedAsyncTask<?, ?> task);

	Activity getActivity();

	void onTaskErrored(AListenedAsyncTask task);
}

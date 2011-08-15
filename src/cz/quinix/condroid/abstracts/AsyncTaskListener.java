package cz.quinix.condroid.abstracts;

public interface AsyncTaskListener {
	
	public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task);
}

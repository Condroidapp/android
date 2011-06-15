package cz.quinix.condroid;

import java.util.List;

import android.os.AsyncTask;

public abstract class ListenedAsyncTask<Params, Progress> extends AsyncTask<Params, Progress, List<?>> {
	
	private AsyncTaskListener listener;
	
	public ListenedAsyncTask(AsyncTaskListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onPostExecute(List<?> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(listener != null) {
			listener.onAsyncTaskCompleted(result);
		}
	}

}

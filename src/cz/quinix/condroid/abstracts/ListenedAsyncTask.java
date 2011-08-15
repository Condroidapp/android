package cz.quinix.condroid.abstracts;

import java.util.List;


import android.os.AsyncTask;

public abstract class ListenedAsyncTask<Params, Progress> extends AsyncTask<Params, Progress, List<?>> {
	
	private AsyncTaskListener listener;
	private List<?> result;
	
	public ListenedAsyncTask(AsyncTaskListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onPostExecute(List<?> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		this.result = result;
		if(listener != null) {
			listener.onAsyncTaskCompleted(this);
		}
		
	}
	
	public boolean hasResult() {
		if(result != null) return true;
		return false;
	}
	
	public List<?> getResult() {
		return result;
	}

}

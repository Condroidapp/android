package cz.quinix.condroid.database;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.SystemClock;

public class DataLoadTask extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pd;
	private DataProvider db;
	
	public DataLoadTask(ProgressDialog pd, DataProvider mDatabase) {
		this.pd = pd;
		this.db = mDatabase;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		pd.dismiss();
	}

}

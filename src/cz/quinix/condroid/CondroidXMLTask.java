package cz.quinix.condroid;



import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

abstract public class CondroidXMLTask<Result> extends AsyncTask<Void, Integer, Result> {
	protected String message = "";
	protected Activity caller;
	
	public CondroidXMLTask(Activity caller) {
		this.caller = caller;
	}
	
	
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		if(this.message != "") {
			Toast.makeText(this.caller, this.message, Toast.LENGTH_LONG).show();
		}
	}
}

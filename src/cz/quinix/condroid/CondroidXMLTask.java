package cz.quinix.condroid;



import android.os.AsyncTask;
import android.widget.Toast;

abstract public class CondroidXMLTask<Result> extends AsyncTask<String, Integer, Result> {
	protected String message = "";
	protected CondroidActivity caller;
	
	public CondroidXMLTask(CondroidActivity caller) {
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

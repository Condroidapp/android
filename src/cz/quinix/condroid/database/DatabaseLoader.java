package cz.quinix.condroid.database;

import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;

public class DatabaseLoader extends ListenedAsyncTask<List<?>, Integer> {

	private CondroidDatabase db;
	private ProgressDialog pd;
	private Convention con;

	public DatabaseLoader(AsyncTaskListener listener, CondroidDatabase db,
			Convention con) {
		super(listener);

		this.db = db;
		this.con = con;

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		int value = values[0];
		
		super.onProgressUpdate(values);
		if (!pd.isShowing()) {
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMax(value);
			pd.show();			
			return;
		}
		float progress = (float) value/2;
		pd.setProgress((int) (progress));
	}

	@Override
	protected List<?> doInBackground(List<?>... params) {
		@SuppressWarnings("unchecked")
		List<Annotation> items = ((List<Annotation>) params[0]);
		this.publishProgress(items.size());
		int counter = 0;
		if (items.size() > 0) {
			SQLiteDatabase db = this.db.getWritableDatabase();
			db.insert("cons", null, con.getContentValues());
			HashMap<String, Integer> lines = new HashMap<String, Integer>();
			for (Annotation annotation : items) {
				if (!lines.containsKey(annotation.getProgramLine())) {
					ContentValues cv = new ContentValues();
					cv.put("title", annotation.getProgramLine());
					cv.put("cid", con.getCid());
					int key = (int) db.insert("lines", null, cv);
					lines.put(annotation.getProgramLine(), key);
				}
				this.publishProgress(counter++);
				annotation.setLid(lines.get(annotation.getProgramLine()));
			}

			for (Annotation annotation : items) {
				ContentValues cv = annotation.getContentValues();
				cv.put("cid", con.getCid());
				db.insertOrThrow("annotations", null, cv);
				this.publishProgress(counter++);
			}
		}
		return null;
	}

	public DatabaseLoader setListener(AsyncTaskListener welcomeActivity,
			ProgressDialog pd2) {
		super.setListener(welcomeActivity);
		this.pd = pd2;
		return this;
	}

}

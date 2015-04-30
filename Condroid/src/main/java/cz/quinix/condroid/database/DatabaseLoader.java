package cz.quinix.condroid.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.inject.Inject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.loader.AProgressedTask;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.model.Place;
import cz.quinix.condroid.model.ProgramLine;

public class DatabaseLoader extends AProgressedTask<Integer, Integer> {

	private final boolean progressBar;

	@Inject
	private DataProvider dataProvider;

	private boolean fullInsert;

	private Map<String, List<Annotation>> parameters;

	private Convention event;

	public DatabaseLoader(ITaskListener listener, boolean progressBar) {
		super(listener, listener.getActivity());
		this.progressBar = progressBar;
	}

	@Override
	protected void onSuccess(Integer result) throws Exception {
		if (this.fullInsert) {
			SearchProvider.clear();
		}
		super.onSuccess(result);
	}

	@Override
	protected void onPreExecute() throws Exception {
		if (this.parameters == null) {
			throw new IllegalStateException("Parameters to insert pre not set.");
		}
		if (progressBar) {
			this.showDialog(this.getItemsSize());
		}
	}

	private boolean fullInsert() {
		return this.dataProvider.getCon() == null || this.dataProvider.getCon().getId() != this.event.getId();
	}

	protected void showDialog(int max) {
		if (pd != null) {
			pd.dismiss();
		}
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.processing));
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMax(max);
		pd.setCancelable(false);

		pd.show();
	}

	protected void updateProgress(int value) {
		float progress = (float) value;
		if (pd != null) {
			pd.setProgress((int) (progress));
		}
	}

	public void setData(Map<String, List<Annotation>> parameters, Convention event) {
		this.parameters = parameters;
		this.event = event;
	}

	@Override
	public Integer call() throws Exception {
		CondroidDatabase database = this.dataProvider.getDatabase();
		int counter = 0;
		if (this.fullInsert() && !database.isEmpty()) {
			database.purge(event.getId());
		}
		if (this.getItemsSize() > 0) {
			event.setLastUpdate(new Date());
			SQLiteDatabase db = database.getWritableDatabase();
			db.replace("cons", null, event.getContentValues());

			database.truncatePlaces();

			if (event.getPlaces() != null) {
				for (Place place : event.getPlaces()) {
					ContentValues cv = place.getContentValues();
					cv.put("event_id", event.getId());
					db.replaceOrThrow(CondroidDatabase.PLACES_TABLE, null, cv);
				}
			}

			HashMap<String, Integer> lines = new HashMap<String, Integer>();
			if (!fullInsert) {
				Map<Integer, ProgramLine> l = dataProvider.getProgramLines();
				for (ProgramLine i : l.values()) {
					lines.put(i.getName(), i.getLid());
				}
			}
			String[] keys = {"add", "change"};
			try {
				db.beginTransaction();
				for (String key : keys) {
					List<Annotation> items = this.parameters.get(key);
					for (Annotation annotation : items) {
						if (!lines.containsKey(annotation.getProgramLine())) {
							ContentValues cv = new ContentValues();
							cv.put("title", annotation.getProgramLine());
							cv.put("cid", event.getId());
							int programKey = (int) db.replace("lines", null, cv);
							lines.put(annotation.getProgramLine(), programKey);
						}
						annotation.setLid(lines.get(annotation.getProgramLine()));
					}

					for (Annotation annotation : items) {
						ContentValues cv = annotation.getContentValues();
						cv.put("cid", event.getId());
						db.replaceOrThrow("annotations", null, cv);
						this.updateProgress(++counter);
					}
				}
				for (Annotation a : this.parameters.get("delete")) {
					String[] where = {String.valueOf(event.getId()), String.valueOf(a.getPid())};
					db.delete("annotations", "cid = ? AND pid = ?", where);
					this.updateProgress(++counter);
				}
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				Log.e("Condroid", "DB Insert", e);
			}
			db.endTransaction();
			dataProvider.clear();
		}
		return this.getItemsSize();
	}

	private int getItemsSize() {
		return this.parameters.get("add").size() + this.parameters.get("change").size() + this.parameters.get("delete").size();
	}
}

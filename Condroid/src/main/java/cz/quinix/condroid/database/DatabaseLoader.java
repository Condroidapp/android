package cz.quinix.condroid.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;

import java.util.HashMap;
import java.util.List;

public class DatabaseLoader extends ListenedAsyncTask<List<?>, Integer> {

    private CondroidDatabase db;
    private Convention con;
    private int pdMax;
    private boolean fullInsert;

    public DatabaseLoader(AsyncTaskListener listener, CondroidDatabase db, Convention con, boolean fullInsert) {
        super(listener);
        this.db = db;
        this.con = con;
        this.fullInsert = fullInsert;
    }

    @Override
    protected void onPostExecute(List<?> result) {
        if (this.fullInsert) {
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(parentActivity).edit();
            e.remove("con_specific_message");
            e.commit();
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        //intentionally
    }

    @Override
    protected void showDialog() {
        if (pd != null) {
            pd.dismiss();
        }
        pd = new ProgressDialog(parentActivity);
        pd.setMessage(parentActivity.getString(R.string.processing));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(pdMax);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int value = values[0];

        super.onProgressUpdate(values);
        if (pd == null || !pd.isShowing()) {
            pdMax = value;
            showDialog();
            return;
        }
        float progress = (float) value / 2;
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
            db.replace("cons", null, con.getContentValues());

            HashMap<String, Integer> lines = new HashMap<String, Integer>();
            if (!fullInsert) {
                HashMap<Integer, String> l = DataProvider.getInstance(null).getProgramLines();
                for (Integer i : l.keySet()) {
                    lines.put(l.get(i), i);
                }
            }
            try {
                db.beginTransaction();
                for (Annotation annotation : items) {
                    if (!lines.containsKey(annotation.getProgramLine())) {
                        ContentValues cv = new ContentValues();
                        cv.put("title", annotation.getProgramLine());
                        cv.put("cid", con.getId());
                        int key = (int) db.replace("lines", null, cv);
                        lines.put(annotation.getProgramLine(), key);
                    }
                    this.publishProgress(counter++);
                    annotation.setLid(lines.get(annotation.getProgramLine()));
                    if (this.isCancelled()) {
                        Log.d("Condroid", "Premature end");
                        db.endTransaction();
                        return null;
                    }
                }

                for (Annotation annotation : items) {
                    ContentValues cv = annotation.getContentValues();
                    cv.put("cid", con.getId());
                    db.replaceOrThrow("annotations", null, cv);
                    this.publishProgress(counter++);
                    if (this.isCancelled()) {
                        Log.d("Condroid", "Premature end 2");
                        db.endTransaction();
                        return null;
                    }
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                Log.e("Condroid", "DB Insert", e);
            }
            db.endTransaction();
        }
        return null;
    }


}

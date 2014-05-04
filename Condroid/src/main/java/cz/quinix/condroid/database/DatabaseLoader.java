package cz.quinix.condroid.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.google.inject.Inject;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.loader.AProgressedTask;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.model.ProgramLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseLoader extends AProgressedTask<Integer, List<Annotation>> {

    @Inject private DataProvider dataProvider;
    private boolean fullInsert;
    private Map<String, List<Annotation>> parameters;
    private Convention event;

    public DatabaseLoader(ITaskListener listener) {
        super(listener, (com.actionbarsherlock.app.SherlockFragmentActivity) listener.getActivity());
    }

    @Override
    protected void onSuccess(List<Annotation> annotations) throws Exception {
        if (this.fullInsert) {
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
            e.remove("con_specific_message");
            e.commit();
        }
        super.onSuccess(annotations);
    }

    @Override
    protected void onPreExecute() throws Exception {
        if(this.parameters == null) {
            throw new IllegalStateException("Parameters to insert pre not set.");
        }
        this.showDialog(this.getItemsSize());
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
        pd.setProgress((int) (progress));
    }

    public void setData(Map<String, List<Annotation>> parameters, Convention event) {
        this.parameters = parameters;
        this.event = event;
    }

    @Override
    public List<Annotation> call() throws Exception {
        CondroidDatabase database = this.dataProvider.getDatabase();
        int counter = 0;
        if(this.fullInsert() && !database.isEmpty()) {
            database.purge(event.getId());
        }
        if (this.getItemsSize() > 0) {
            SQLiteDatabase db = database.getWritableDatabase();
            db.replace("cons", null, event.getContentValues());

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
                for(String key : keys) {
                    List<Annotation> items = this.parameters.get(key);
                    for (Annotation annotation : items) {
                        if (!lines.containsKey(annotation.getProgramLine())) {
                            ContentValues cv = new ContentValues();
                            cv.put("title", annotation.getProgramLine());
                            cv.put("cid", event.getId());
                            int programKey = (int) db.replace("lines", null, cv);
                            lines.put(annotation.getProgramLine(), programKey);
                        }
                        this.updateProgress(counter++);
                        annotation.setLid(lines.get(annotation.getProgramLine()));
                    /*if (this.isCancelled()) {
                        Log.d("Condroid", "Premature end");
                        db.endTransaction();
                        return null;
                    }*/
                    }

                    for (Annotation annotation : items) {
                        ContentValues cv = annotation.getContentValues();
                        cv.put("cid", event.getId());
                        db.replaceOrThrow("annotations", null, cv);
                        this.updateProgress(counter++);
                    /*if (this.isCancelled()) {
                        Log.d("Condroid", "Premature end 2");
                        db.endTransaction();
                        return null;
                    }*/
                    }
                }
                for(Annotation a :this.parameters.get("delete")) {
                    String[] where = {String.valueOf(event.getId()), String.valueOf(a.getPid())};
                    db.delete("annotations", "cid = ? AND pid = ?", where);
                    this.updateProgress(counter++);
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                Log.e("Condroid", "DB Insert", e);
            }
            db.endTransaction();
        }
        return null;
    }

    private int getItemsSize() {
        return this.parameters.get("add").size() + this.parameters.get("change").size() + this.parameters.get("delete").size();
    }
}

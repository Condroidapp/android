package cz.quinix.condroid.abstracts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.loader.ConventionLoader;
import cz.quinix.condroid.loader.DateTypeAdapter;
import cz.quinix.condroid.ui.ProgramActivity;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.util.Date;
import java.util.List;
/** @deprecated */
public abstract class ListenedAsyncTask<Params, Progress> extends AsyncTask<Params, Progress, List<?>> {

    protected Activity parentActivity;
    private AsyncTaskListener listener;
    private List<?> result;
    protected ProgressDialog pd;
    protected Exception backgroundException = null;

    public static final String API_ENDPOINT = "http://condroid.fan-project.com/api/3/";

    public ListenedAsyncTask(AsyncTaskListener listener) {
        this.listener = listener;
        if (listener != null)
            this.parentActivity = listener.getActivity();
    }

    public ListenedAsyncTask<Params, Progress> setListener(AsyncTaskListener listener) {
        this.listener = listener;
        if (listener != null && parentActivity == null) {
            parentActivity = listener.getActivity();
        }
        return this;
    }

    @Override
    protected void onPreExecute() {
        this.showDialog();
    }

    @Override
    protected void onPostExecute(List<?> result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        this.result = result;
        if (pd != null) {
            pd.dismiss();
        }
        if (backgroundException != null) {
            Toast.makeText(parentActivity, (backgroundException instanceof XMLProccessException)?backgroundException.getMessage():"Během zpracování došlo k neočekávané chybě. Zkuste akci opakovat později, pokud problém přetrvá, kontaktujte autora volbou feedback.", Toast.LENGTH_LONG).show();
        }
        if (listener != null) {
            listener.onAsyncTaskCompleted(this);
        }

    }

    public boolean hasResult() {
        if (result != null) return true;
        return false;
    }

    public List<?> getResult() {
        return result;
    }

    public void attach(ProgramActivity parent) {
        this.parentActivity = parent;
        if (listener == null) {
            listener = (AsyncTaskListener) parent;
        }
        if (!this.getStatus().equals(Status.FINISHED) && pd != null) {
            this.showDialog();
        }
    }

    protected void showDialog() {

    }

    public void detach() {
        if (listener.equals(parentActivity)) {
            listener = null;
        }
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        this.parentActivity = null;
    }

    protected CondroidApi getCondroidService() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ListenedAsyncTask.API_ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();
        return adapter.create(CondroidApi.class);
    }
}

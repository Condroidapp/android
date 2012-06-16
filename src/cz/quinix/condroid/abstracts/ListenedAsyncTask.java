package cz.quinix.condroid.abstracts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import cz.quinix.condroid.ui.ProgramActivity;

import java.util.List;

public abstract class ListenedAsyncTask<Params, Progress> extends AsyncTask<Params, Progress, List<?>> {

    protected Activity parentActivity;
    private AsyncTaskListener listener;
    private List<?> result;
    protected ProgressDialog pd;

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

}

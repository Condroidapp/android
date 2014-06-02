package cz.quinix.condroid.ui.dataLoading;

import android.app.Activity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.DataLoader;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;

public class Downloader extends AsyncTaskDialog {

    private Convention convention;
    private boolean update;


    public Downloader(Activity activity, Convention convention) {
        this(activity, convention, false);
    }

    public Downloader(Activity programActivity, Convention convention, boolean update) {
        this.update = update;
        this.parent = programActivity;
        this.convention = convention;
    }


    public void invoke() {
        String lastUpdate = null;
        if(this.update) {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            lastUpdate = format.format(convention.getLastUpdate());
        }

        DataLoader task1 = new DataLoader(this, parent, convention, lastUpdate);

        task1.execute();
    }

    @Override
    public void onTaskCompleted(AListenedAsyncTask<?, ?> task) {
        Map<String, List<Annotation>> annotations = (Map<String, List<Annotation>>) task.getResults();
        if(annotations == null) {
            Toast.makeText(this.getActivity(), R.string.noUpdates, Toast.LENGTH_LONG).show();
            return;
        }
        if(annotations.size() > 0) {
            DatabaseLoader task2 = new DatabaseLoader((ITaskListener) parent);
            task2.setData(annotations, convention);
            task2.execute();
        }
    }
}

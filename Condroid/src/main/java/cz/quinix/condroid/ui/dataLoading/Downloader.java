package cz.quinix.condroid.ui.dataLoading;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.DataLoader;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Downloader extends AsyncTaskDialog {

    private List<Convention> conventionList;
    private DataLoader task1;
    private DatabaseLoader task2;
    private Convention convention;

    public Downloader(SherlockFragmentActivity programActivity, Convention convention) {
        this.parent = programActivity;
        this.convention = convention;
    }

    @Override
    public void setParent(SherlockFragmentActivity parent) {
        super.setParent(parent);
        /*if (task1 != null && !task1.getStatus().equals(AsyncTask.Status.FINISHED)) {
            if (parent == null) {
                task1.detach();
            } else {
                task1.attach(parent);
            }
        }
        if (task2 != null && !task2.getStatus().equals(AsyncTask.Status.FINISHED)) {
            if (parent == null) {
                task2.detach();
            } else {
                task2.attach(parent);
            }
        }*/
    }

    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task) {


        /*if (task.hasResult()) {
            List<?> list = task.getResult();
            if (list.size() == 0 && ((DataLoader) task).getResultCode() != -1) {
                Toast.makeText(parent, "Nejsou k dispozici žádné aktualizace.", Toast.LENGTH_LONG).show();
                ((AsyncTaskListener) parent).onAsyncTaskCompleted(task);
            } else if (list != null) {
                task2 = DataProvider.getInstance(parent).prepareInsert(conventionList != null || (task instanceof DataLoader && ((DataLoader) task).getResultCode() == 2));
                task2.setListener((AsyncTaskListener) parent);
                task2.execute(list);
            }
        }*/
    }

    public void invoke() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        String lastUpdate = format.format(convention.getLastUpdate());

        task1 = new DataLoader(this, parent, convention, null);

        task1.execute();
    }

    @Override
    public void onTaskCompleted(AListenedAsyncTask<?, ?> task) {
        Map<String, List<Annotation>> annotations = (Map<String, List<Annotation>>) task.getResults();
        if(annotations.size() > 0) {
            task2 = new DatabaseLoader((ITaskListener) parent);
            task2.setData(annotations, convention);
            task2.execute();
        }
    }
}

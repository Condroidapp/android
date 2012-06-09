package cz.quinix.condroid.ui.dataLoading;

import android.content.DialogInterface;
import android.os.AsyncTask;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.DataLoader;
import cz.quinix.condroid.model.Convention;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 15.4.12
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Downloader extends AsyncTaskDialog {

    private List<Convention> conventionList;
    private DataLoader task1;
    private DatabaseLoader task2;

    public Downloader(CondroidActivity parent, List<Convention> conventionList) {
        this.parent = parent;
        this.conventionList = conventionList;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        DataProvider.getInstance(parent).setConvention(conventionList.get(i));
        task1 = new DataLoader(this);
        task1.execute(conventionList.get(i).getDataUrl());
    }

    @Override
    public void setParent(CondroidActivity parent) {
        super.setParent(parent);    //To change body of overridden methods use File | Settings | File Templates.
        if (task1 != null && !task1.getStatus().equals(AsyncTask.Status.FINISHED)) {
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
        }
    }

    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task) {


        if (task.hasResult()) {
            List<?> list = task.getResult();
            if (list != null) {
                task2 = DataProvider.getInstance(parent).prepareInsert();
                task2.setListener((AsyncTaskListener) parent);
                task2.execute(list);
            }
        }
    }
}

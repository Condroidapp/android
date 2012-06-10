package cz.quinix.condroid.ui.dataLoading;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.DataLoader;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.ProgramActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    private Convention convention;

    public Downloader(CondroidActivity parent, List<Convention> conventionList) {
        this.parent = parent;
        this.conventionList = conventionList;
    }

    public Downloader(ProgramActivity programActivity, Convention convention) {
        this.parent = programActivity;
        this.convention = convention;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if(conventionList != null) {
            DataProvider.getInstance(parent).setConvention(conventionList.get(i));
            convention = conventionList.get(i);
        }
        this.invoke();
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
            if(list.size() == 0 && ((DataLoader) task).getResultCode() != -1) {
                Toast.makeText(parent, "Nejsou k dispozici žádné aktualizace.", Toast.LENGTH_LONG).show();
                ((AsyncTaskListener) parent).onAsyncTaskCompleted(task);
            }
            else if (list != null) {
                task2 = DataProvider.getInstance(parent).prepareInsert(conventionList != null);
                task2.setListener((AsyncTaskListener) parent);
                task2.execute(list);
            }
        }
    }

    public void invoke() {
        String lastUpdate = null;

        if(conventionList == null) {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            lastUpdate = format.format(convention.getLastUpdate());
        }
        task1 = new DataLoader(this);

        task1.execute(convention.getDataUrl(), lastUpdate);
    }
}

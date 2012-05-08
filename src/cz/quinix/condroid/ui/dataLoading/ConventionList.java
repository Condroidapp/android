package cz.quinix.condroid.ui.dataLoading;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.loader.ConventionLoader;
import cz.quinix.condroid.model.Convention;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 15.4.12
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public class ConventionList extends AsyncTaskDialog {

    private ConventionLoader task;

    public ConventionList(CondroidActivity parent) {
        this.parent = parent;
    }

    public void onClick(DialogInterface dialog, int which) {

        dialog.cancel();
        try {
            task=new ConventionLoader(this);
            task.execute();
        } catch (Exception e) {
            Log.e("Condroid", "Exception during XML con-data recieve.", e);
            return;
        }

    }

    @Override
    public void setParent(CondroidActivity parent) {
        super.setParent(parent);    //To change body of overridden methods use File | Settings | File Templates.

        if(task != null && !task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            if(parent == null) {
                task.detach();
            }
            else {
                task.attach(parent);
            }
        }
    }

    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task) {

        if (task.hasResult()) {
            List<?> list = task.getResult();
            // conventions downloaded
            if (list.size() == 0) {
                Toast.makeText(parent,
                        "Chyba při stahování, zkuste to prosím později.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            final List<Convention> conventionList = (List<Convention>) list;
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    parent);
            builder.setTitle(R.string.dPickConvention);
            String[] items = new String[list.size()];
            int i = 0;
            for (Object con : list) {
                items[i++] = ((Convention) con).getName();
            }
            this.subDialog = new Downloader(parent, conventionList);
            builder.setItems(items, this.subDialog);
            AlertDialog d = builder.create();
            d.show();
        }

    }
}
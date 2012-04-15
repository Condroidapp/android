package cz.quinix.condroid.ui.dataLoading;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
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
public class Downloader implements DialogInterface.OnClickListener, AsyncTaskListener {

    private CondroidActivity parent;
    private List<Convention> conventionList;
    private ProgressDialog pd;

    public Downloader(CondroidActivity parent, List<Convention> conventionList) {
        this.parent = parent;
        this.conventionList = conventionList;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        DataProvider.getInstance(parent).setConvention(conventionList.get(i));
        pd = ProgressDialog.show(parent, "",
                parent.getString(R.string.preparing), true);
        new DataLoader(this, pd)
                .execute(conventionList.get(i).getDataUrl());
    }

    @Override
    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task) {
        pd.dismiss();

        if (task.hasResult()) {
            List<?> list = task.getResult();
            if (list != null) {
                pd = new ProgressDialog(parent);
                pd.setMessage(parent.getString(R.string.processing));
                pd.setCancelable(true);

                DataProvider.getInstance(parent).prepareInsert().setListener((AsyncTaskListener) parent, pd).execute(list);
            }
        }
    }
}

package cz.quinix.condroid.ui.dataLoading;

import android.app.Activity;
import android.content.DialogInterface;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 7.5.12
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class AsyncTaskDialog implements DialogInterface.OnClickListener, AsyncTaskListener {
    protected CondroidActivity parent;
    protected AsyncTaskDialog subDialog = null;

    public void setParent(CondroidActivity parent) {
        this.parent = parent;
        if (subDialog != null) {
            subDialog.setParent(parent);
        }

    }

    @Override
    public Activity getActivity() {
        return parent;
    }
}

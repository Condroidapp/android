package cz.quinix.condroid.ui.dataLoading;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockActivity;

import cz.quinix.condroid.abstracts.ITaskListener;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 7.5.12
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class AsyncTaskDialog implements ITaskListener {
    protected Activity parent;

    public void setParent(Activity parent) {
        this.parent = parent;
    }

    @Override
    public Activity getActivity() {
        return parent;
    }
}

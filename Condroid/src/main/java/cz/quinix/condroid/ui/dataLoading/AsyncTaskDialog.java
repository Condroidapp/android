package cz.quinix.condroid.ui.dataLoading;

import android.app.Activity;
import android.content.DialogInterface;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.ui.ProgramActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 7.5.12
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class AsyncTaskDialog implements ITaskListener {
    protected SherlockFragmentActivity parent;

    public void setParent(SherlockFragmentActivity parent) {
        this.parent = parent;
    }

    @Override
    public SherlockFragmentActivity getActivity() {
        return parent;
    }
}

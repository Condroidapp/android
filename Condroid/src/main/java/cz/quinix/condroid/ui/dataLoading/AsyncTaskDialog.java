package cz.quinix.condroid.ui.dataLoading;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.quinix.condroid.abstracts.ITaskListener;

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

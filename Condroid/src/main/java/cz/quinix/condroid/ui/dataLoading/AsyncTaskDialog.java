package cz.quinix.condroid.ui.dataLoading;

import android.app.Activity;

import cz.quinix.condroid.abstracts.ITaskListener;

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

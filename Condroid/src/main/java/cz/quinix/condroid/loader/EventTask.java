package cz.quinix.condroid.loader;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.model.Convention;

public class EventTask extends AListenedAsyncTask<Void, Convention> {

	private int id;

	public EventTask(ITaskListener listener, int id) {
		super(listener);
		this.id = id;
	}

	@Override
	public Convention call() throws Exception {
		CondroidApi service = getCondroidService();
		return service.getEvent(id);
	}
}

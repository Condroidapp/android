package cz.quinix.condroid.loader;

import java.util.List;

import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;

public class DatabaseLoader extends ListenedAsyncTask<List<?>, Integer> {
	
	private DataProvider db;

	public DatabaseLoader(AsyncTaskListener listener, DataProvider dp) {
		super(listener);
		
		db=dp;

	}

	@Override
	protected List<?> doInBackground(List<?>... params) {
		List<Annotation> items = (List<Annotation>) params[0];
		this.publishProgress(0);
		 
		if(items.size() > 0) {
			db.insert(items);
		}
		return null;
	}

}

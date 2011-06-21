package cz.quinix.condroid.abstracts;

import java.util.List;

public interface AsyncTaskListener {
	
	public void onAsyncTaskCompleted(List<?> list);
}

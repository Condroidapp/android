package cz.quinix.condroid;

import java.util.List;

public interface AsyncTaskListener {
	
	public void onAsyncTaskCompleted(List<?> list);
}

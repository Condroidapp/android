package cz.quinix.condroid.ui.dataLoading;

import android.app.Activity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.DataLoader;
import cz.quinix.condroid.loader.EventTask;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;

public class Downloader extends AsyncTaskDialog {

	private Convention convention;

	private boolean update;

	private boolean progressBar;

	public Downloader(Activity activity, Convention convention) {
		this(activity, convention, false, true);
	}

	public Downloader(Activity programActivity, Convention convention, boolean update, boolean progressBar) {
		this.update = update;
		this.progressBar = progressBar;
		this.parent = programActivity;
		this.convention = convention;
	}

	public void invoke() {
		String lastUpdate = null;
		if (this.update) {
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
			lastUpdate = format.format(convention.getLastUpdate());
		}

		DataLoader task1 = new DataLoader(this, parent, convention, lastUpdate, progressBar);

		task1.execute();
	}

	@Override
	public void onTaskCompleted(AListenedAsyncTask<?, ?> task) {
		final Map<String, List<Annotation>> annotations = (Map<String, List<Annotation>>) task.getResults();
		if (annotations == null && !this.update) {
			Toast.makeText(this.getActivity(), R.string.noUpdates, Toast.LENGTH_LONG).show();
		}
		if (annotations == null) {
			((ITaskListener) parent).onTaskCompleted(task);
			return;
		}
		if (annotations.size() > 0) {
			EventTask task2 = new EventTask(new ITaskListener() {
				@Override
				public void onTaskCompleted(AListenedAsyncTask<?, ?> task) {
					DatabaseLoader task2 = new DatabaseLoader((ITaskListener) parent, progressBar);
					task2.setData(annotations, (Convention) task.getResults());
					task2.execute();
				}

				@Override
				public Activity getActivity() {
					return parent;
				}

				@Override
				public void onTaskErrored(AListenedAsyncTask task) {
					Downloader.this.onTaskErrored(task);
				}
			}, convention.getId());
			task2.execute();

		}
	}

	@Override
	public void onTaskErrored(AListenedAsyncTask task) {
		if (this.parent instanceof ITaskListener) {
			((ITaskListener) parent).onTaskErrored(task);
		}
	}
}

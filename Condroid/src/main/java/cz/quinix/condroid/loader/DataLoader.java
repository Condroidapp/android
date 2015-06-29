package cz.quinix.condroid.loader;

import android.app.Activity;

import java.util.List;
import java.util.Map;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import retrofit.RetrofitError;

public class DataLoader extends AProgressedTask<Integer, Map<String, List<Annotation>>> {

	private final boolean showProgress;


	private Convention convention;

	private String lastUpdate;

	public DataLoader(ITaskListener listener, Activity parent, Convention convention, String lastUpdate, boolean showProgress) {
		super(listener, parent);
		this.convention = convention;
		this.lastUpdate = lastUpdate;
		this.showProgress = showProgress;
		pdString = parent.getString(R.string.loading);
		this.showDialog();
	}

	@Override
	protected void showDialog() {
		if (this.showProgress) {
			super.showDialog();
		}
	}

	@Override
	public Map<String, List<Annotation>> call() throws Exception {
		CondroidApi service = getCondroidService();
		if (this.lastUpdate != null) {
			try {
				return service.listAnnotations(this.convention.getId(), lastUpdate);
			} catch (RetrofitError e) {
				if (e.getResponse() != null && e.getResponse().getStatus() == 304) {
					return null;
				}
				throw e;
			}
		}
		return service.listAnnotations(this.convention.getId());
	}

}

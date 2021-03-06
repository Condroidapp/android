package cz.quinix.condroid.abstracts;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Date;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.R;
import cz.quinix.condroid.loader.AnnotationTypeAdapter;
import cz.quinix.condroid.loader.DateTypeAdapter;
import cz.quinix.condroid.model.AnnotationType;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import roboguice.util.RoboAsyncTask;

public abstract class AListenedAsyncTask<Progress, Result> extends RoboAsyncTask<Result> {

	@Inject
	private Provider<Context> contextProvider;

	private ITaskListener listener;

	private Result results;

	public AListenedAsyncTask(ITaskListener listener) {
		super(listener.getActivity());
		this.listener = listener;
	}

	protected CondroidApi getCondroidService() {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.registerTypeAdapter(AnnotationType.class, new AnnotationTypeAdapter())
				.create();
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(CondroidApi.API_ENDPOINT)
				.setConverter(new GsonConverter(gson))
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addHeader("X-Device-Info", CondroidActivity.getDeviceInfoString(listener.getActivity()));
					}
				})
				.build();
		return adapter.create(CondroidApi.class);
	}

	protected void onSuccess(Result results) throws Exception {
		this.results = results;
		this.listener.onTaskCompleted(this);
	}

	public Result getResults() {
		return this.results;
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		if (e instanceof RetrofitError) {
			if (((RetrofitError) e).isNetworkError()) {
				Toast.makeText(listener.getActivity(), R.string.networkError, Toast.LENGTH_LONG).show();
				//return;
			} else if (((RetrofitError) e).getResponse() != null && ((RetrofitError) e).getResponse().getStatus() >= 500) {
				Toast.makeText(listener.getActivity(), R.string.serverError, Toast.LENGTH_LONG).show();
				//return;
			}

		}
		super.onException(e);
		listener.onTaskErrored(this);
	}
}

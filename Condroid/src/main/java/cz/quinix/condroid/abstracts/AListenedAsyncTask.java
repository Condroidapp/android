package cz.quinix.condroid.abstracts;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Date;
import java.util.List;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.loader.AnnotationTypeAdapter;
import cz.quinix.condroid.loader.DateTypeAdapter;
import cz.quinix.condroid.model.AnnotationType;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import roboguice.util.RoboAsyncTask;

/**
 * Created by Jan on 13. 4. 2014.
 */
public abstract class AListenedAsyncTask<Progress, Result> extends RoboAsyncTask<Result> {

    @Inject private Provider<Context> contextProvider;
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
                .setEndpoint(CondroidActivity.API_ENDPOINT)
                .setConverter(new GsonConverter(gson))
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
}

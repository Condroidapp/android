package cz.quinix.condroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;

import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.loader.ConventionLoader;
import cz.quinix.condroid.model.Convention;
import roboguice.inject.InjectView;

public class WelcomeActivity extends RoboSherlockActivity implements ITaskListener {

    @Inject
    private DataProvider database;

    @InjectView(R.id.pbEvents) private ProgressBar progressBar;
    @InjectView(R.id.lEventSelector) private ListView listView;
    @InjectView(R.id.layProgressEvents) private LinearLayout layProgressEvents;
    @InjectView(R.id.layEventSelector) private LinearLayout layEventSelector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.welcome);

        this.load();
    }

    private void load() {
        layEventSelector.setVisibility(View.GONE);
        layProgressEvents.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        if(database.hasData()) {
            Log.d(this.getClass().getName(), "Screen goto: Annotation list");
            return;
        }

        ConventionLoader task = new ConventionLoader(this);
        task.execute();
    }


    @Override
    public void onTaskCompleted(AListenedAsyncTask<?, ?> task, List<?> results) {
        final List<Convention> data = (List<Convention>) results;
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for (Convention c : data) {
            a.add(c.getName());
        }

        listView.setAdapter(a);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Convention item = data.get(position);
                Toast.makeText(WelcomeActivity.this, "Clicked "+item.getName(), Toast.LENGTH_LONG).show();
            }
        });

        layEventSelector.setVisibility(View.VISIBLE);
        layProgressEvents.setVisibility(View.GONE);
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}

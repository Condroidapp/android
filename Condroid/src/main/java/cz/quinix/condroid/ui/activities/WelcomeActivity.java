package cz.quinix.condroid.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.ConventionLoader;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.adapters.EventAdapter;
import cz.quinix.condroid.ui.dataLoading.Downloader;
import roboguice.inject.InjectView;

public class WelcomeActivity extends RoboSherlockFragmentActivity implements ITaskListener {

    @Inject
    private DataProvider database;

    @InjectView(R.id.pbEvents) private ProgressBar progressBar;
    @InjectView(R.id.lEventSelector) private ListView listView;
    @InjectView(R.id.layProgressEvents) private LinearLayout layProgressEvents;
    @InjectView(R.id.layEventSelector) private LinearLayout layEventSelector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "cz.quinix.condroid.FastJodaTimeZoneProvider");

        this.setContentView(R.layout.welcome);


        this.load();

    }

    private void load() {
        layEventSelector.setVisibility(View.GONE);
        layProgressEvents.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        boolean force = this.getIntent().getBooleanExtra("force", false);
        if(database.hasData() && !force) {
            Log.d(this.getClass().getName(), "Screen goto: Annotation list");
            this.startProgramActivity();
            return;
        }

        ConventionLoader task = new ConventionLoader(this);
        task.execute();
    }


    @Override
    public void onTaskCompleted(AListenedAsyncTask<?, ?> task) {
        if (task instanceof ConventionLoader) {
            this.onEventsLoaded(((ConventionLoader) task).getResults());
        } else if (task instanceof DatabaseLoader) {
            this.showMessage();
            this.startProgramActivity();
        } else {
            throw new IllegalArgumentException("Instance of " + task.getClass().getName() + " is not supported in this handler.");
        }
    }

    private void showMessage() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (!database.getCon().getMessage().equals("")) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(database.getCon().getName());
            ab.setMessage(database.getCon().getMessage());
            ab.setCancelable(true);
            ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            ab.create().show();
        }
    }

    private void onEventsLoaded(final List<Convention> results) {
        EventAdapter adapter = new EventAdapter(this, R.layout.event_item_layout, results);

        listView.setAdapter(adapter);
        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Convention item = results.get(position);
                Downloader loader = new Downloader(WelcomeActivity.this, item);
                loader.invoke();
            }
        });

        layEventSelector.setVisibility(View.VISIBLE);
        layProgressEvents.setVisibility(View.GONE);
    }

    private void startProgramActivity() {
        if(this.database.hasData()) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        } else {
            Toast.makeText(this, "Nebyly nalezeny žádná data pro tuto událost.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}

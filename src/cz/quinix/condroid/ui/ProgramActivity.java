package cz.quinix.condroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.RunningAdapter;
import cz.quinix.condroid.ui.dataLoading.ConventionList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 13.4.12
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ProgramActivity extends CondroidActivity implements AsyncTaskListener {

    public static final String TAG = "Condroid";
    private static final String SCREEN_RUNNING = "running";
    private static final String SCREEN_ALL = "all";
    private static final String SCREEN_TW = "TW";

    protected DataProvider provider;
    protected List<Annotation> annotations = null;
    protected BaseAdapter adapter;

    private String screen = ProgramActivity.SCREEN_RUNNING;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "cz.quinix.condroid.FastJodaTimeZoneProvider");



        this.setContentView(R.layout.program);
        provider = DataProvider.getInstance(getApplicationContext());

        if(this.dataAvailable()) {
            this.initView();
        }
        Button running = (Button) this.findViewById(R.id.bNow);
        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchView(SCREEN_RUNNING);
            }
        });


        Button all = (Button) this.findViewById(R.id.bAll);
        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchView(SCREEN_ALL);
            }
        });



    }

    private boolean dataAvailable() {
        if(provider.hasData()) {
            return true;
        } else {
            this.loadData();
        }



        return false;
    }

    private void loadData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.noData)
                .setPositiveButton(R.string.yes, new ConventionList(this))
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                                if (!provider.hasData()) {
                                    Toast.makeText(
                                            ProgramActivity.this,
                                            "Condroid nemá data se kterými by mohl pracovat, proto se nyní ukončí.",
                                            Toast.LENGTH_LONG).show();
                                    ProgramActivity.this.finish();
                                }
                            }
                        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadDatalist() {
        if(this.screen == SCREEN_RUNNING) {
            annotations = provider.getRunningAndNext();
        }
        if(this.screen == SCREEN_ALL) {
            annotations = provider.getAnnotations("",0);
        }
    }
    private void initView() {
        this.loadDatalist();
        if(this.screen == SCREEN_RUNNING || this.screen == SCREEN_ALL) {
            adapter = new RunningAdapter(annotations, this);
            ListView lw = (ListView) this.findViewById(R.id.listView);
            lw.setAdapter(adapter);
            registerForContextMenu(lw);
        }
    }

    private void switchView(String viewName) {
        if(!this.screen.equals(viewName)) {
            this.screen = viewName;
            loadDatalist();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task) {

        if (!provider.hasData()) {
            Toast.makeText(this,
                    "Chyba při stahování, zkuste to prosím později.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(TAG, 0).edit();
        editor.remove("messageShown");
        editor.commit();
        initView();
    }
}


package cz.quinix.condroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.RunningAdapter;
import cz.quinix.condroid.ui.dataLoading.ConventionList;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 13.4.12
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ProgramActivity extends CondroidActivity implements AsyncTaskListener, AdapterView.OnItemClickListener {

    public static final String TAG = "Condroid";
    private static final String SCREEN_RUNNING = "running";
    private static final String SCREEN_ALL = "all";
    private static final String SCREEN_TW = "TW";

    protected DataProvider provider;
    protected ListView lwRunning = null;
    protected ListView lwAll = null;

    private View openedContextMenu;


    private String screen = ProgramActivity.SCREEN_RUNNING;
    public static boolean refreshDataset = false;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "cz.quinix.condroid.FastJodaTimeZoneProvider");


        this.setContentView(R.layout.program);
        provider = DataProvider.getInstance(getApplicationContext());

        lwRunning = (ListView) this.findViewById(R.id.lwRunning);
        lwAll = (ListView) this.findViewById(R.id.lwAll);
        if (this.dataAvailable()) {
            this.initView();
        }
        Button running = (Button) this.findViewById(R.id.bNow);
        running.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchView(SCREEN_RUNNING);
            }
        });


        Button all = (Button) this.findViewById(R.id.bAll);
        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchView(SCREEN_ALL);
            }
        });

        lwRunning.setOnItemClickListener(this);
        lwAll.setOnItemClickListener(this);
        registerForContextMenu(lwRunning);
        registerForContextMenu(lwAll);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.refreshDataset) {
            if (lwAll.getAdapter() != null) {
                ((EndlessAdapter) lwAll.getAdapter()).refreshDataset();
            }
            if (lwRunning.getAdapter() != null) {
                ((EndlessAdapter) lwRunning.getAdapter()).refreshDataset();
            }
            refreshDataset = false;
        }
    }

    private boolean dataAvailable() {
        if (provider.hasData()) {
            return true;
        } else {
            this.loadData();
            return true;
        }
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

    private void initView() {
        this.initListView();
    }

    private void switchView(String viewName) {
        if (!this.screen.equals(viewName)) {
            this.screen = viewName;
            this.initListView();
        }
    }

    private void initListView() {
        if (this.screen == SCREEN_ALL) {
            if (this.lwAll.getAdapter() == null) {
                //init
                this.lwAll.setAdapter(new EndlessAdapter(this, provider.getAnnotations("", 0)));
            } else {
                ((EndlessAdapter) lwAll.getAdapter()).refreshDataset();
            }
            lwRunning.setVisibility(View.GONE);
            lwAll.setVisibility(View.VISIBLE);
        }
        if (this.screen == SCREEN_RUNNING) {
            if (this.lwRunning.getAdapter() == null) {
                this.lwRunning.setAdapter(new RunningAdapter(provider.getRunningAndNext(0), this));
            } else {
                ((EndlessAdapter) lwRunning.getAdapter()).refreshDataset();
            }
            lwAll.setVisibility(View.GONE);
            lwRunning.setVisibility(View.VISIBLE);
        }
    }


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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView instanceof ListView) {
            Annotation selected = (Annotation) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(this, ShowAnnotation.class);
            intent.putExtra("annotation", selected);
            this.startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v instanceof ListView) {
            openedContextMenu = v;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Annotation an = (Annotation) ((ListView) v).getItemAtPosition(info.position);
            if (an.getTitle() != "break") {
                menu.setHeaderTitle(an.getTitle());
                String[] menuItems = getResources().getStringArray(R.array.annotationContext);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        }

    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        this.openedContextMenu = null;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (this.openedContextMenu != null) {
            int menuItemIndex = item.getItemId();
            Annotation an = (Annotation) ((ListView) this.openedContextMenu).getItemAtPosition(info.position);

            switch (menuItemIndex) {
                case 0:
                    new ShareProgramListener(this).invoke(an);
                    break;
                case 1:

                    new MakeFavoritedListener(this).invoke(an, null);
                    ((EndlessAdapter) ((ListView) openedContextMenu).getAdapter()).refreshDataset();
                    break;
                case 2:
                    new SetReminderListener(this).invoke(an);
                default:
                    break;
            }
        }
        return true;
    }
}


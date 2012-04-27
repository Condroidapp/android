package cz.quinix.condroid.ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.RunningAdapter;
import cz.quinix.condroid.ui.dataLoading.ConventionList;
import cz.quinix.condroid.ui.listeners.FilterListener;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 13.4.12
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ProgramActivity extends CondroidActivity implements AsyncTaskListener, AdapterView.OnItemClickListener {

    public static final String TAG = "Condroid";
    public static final String SCREEN_RUNNING = "running";
    public static final String SCREEN_ALL = "all";
    public static final String SCREEN_TW = "TW";

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
        FrameLayout running = (FrameLayout) this.findViewById(R.id.fRunning);
        FrameLayout all = (FrameLayout) this.findViewById(R.id.fAll);
        FrameLayout twitter = (FrameLayout) this.findViewById(R.id.fTwitter);
        running.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchView(SCREEN_RUNNING);
            }
        });


        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchView(SCREEN_ALL);
            }
        });

        ImageButton ibFilter = (ImageButton) this.findViewById(R.id.ibFilter);
        ibFilter.setOnClickListener(new FilterListener(this));

        ImageButton ibSearch = (ImageButton) this.findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgramActivity.this.onSearchRequested();
            }
        });


        lwRunning.setOnItemClickListener(this);
        lwAll.setOnItemClickListener(this);
        registerForContextMenu(lwRunning);
        registerForContextMenu(lwAll);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        this.handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            SearchProvider.getSearchQueryBuilder(screen).addParam(intent.getStringExtra(SearchManager.QUERY));
            this.applySearch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.refreshDataset) {
            this.refreshLists();
            refreshDataset = false;
        }
    }

    private void refreshLists() {
        if (lwAll.getAdapter() != null) {
            ((EndlessAdapter) lwAll.getAdapter()).refreshDataset();
        }
        if (lwRunning.getAdapter() != null) {
            ((EndlessAdapter) lwRunning.getAdapter()).refreshDataset();
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
        this.handleIntent(this.getIntent());
    }

    private void switchView(String viewName) {
        if (!this.screen.equals(viewName)) {
            this.screen = viewName;
            this.initListView();
        }
    }

    private void initListView() {
        FrameLayout running = (FrameLayout) this.findViewById(R.id.fRunning);
        FrameLayout all = (FrameLayout) this.findViewById(R.id.fAll);
        FrameLayout twitter = (FrameLayout) this.findViewById(R.id.fTwitter);
        if (this.screen == SCREEN_ALL) {
            if (this.lwAll.getAdapter() == null) {
                //init
                this.lwAll.setAdapter(new EndlessAdapter(this, provider.getAnnotations(null, 0)));
            } else {
                ((EndlessAdapter) lwAll.getAdapter()).refreshDataset();
            }
            lwRunning.setVisibility(View.GONE);
            lwAll.setVisibility(View.VISIBLE);

            all.setBackgroundColor(R.color.black);
            running.setBackgroundColor(android.R.color.transparent);
            twitter.setBackgroundColor(android.R.color.transparent);
        }
        if (this.screen == SCREEN_RUNNING) {
            if (this.lwRunning.getAdapter() == null) {
                this.lwRunning.setAdapter(new RunningAdapter(provider.getRunningAndNext(0), this));
            } else {
                ((EndlessAdapter) lwRunning.getAdapter()).refreshDataset();
            }
            lwAll.setVisibility(View.GONE);
            lwRunning.setVisibility(View.VISIBLE);

            running.setBackgroundColor(R.color.black);
            all.setBackgroundColor(android.R.color.transparent);
            twitter.setBackgroundColor(android.R.color.transparent);
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
            if(!selected.getTitle().startsWith("break")) {
                Intent intent = new Intent(this, ShowAnnotation.class);
                intent.putExtra("annotation", selected);
                this.startActivity(intent);
            }
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

    public String getActualScreenTag() {
        return this.screen;
    }



    public void applySearch() {
        if (screen == SCREEN_RUNNING) {
            List<Annotation> i = provider.getRunningAndNext(SearchProvider.getSearchQueryBuilder(SCREEN_RUNNING), 0);
            if (i.size() > 0) {
                ((EndlessAdapter) lwRunning.getAdapter()).setItems(i, true);
                lwRunning.setSelection(0);
            } else {
                Toast.makeText(this, "Nebyly nalezeny žádné záznamy", Toast.LENGTH_LONG).show(); //better - to UI
            }
        }

        if (screen == SCREEN_ALL) {
            List<Annotation> i = provider.getAnnotations(SearchProvider.getSearchQueryBuilder(SCREEN_ALL), 0);
            if (i.size() > 0) {
                ((EndlessAdapter) lwAll.getAdapter()).setItems(i, true);
                lwAll.setSelection(0);

            } else {
                Toast.makeText(this, "Nebyly nalezeny žádné záznamy", Toast.LENGTH_LONG).show(); //better - to UI
            }
        }
    }
}


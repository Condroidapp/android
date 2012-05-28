package cz.quinix.condroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.RunningAdapter;
import cz.quinix.condroid.ui.dataLoading.AsyncTaskDialog;
import cz.quinix.condroid.ui.dataLoading.ConventionList;
import cz.quinix.condroid.ui.listeners.*;

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
    private ListenedAsyncTask task = null;

    private View openedContextMenu;


    private String screen = ProgramActivity.SCREEN_RUNNING;
    public static boolean refreshDataset = false;
    private AsyncTaskDialog asyncTaskHandler;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "cz.quinix.condroid.FastJodaTimeZoneProvider");


        this.setContentView(R.layout.program);
        provider = DataProvider.getInstance(getApplicationContext());

        lwRunning = (ListView) this.findViewById(R.id.lwRunning);
        lwAll = (ListView) this.findViewById(R.id.lwAll);
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey("activeView")) {
                screen = savedInstanceState.getString("activeView");
            }
        }

        asyncTaskHandler = (AsyncTaskDialog) getLastNonConfigurationInstance();
        if(asyncTaskHandler != null) {
            asyncTaskHandler.setParent(this);
        }

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



        TextView tFilterAll = (TextView) this.findViewById(R.id.tFilterStatusAll);
        tFilterAll.setOnClickListener(new DisableFilterListener(this));


        TextView tFilterRunning = (TextView) this.findViewById(R.id.tFilterStatusRunning);
        tFilterRunning.setOnClickListener(new DisableFilterListener(this));


        lwRunning.setOnItemClickListener(this);
        lwAll.setOnItemClickListener(this);
        registerForContextMenu(lwRunning);
        registerForContextMenu(lwAll);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("activeView",this.getActualScreenTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        this.handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
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
            ((EndlessAdapter) lwAll.getAdapter()).notifyDataSetChanged();
        }
        if (lwRunning.getAdapter() != null) {
            ((EndlessAdapter) lwRunning.getAdapter()).notifyDataSetChanged();
        }
    }

    private boolean dataAvailable() {
        if (provider.hasData()) {
            return true;
        } else {
            if(asyncTaskHandler == null) {
                this.loadData();
                return true;
            }
        }
        return false;
    }

    private void loadData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        asyncTaskHandler = new ConventionList(this);
        builder.setMessage(R.string.downloadDialog)
                .setPositiveButton(R.string.yes, asyncTaskHandler)
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
        this.findViewById(R.id.tNoData).setVisibility(View.GONE);
        if (this.screen.equals(SCREEN_ALL)) {
            if (this.lwAll.getAdapter() == null) {
                //init
                this.lwAll.setAdapter(new EndlessAdapter(this, provider.getAnnotations(null, 0)));
            } else {
                ((EndlessAdapter) lwAll.getAdapter()).notifyDataSetChanged();
            }
            lwRunning.setVisibility(View.GONE);
            lwAll.setVisibility(View.VISIBLE);

            findViewById(R.id.tFilterStatusRunning).setVisibility(View.GONE);
            if (!SearchProvider.getSearchQueryBuilder(SCREEN_ALL).isEmpty())
                findViewById(R.id.tFilterStatusAll).setVisibility(View.VISIBLE);

            all.setBackgroundColor(R.color.black);
            running.setBackgroundColor(android.R.color.transparent);
            twitter.setBackgroundColor(android.R.color.transparent);

                this.showNoDataLine(!(((EndlessAdapter) this.lwAll.getAdapter()).getDataSize() > 0));

        }
        if (this.screen.equals(SCREEN_RUNNING)) {
            if (this.lwRunning.getAdapter() == null) {
                this.lwRunning.setAdapter(new RunningAdapter(provider.getRunningAndNext(0), this));
            } else {
                ((EndlessAdapter) lwRunning.getAdapter()).notifyDataSetChanged();
            }
            lwAll.setVisibility(View.GONE);
            lwRunning.setVisibility(View.VISIBLE);

            findViewById(R.id.tFilterStatusAll).setVisibility(View.GONE);
            if (!SearchProvider.getSearchQueryBuilder(SCREEN_RUNNING).isEmpty())
                findViewById(R.id.tFilterStatusRunning).setVisibility(View.VISIBLE);

            running.setBackgroundColor(R.color.black);
            all.setBackgroundColor(android.R.color.transparent);
            twitter.setBackgroundColor(android.R.color.transparent);
                this.showNoDataLine(!(((EndlessAdapter) this.lwRunning.getAdapter()).getDataSize() > 0));

        }
    }


    public void onAsyncTaskCompleted(ListenedAsyncTask<?, ?> task) {
        asyncTaskHandler = null;
        if (!provider.hasData()) {
            Toast.makeText(this,
                    "Chyba při stahování, zkuste to prosím později.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        SearchProvider.getSearchQueryBuilder(SCREEN_ALL).clear();
        SearchProvider.getSearchQueryBuilder(SCREEN_RUNNING).clear();
        lwAll.setAdapter(null);
        lwRunning.setAdapter(null);
        this.initListView();

        SharedPreferences.Editor editor = getSharedPreferences(TAG, 0).edit();
        editor.remove("messageShown");
        editor.commit();
        initView();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if(this.asyncTaskHandler != null) {
            this.asyncTaskHandler.setParent(null);
            return this.asyncTaskHandler;
        }
        return super.onRetainNonConfigurationInstance();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView instanceof ListView) {
            Annotation selected = (Annotation) adapterView.getItemAtPosition(i);
            if (!selected.getTitle().startsWith("break")) {
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
                    ((EndlessAdapter) ((ListView) openedContextMenu).getAdapter()).notifyDataSetChanged();
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
        if (screen.equals(SCREEN_RUNNING)) {
            SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(SCREEN_RUNNING);
            List<Annotation> i = provider.getRunningAndNext(sb, 0);
            if (!sb.isEmpty()) {
                TextView tw = (TextView) findViewById(R.id.tFilterStatusRunning);
                tw.setVisibility(View.VISIBLE);
                tw.setText(sb.getReadableCondition());
            }
            else {
                findViewById(R.id.tFilterStatusRunning).setVisibility(View.GONE);
            }

            ((EndlessAdapter) lwRunning.getAdapter()).setItems(i, true);
            lwRunning.setSelection(0);
            lwRunning.setVisibility(View.VISIBLE);
                this.showNoDataLine(i.size() == 0);

        }

        if (screen.equals(SCREEN_ALL)) {
            SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(SCREEN_ALL);
            List<Annotation> i = provider.getAnnotations(sb, 0);
            if (!sb.isEmpty()) {
                TextView tw = (TextView) findViewById(R.id.tFilterStatusAll);
                tw.setVisibility(View.VISIBLE);
                tw.setText(sb.getReadableCondition());
            }
            else {
                findViewById(R.id.tFilterStatusAll).setVisibility(View.GONE);
            }


            ((EndlessAdapter) lwAll.getAdapter()).setItems(i, true);
            lwAll.setSelection(0);
            lwAll.setVisibility(View.VISIBLE);



                this.showNoDataLine(i.size()==0);

        }
    }

    public void showNoDataLine(boolean b) {
        if (b) {
            lwRunning.setVisibility(View.GONE);
            lwAll.setVisibility(View.GONE);
            this.findViewById(R.id.tNoData).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.tNoData).setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = this.getMenuInflater();
        mi.inflate(R.menu.program, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mAbout:
                new AboutDialog(this).show();
                return true;
            case R.id.mReminderList:
                Intent in = new Intent(this, ReminderList.class);
                this.startActivityForResult(in,0);

                return true;
            case R.id.mData_reload:
                this.loadData();
                return true;

            case R.id.mSettings:
                Intent i = new Intent(this, Preferences.class);
                this.startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


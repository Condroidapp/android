package cz.quinix.condroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import cz.quinix.condroid.ui.dataLoading.AsyncTaskDialog;
import cz.quinix.condroid.ui.dataLoading.ConventionList;
import cz.quinix.condroid.ui.dataLoading.Downloader;
import cz.quinix.condroid.ui.listeners.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 13.4.12
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProgramActivity extends CondroidActivity implements AsyncTaskListener, AdapterView.OnItemClickListener {

    public static final String TAG = "Condroid";
    //public static final String SCREEN_RUNNING = "running";
    //public static final String SCREEN_ALL = "all";
    public static final String SCREEN_TW = "TW";

    protected DataProvider provider;
    protected ListView lwMain = null;
    //protected ListView lwAll = null;
    private ListenedAsyncTask task = null;
    private boolean animateOnResult = false;
    private Date onResumeTime = null;

    //private View openedContextMenu;


    //private String screen = ProgramActivity.SCREEN_RUNNING;
    public static boolean refreshDataset = false;
    private AsyncTaskDialog asyncTaskHandler;
    private static RefreshRegistry refreshRegistry;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "cz.quinix.condroid.FastJodaTimeZoneProvider");

        if (refreshRegistry == null) {
            refreshRegistry = new RefreshRegistry();
        }
        refreshRegistry.registerInstance(this);


        this.setContentView(R.layout.program);
        provider = DataProvider.getInstance(getApplicationContext());

        lwMain = (ListView) this.findViewById(R.id.lwMain);

        asyncTaskHandler = (AsyncTaskDialog) getLastNonConfigurationInstance();
        if (asyncTaskHandler != null) {
            asyncTaskHandler.setParent(this);
        }

        if (this.dataAvailable()) {
            this.initView();
            this.showUpdatesDialog();
        }


        FrameLayout running = (FrameLayout) this.findViewById(R.id.fRunning);
        FrameLayout all = (FrameLayout) this.findViewById(R.id.fAll);
        //FrameLayout twitter = (FrameLayout) this.findViewById(R.id.fTwitter);
        running.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchView(Running.class);
            }
        });


        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchView(All.class);
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


        TextView tFilterAll = (TextView) this.findViewById(R.id.tFilterStatus);
        tFilterAll.setOnClickListener(new DisableFilterListener(this));


        lwMain.setOnItemClickListener(this);

        registerForContextMenu(lwMain);

        if (!SearchProvider.getSearchQueryBuilder(this.getClass().getName()).isEmpty()) {
            applySearch(); //for applying search when screen rotates
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        this.handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            SearchProvider.getSearchQueryBuilder(this.getClass().getName()).addParam(intent.getStringExtra(SearchManager.QUERY));
            intent.removeExtra(SearchManager.QUERY);
            this.applySearch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!animateOnResult) {
            overridePendingTransition(0, 0);

        }
        animateOnResult = false;
        Date now = new Date();
        if(!refreshDataset && onResumeTime != null &&
                (now.getHours() != onResumeTime.getHours() || now.getTime()-onResumeTime.getTime() > 5*60*1000)) {
            //onPause-onResume interval was more than 5 minutes or hour changed
            refreshDataset = true;
        }
        if (refreshDataset) {
            refreshRegistry.performRefresh();
            refreshDataset = false;
        }

        this.showUpdatesDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onResumeTime = new Date();
    }

    private void showUpdatesDialog() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("updates_found", false) && !sp.getBoolean("updates_found_message", false)) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(R.string.dUpdatesFoundTitle);
            ab.setMessage(R.string.dUpdatesFoundMsg);

            ab.setNegativeButton(R.string.dNotNow, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            ab.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    asyncTaskHandler = new Downloader(ProgramActivity.this, provider.getCon());
                    ((Downloader)asyncTaskHandler).invoke();
                }
            });
            ab.setCancelable(true);
            ab.create().show();

            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("updates_found_message", true);
            edit.commit();
        }
    }

    private boolean dataAvailable() {
        if (provider.hasData()) {
            return true;
        } else {
            if (asyncTaskHandler == null) {
                this.loadData(true);
                return true;
            }
        }
        return false;
    }

    private void loadData() {
        this.loadData(false);
    }

    private void loadData(boolean forceFull) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        if(provider.hasData() && !forceFull) {
            asyncTaskHandler = new Downloader(this, provider.getCon());
            builder.setMessage(R.string.updateOrFullDialog)
                    .setNegativeButton(R.string.full, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            asyncTaskHandler = new ConventionList(ProgramActivity.this);
                            asyncTaskHandler.onClick(dialogInterface, i);
                        }
                    })
                    .setPositiveButton(R.string.update, asyncTaskHandler);
        }
        else {


            asyncTaskHandler = new ConventionList(this);
            builder.setMessage(R.string.downloadDialog)
                    .setCancelable(false)
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


        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initView() {
        this.initListView();
        this.handleIntent(this.getIntent());
    }

    private void switchView(Class<?> viewName) {
        if (this.getClass() != viewName) {
            Intent intent = new Intent(this, viewName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.startActivity(intent);
        }
    }


    protected void initListView() {
        findViewById(R.id.tFilterStatus).setVisibility(View.GONE);
        if (!SearchProvider.getSearchQueryBuilder(this.getClass().getName()).isEmpty())
            findViewById(R.id.tFilterStatus).setVisibility(View.VISIBLE);
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
        //SearchProvider.getSearchQueryBuilders().clear(); why is this in here?!

        refreshDataset = true;
        this.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        if(!sp.getBoolean("con_specific_message", false)) {
            if(!provider.getCon().getMessage().equals("")) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setTitle(provider.getCon().getName());
                ab.setMessage(provider.getCon().getMessage());
                ab.setCancelable(true);
                ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ab.create().show();
            }
            editor.putBoolean("con_specific_message", true);

        }

        editor.remove("updates_found");
        editor.remove("updates_found_message");
        editor.remove("updates_found_time");

        editor.commit();

        Preferences.planUpdateService(this);

        initView();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (this.asyncTaskHandler != null) {
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
                this.animateOnResult = true;
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
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int menuItemIndex = item.getItemId();
        Annotation an = (Annotation) this.lwMain.getItemAtPosition(info.position);

        switch (menuItemIndex) {
            case 0:
                new ShareProgramListener(this).invoke(an);
                break;
            case 1:
                new MakeFavoritedListener(this).invoke(an, null);
                ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
                break;
            case 2:
                new SetReminderListener(this).invoke(an);
            default:
                break;
        }

        return true;
    }

    public void applySearch() {
        SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        List<Annotation> i = this.loadData(sb, 0);
        if (!sb.isEmpty()) {
            TextView tw = (TextView) findViewById(R.id.tFilterStatus);
            tw.setVisibility(View.VISIBLE);
            tw.setText(sb.getReadableCondition());
        } else {
            findViewById(R.id.tFilterStatus).setVisibility(View.GONE);
        }


        ((EndlessAdapter) lwMain.getAdapter()).setItems(i, true);
        lwMain.setSelection(0);
        lwMain.setVisibility(View.VISIBLE);
        this.showNoDataLine(i.size() == 0);

    }

    protected abstract List<Annotation> loadData(SearchQueryBuilder sb, int page);

    public void showNoDataLine(boolean b) {
        if (b) {
            lwMain.setVisibility(View.GONE);
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
                this.startActivityForResult(in, 0);

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

    private class RefreshRegistry {
        private List<ProgramActivity> list = new ArrayList<ProgramActivity>();

        void registerInstance(ProgramActivity p) {
            list.add(p);
        }

        void performRefresh() {
            for (ProgramActivity p : list) {
                p.applySearch();
            }
        }
    }

    public void stopAsyncTask() {
        if(asyncTaskHandler != null) {
            asyncTaskHandler = null;
        }
    }
}
package cz.quinix.condroid.ui.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.AboutDialog;
import cz.quinix.condroid.ui.Preferences;
import cz.quinix.condroid.ui.ReminderList;
import cz.quinix.condroid.ui.adapters.GroupedAdapter;
import cz.quinix.condroid.ui.dataLoading.AsyncTaskDialog;
import cz.quinix.condroid.ui.dataLoading.Downloader;
import cz.quinix.condroid.ui.fragments.AllAnnotations;
import cz.quinix.condroid.ui.fragments.CondroidFragment;
import cz.quinix.condroid.ui.fragments.RefreshRegistry;
import cz.quinix.condroid.ui.fragments.RunningAnnotations;
import cz.quinix.condroid.ui.listeners.DisableFilterListener;
import cz.quinix.condroid.ui.listeners.FilterListener;
import cz.quinix.condroid.ui.listeners.TabListener;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 13.4.12
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ProgramActivity extends RoboSherlockFragmentActivity implements AdapterView.OnItemClickListener {

    @Inject private DataProvider provider;

    private Date onResumeTime = null;
    public static boolean refreshDataset = false;
    private static AsyncTaskDialog asyncTaskHandler;
    private static RefreshRegistry refreshRegistry;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.program);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab runningTab = actionBar.newTab().setText(R.string.tRunning);
        ActionBar.Tab allTab = actionBar.newTab().setText(R.string.tAll);

        CondroidFragment runningFragment = new RunningAnnotations();
        CondroidFragment allFragment = new AllAnnotations();

        runningTab.setTabListener(new TabListener(runningFragment));
        allTab.setTabListener(new TabListener(allFragment));

        actionBar.addTab(runningTab);
        actionBar.addTab(allTab);

        if (savedInstanceState != null) {
            int tab = savedInstanceState.getInt("selected_tab", 0);
            if (tab != actionBar.getSelectedTab().getPosition()) {
                actionBar.selectTab(actionBar.getTabAt(tab));
            }
        }

        if (this.dataAvailable()) {
            this.initView();
            this.showUpdatesDialog();
        }

        TextView tFilterAll = (TextView) this.findViewById(R.id.tFilterStatus);
        tFilterAll.setOnClickListener(new DisableFilterListener(this));

        if (refreshRegistry == null) {
            refreshRegistry = CondroidFragment.getRefreshRegistry();
        }
        Preferences.planUpdateService(this);

        if (TabListener.activeFragment != null && !SearchProvider.getSearchQueryBuilder(TabListener.activeFragment.getClass().getName()).isEmpty()) {
            applySearch(); //for applying search when screen rotates
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        this.handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()) && TabListener.activeFragment != null) {
            SearchProvider.getSearchQueryBuilder(TabListener.activeFragment.getClass().getName()).addParam(intent.getStringExtra(SearchManager.QUERY));
            intent.removeExtra(SearchManager.QUERY);
            this.applySearch();
        }
    }

    public void applySearch() {
        if (TabListener.activeFragment != null) {
            TabListener.activeFragment.applySearch();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_tab", this.getSupportActionBar().getSelectedTab().getPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Date now = new Date();
        if (!refreshDataset && onResumeTime != null &&
                (now.getHours() != onResumeTime.getHours() || now.getTime() - onResumeTime.getTime() > 5 * 60 * 1000)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (asyncTaskHandler != null) {
            asyncTaskHandler.setParent(null);
        }
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
                    ((Downloader) asyncTaskHandler).invoke();
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
        }
        return false;
    }



    private void initView() {
        this.initListView();
        this.handleIntent(this.getIntent());
    }

    protected void initListView() {
        findViewById(R.id.tFilterStatus).setVisibility(View.GONE);
        if (TabListener.activeFragment != null && !SearchProvider.getSearchQueryBuilder(TabListener.activeFragment.getClass().getName()).isEmpty())
            findViewById(R.id.tFilterStatus).setVisibility(View.VISIBLE);
    }


    public void onAsyncTaskCompleted() {
        asyncTaskHandler = null;
        if (!provider.hasData()) {
            Toast.makeText(this,
                    "Chyba při stahování, zkuste to prosím později.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //SearchProvider.getSearchQueryBuilders().clear(); why is this in here?!

        if (refreshRegistry != null) {
            refreshRegistry.performRefresh();
            refreshDataset = false;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        if (!sp.getBoolean("con_specific_message", false)) {
            if (!provider.getCon().getMessage().equals("")) {
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if (adapterView instanceof ListView) {
                Object item = adapterView.getAdapter().getItem(i);
                Annotation selected;
                if(item instanceof GroupedAdapter.Entry && !((GroupedAdapter.Entry) item).isSeparator()) {
                    selected = ((GroupedAdapter.Entry) item).annotation;
                } else {
                    selected = (Annotation) item;
                }

                if (selected != null) {
                    Intent intent = new Intent(this, ShowAnnotation.class);
                    intent.putExtra("annotation", selected);
                    this.startActivity(intent);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Condroid", "", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = this.getSupportMenuInflater();
        mi.inflate(R.menu.program, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mAbout:
                new AboutDialog(this);
                return true;
            case R.id.mReminderList:
                Intent in = new Intent(this, ReminderList.class);
                this.startActivityForResult(in, 0);

                return true;
            case R.id.mData_reload:
               // this.loadData();
                return true;

            case R.id.mSettings:
                Intent i = new Intent(this, Preferences.class);
                this.startActivity(i);
                return true;
            case R.id.mFilter:
                new FilterListener(this).invoke();
                return true;
            case R.id.mSearch:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
package cz.quinix.condroid.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.adapters.DrawerAdapter;
import cz.quinix.condroid.ui.dataLoading.Downloader;
import cz.quinix.condroid.ui.dataLoading.UpdateChecker;
import cz.quinix.condroid.ui.fragments.TabsFragment;
import cz.quinix.condroid.ui.listeners.DrawerItemClickListener;
import cz.quinix.condroid.ui.listeners.FilterListener;

public class MainActivity extends RoboSherlockFragmentActivity implements ITaskListener {

    private DrawerLayout mDrawerLayout;
    @Inject
    private DataProvider provider;
    private TabsFragment tabsFragment;
    private ViewGroup mDrawerContent;
    private Menu optionsMenu;
    private ActionBarDrawerToggle mDrawerToggle;
    private UpdateChecker updateChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        String[] drawerContent = getResources().getStringArray(R.array.drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContent = (ViewGroup) findViewById(R.id.left_drawer);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

        // set up the drawer's list view with items and click listener
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerList.setAdapter(new DrawerAdapter(this, drawerContent));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.fa_folder_open, /* "open drawer" description for accessibility */
                R.string.fa_sort_numeric_desc/* "close drawer" description for accessibility */
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            tabsFragment = TabsFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, tabsFragment, TabsFragment.TAG).commit();
        } else {
            tabsFragment = (TabsFragment) getSupportFragmentManager().findFragmentByTag(TabsFragment.TAG);
        }

        Convention con = provider.getCon();

        TextView t = (TextView) findViewById(R.id.tEventName);
        t.setText(con.getName());

        TextView t2 = (TextView) findViewById(R.id.tEventDate);
        t2.setText(con.getDate());

        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        this.handleIntent(intent);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            tabsFragment.handleSearch(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater mi = this.getSupportMenuInflater();
        mi.inflate(R.menu.program, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            new UpdateChecker(this, provider.getCon()).execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (mDrawerLayout.isDrawerOpen(mDrawerContent)) {
                    mDrawerLayout.closeDrawer(mDrawerContent);
                } else {
                    mDrawerLayout.openDrawer(mDrawerContent);
                }
                return true;
            }
            case R.id.mData_reload:
                this.loadData();
                return true;

            case R.id.mFilter:
                new FilterListener(this.tabsFragment.getActiveFragment(), this.provider).invoke();
                return true;
            case R.id.mSearch:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData() {
        this.setRefreshActionButtonState(true);
        Downloader d = new Downloader(MainActivity.this, provider.getCon(), true, false);
        d.invoke();
    }

    @Override
    public void onTaskCompleted(AListenedAsyncTask<?, ?> task) {
        if (task instanceof DatabaseLoader) {
            int results = ((DatabaseLoader) task).getResults();
            Toast.makeText(this, this.getResources().getQuantityString(R.plurals.found_updates, results, results), Toast.LENGTH_LONG).show();
            if (tabsFragment != null) {
                tabsFragment.refreshFragments();
            }
        } else {
            Log.w(this.getClass().getName(), "Instance of " + task.getClass().getName() + " is not supported in this handler.");
        }
        this.setRefreshActionButtonState(false);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onTaskErrored(AListenedAsyncTask task) {
        this.setRefreshActionButtonState(false);
    }

    public void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawerContent)) {
            mDrawerLayout.closeDrawer(mDrawerContent);
        }
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.mData_reload);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}

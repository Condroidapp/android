package cz.quinix.condroid.ui.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

import java.util.Date;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.AboutDialog;
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
    private ServiceConnection mPlayService;
    private AboutDialog aboutDialog; //for inapp handle - needs refactoring


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setupDrawer();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon


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

    private void showDonate() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        boolean shown = sp.getBoolean("donate_dialog_shown", false);

        if(shown) {
            return;
        }
        int days = sp.getInt("donate_days_used", 0);

        long prevDate = sp.getLong("donate_prev_date", new Date().getTime());
        Date previous = new Date();
        previous.setTime(prevDate);

        if(previous.getDate() != (new Date()).getDate()) {
            days++;
        }

        SharedPreferences.Editor editor = sp.edit();
        if(days > 3) {

            editor.putBoolean("donate_dialog_shown", true);
            AboutDialog a = new AboutDialog(this);
            this.setActivityResultListener(a);
            a.showDonate();
        }
        editor.putInt("donate_days_used", days);
        editor.putLong("donate_prev_date", (new Date()).getTime());

        editor.commit();

    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContent = (ViewGroup) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawerOpen, R.string.drawerClose
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        int[] views = {R.id.tdRestaurants, R.id.tdMap, R.id.tdWeb, R.id.tdAnother, R.id.tdReminders, R.id.tdSettings, R.id.tdAbout};

        for (int id : views) {
            TextView view = (TextView) findViewById(id);
            view.setOnClickListener(new DrawerItemClickListener(this, provider));
        }

        if (provider.getCon().getGps() == null) {
            findViewById(R.id.tdMap).setVisibility(View.GONE);
        }
        if (provider.getPlaces() == null || provider.getPlaces().size() == 0) {
            findViewById(R.id.tdRestaurants).setVisibility(View.GONE);
        }


    }


    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        this.handleIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Condroid", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (aboutDialog != null && aboutDialog.getIabHelper() != null && !aboutDialog.getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("Condroid", "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.showDonate();

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

        if (hasFocus) {
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

    public void setActivityResultListener(AboutDialog aboutDialog) {

        this.aboutDialog = aboutDialog;
    }
}

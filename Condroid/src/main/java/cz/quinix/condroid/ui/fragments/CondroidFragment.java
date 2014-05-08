package cz.quinix.condroid.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 16.6.12
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class CondroidFragment extends RoboSherlockFragment {

    private View view;
    protected ListView lwMain;

    private static RefreshRegistry refreshRegistry;

    static {
        refreshRegistry = new RefreshRegistry();
    }

    @Inject protected DataProvider dataProvider;

    protected CondroidFragment() {
        refreshRegistry.registerInstance(this);
    }

    public static RefreshRegistry getRefreshRegistry() {
        return refreshRegistry;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_view, container, false);
        }
        return view;
    }

    public View getView() {
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();    //To change body of overridden methods use File | Settings | File Templates.
        if (lwMain == null) {
            lwMain = (ListView) this.getView().findViewById(R.id.lwMain);
            initListView();
        } else {
            ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
        }
        this.updateSearchField(getActivity());

    }

    protected void initListView() {
        if (this.lwMain.getAdapter() == null) {
            //init
            this.lwMain.setAdapter(this.getListViewAdapter());
            this.lwMain.setOnItemClickListener((AdapterView.OnItemClickListener) this.getActivity());
            if (lwMain.getAdapter().getCount() == 0) {
                lwMain.setVisibility(View.GONE);
                this.getView().findViewById(R.id.tNoData).setVisibility(View.VISIBLE);
            }
            this.registerForContextMenu(lwMain);
        } else {
            ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
        }
    }

    protected abstract EndlessAdapter getListViewAdapter();

    public boolean onContextItemSelected(android.view.MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int menuItemIndex = item.getItemId();
        Annotation an = (Annotation) this.lwMain.getItemAtPosition(info.position);

        switch (menuItemIndex) {
            case 0:
                new ShareProgramListener(this.getActivity()).invoke(an);
                break;
            case 1:
                new MakeFavoritedListener(this.getActivity()).invoke(an);
                ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
                break;
            case 2:
                new SetReminderListener(this.getActivity()).invoke(an);
            default:
                break;
        }

        return true;
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

    private void updateSearchField(Activity activity) {
        if (activity != null) {
            SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
            TextView tw = (TextView) activity.findViewById(R.id.tFilterStatus);
            if (!sb.isEmpty()) {
                tw.setVisibility(View.VISIBLE);
                tw.setText(sb.getReadableCondition());
            } else {
                tw.setVisibility(View.GONE);
            }
        }
    }

    public void applySearch() {
        if (lwMain == null) {
            return; //not initiated - fuck off
        }
        SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        List<Annotation> i = this.loadData(sb, 0);
        if (this.getActivity() != null) {
            this.updateSearchField(getActivity());
        }

        ((EndlessAdapter) lwMain.getAdapter()).setItems(i, true);
        lwMain.setSelection(0);
        lwMain.setVisibility(View.VISIBLE);
        if (i.size() == 0) {
            lwMain.setVisibility(View.GONE);
            this.getView().findViewById(R.id.tNoData).setVisibility(View.VISIBLE);
        } else {
            this.getView().findViewById(R.id.tNoData).setVisibility(View.GONE);
        }

    }

    protected abstract List<Annotation> loadData(SearchQueryBuilder sb, int i);

}

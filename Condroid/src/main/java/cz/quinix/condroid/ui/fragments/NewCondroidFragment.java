package cz.quinix.condroid.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.activities.ShowAnnotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.GroupedAdapter;
import cz.quinix.condroid.ui.listeners.DisableFilterListener;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;
import roboguice.inject.InjectView;

/**
 * Created by Jan on 1. 6. 2014.
 */
public abstract class NewCondroidFragment extends RoboSherlockFragment implements AdapterView.OnItemClickListener {


    @Inject
    protected DataProvider dataProvider;
    @InjectView(R.id.lwMain)
    private ListView lwMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initListView();
        this.updateSearchField();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.lwMain != null && this.lwMain.getAdapter() != null) {
            ((EndlessAdapter) this.lwMain.getAdapter()).notifyDataSetChanged();
        }
    }

    protected void initListView() {
        if (this.lwMain.getAdapter() == null) {
            //init
            this.lwMain.setAdapter(this.createListViewAdapter());
            this.lwMain.setOnItemClickListener(this);
            if (lwMain.getAdapter().getCount() == 0) {
                lwMain.setVisibility(View.GONE);
                this.getView().findViewById(R.id.tNoData).setVisibility(View.VISIBLE);
            }
            this.registerForContextMenu(lwMain);
        } else {
            ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
        }

        this.updateSearchField();
    }




    protected abstract EndlessAdapter createListViewAdapter();

    protected EndlessAdapter getListViewAdapter() {
        return (EndlessAdapter) lwMain.getAdapter();
    }

    public boolean onContextItemSelected(android.view.MenuItem item) {

        if (!getUserVisibleHint()) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int menuItemIndex = item.getItemId();
        Object object = this.lwMain.getItemAtPosition(info.position);
        Annotation selected;
        if (object instanceof GroupedAdapter.Entry && !((GroupedAdapter.Entry) object).isSeparator()) {
            selected = ((GroupedAdapter.Entry) object).annotation;
        } else {
            selected = (Annotation) object;
        }

        switch (menuItemIndex) {
            case 0:
                new ShareProgramListener(this.getActivity()).invoke(selected);
                break;
            case 1:
                new MakeFavoritedListener(this.getActivity()).invoke(selected);
                ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
                break;
            case 2:
                new SetReminderListener(this.getActivity(), dataProvider).invoke(selected);
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
            Object an = ((ListView) v).getItemAtPosition(info.position);
            Annotation selected;
            if (an instanceof GroupedAdapter.Entry && !((GroupedAdapter.Entry) an).isSeparator()) {
                selected = ((GroupedAdapter.Entry) an).annotation;
            } else {
                selected = (Annotation) an;
            }
            if (selected != null) {
                menu.setHeaderTitle(selected.getTitle());
                String[] menuItems = getResources().getStringArray(R.array.annotationContext);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        }

    }

    private void updateSearchField() {
        SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        TextView tw = (TextView) this.getView().findViewById(R.id.tFilterStatus);
        if (!sb.isEmpty()) {
            tw.setVisibility(View.VISIBLE);
            tw.setText(sb.getReadableCondition());
            tw.setOnClickListener(new DisableFilterListener(this));
        } else {
            tw.setVisibility(View.GONE);
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (lwMain != null) {
                Object item = lwMain.getAdapter().getItem(position);
                Annotation selected;
                if (item instanceof GroupedAdapter.Entry)
                    if (!((GroupedAdapter.Entry) item).isSeparator()) {
                        selected = ((GroupedAdapter.Entry) item).annotation;
                    } else {
                        return;
                    }
                else {
                    selected = (Annotation) item;
                }

                if (selected != null) {
                    Intent intent = new Intent(this.getActivity(), ShowAnnotation.class);
                    intent.putExtra("annotation", selected);
                    this.startActivity(intent);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Condroid", "", e);
        }
    }

    public void handleSearch(String query) {
        SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());

        sb.addParam(query);

        this.refresh(true);
    }

    protected abstract List<Annotation> loadData(SearchQueryBuilder sb, int page);

    public void refresh() {
        this.refresh(false);
    }

    public void refresh(boolean resetPosition) {
        if (this.getView() == null) {
            return;
        }
        SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        List<Annotation> i = this.loadData(sb, 0);
        this.updateSearchField();


        ((EndlessAdapter) lwMain.getAdapter()).setItems(i);

        if (resetPosition) {

        }

        lwMain.setVisibility(View.VISIBLE);
        if (i.size() == 0) {
            lwMain.setVisibility(View.GONE);
            this.getView().findViewById(R.id.tNoData).setVisibility(View.VISIBLE);
        } else {
            this.getView().findViewById(R.id.tNoData).setVisibility(View.GONE);
        }
    }

    public void resetList() {
        if (lwMain != null) {
            lwMain.setSelection(0);
        }
    }
}

package cz.quinix.condroid.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import cz.quinix.condroid.ui.adapters.AnnotationAdapter;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.GroupedAdapter;
import cz.quinix.condroid.ui.adapters.IAdapterDataProvider;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;
import roboguice.inject.InjectView;

/**
 * Created by Jan on 1. 6. 2014.
 */
public class FullListFragment extends NewCondroidFragment {

    public static NewCondroidFragment newInstance() {
        return new FullListFragment();
    }

    @Override
    protected EndlessAdapter createListViewAdapter() {
        final SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        List<Annotation> annotations = this.loadData(sb, 0);

        return new EndlessAdapter(this.getActivity(), new AnnotationAdapter(this.getActivity(), annotations), new IAdapterDataProvider() {
            @Override
            public List getData(int page) {
                return loadData(sb, page);
            }
        });
    }

    @Override
    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return dataProvider.getAnnotations(sb, page);
    }

    /*public void applySearch() {
        if (lwMain == null) {
            return; //not initiated - fuck off
        }
        SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        List<Annotation> i = this.loadData(sb, 0);
        if (this.getActivity() != null) {
            this.updateSearchField();
        }

        ((EndlessAdapter) lwMain.getAdapter()).setItems(i);
        lwMain.setSelection(0);
        lwMain.setVisibility(View.VISIBLE);
        if (i.size() == 0) {
            lwMain.setVisibility(View.GONE);
            this.getView().findViewById(R.id.tNoData).setVisibility(View.VISIBLE);
        } else {
            this.getView().findViewById(R.id.tNoData).setVisibility(View.GONE);
        }

    }*/

}

package cz.quinix.condroid.ui.fragments;

import java.util.List;

import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.AnnotationAdapter;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.IAdapterDataProvider;

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

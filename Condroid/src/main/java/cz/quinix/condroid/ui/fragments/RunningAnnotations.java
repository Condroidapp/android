package cz.quinix.condroid.ui.fragments;

import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.RunningAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 28.5.12
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class RunningAnnotations extends CondroidFragment {

    @Override
    protected EndlessAdapter getListViewAdapter() {
        return new RunningAdapter(this.loadData(SearchProvider.getSearchQueryBuilder(this.getClass().getName()), 0), this.getActivity());
    }

    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return this.dataProvider.getRunningAndNext(sb, page);
    }
}
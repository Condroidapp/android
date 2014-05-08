package cz.quinix.condroid.ui.fragments;

import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.IAdapterDataProvider;
import cz.quinix.condroid.ui.adapters.AnnotationAdapter;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 28.5.12
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class AllAnnotations extends CondroidFragment {
    @Override
    protected EndlessAdapter getListViewAdapter() {
        final SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
        List<Annotation> annotations = this.loadData(sb, 0);

        return new EndlessAdapter(this.getActivity(), new AnnotationAdapter(this.getActivity(), annotations), new IAdapterDataProvider() {
            @Override
            public List getData(int page) {
                return loadData(sb, page);
            }
        });
    }

    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return this.dataProvider.getAnnotations(sb, page);
    }


}
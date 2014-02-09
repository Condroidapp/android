package cz.quinix.condroid.ui;

import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 28.5.12
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class All extends CondroidFragment {
    @Override
    protected EndlessAdapter getListViewAdapter() {
        return new EndlessAdapter(this.getActivity(), this.loadData(SearchProvider.getSearchQueryBuilder(this.getClass().getName()), 0));
    }

    /*public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().selectTab(getSupportActionBar().getTabAt(1));
    }

    @Override
    protected void initListView() {
        if (this.lwMain.getAdapter() == null) {
            //init
            this.lwMain.setAdapter();
        } else {
            ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
        }
        super.initListView();    //To change body of overridden methods use File | Settings | File Templates.
    }
*/
    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return DataProvider.getInstance(this.getActivity()).getAnnotations(sb, page);
    }


}
package cz.quinix.condroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
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
public class Running extends CondroidFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Preferences.planUpdateService(this.getActivity());
        /*if(lwMain.getAdapter().getCount() == 0 && SearchProvider.getSearchQueryBuilder(this.getClass().getName()).isEmpty() && provider.hasData()) {
            Intent intent = new Intent(this, All.class);
            startActivity(intent);
        } */


    }

    @Override
    protected EndlessAdapter getListViewAdapter() {
        return new RunningAdapter(this.loadData(SearchProvider.getSearchQueryBuilder(this.getClass().getName()), 0), this.getActivity());
    }

    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return DataProvider.getInstance(this.getActivity()).getRunningAndNext(sb, page);
    }
}
package cz.quinix.condroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import cz.quinix.condroid.R;
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
public class All extends ProgramActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.findViewById(R.id.fAll).setBackgroundColor(R.color.black);
    }

    @Override
    protected void initListView() {
        if (this.lwMain.getAdapter() == null) {
            //init
            this.lwMain.setAdapter(new EndlessAdapter(this, this.loadData(null,0)));
        } else {
            ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
        }
        super.initListView();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return this.provider.getAnnotations(sb, page);
    }


}
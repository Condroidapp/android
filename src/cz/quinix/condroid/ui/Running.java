package cz.quinix.condroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import cz.quinix.condroid.R;
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
public class Running extends ProgramActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.findViewById(R.id.fRunning).setBackgroundColor(R.color.black);
        Preferences.planUpdateService(this);
    }

    @Override
    protected void initListView() {
        if (this.lwMain.getAdapter() == null) {
            //init
            this.lwMain.setAdapter(new RunningAdapter(this.loadData(null,0),this));
        } else {
            ((EndlessAdapter) lwMain.getAdapter()).notifyDataSetChanged();
        }
        super.initListView();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
        return this.provider.getRunningAndNext(sb,page);
    }
}
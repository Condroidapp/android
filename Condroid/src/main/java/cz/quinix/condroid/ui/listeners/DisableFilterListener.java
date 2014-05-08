package cz.quinix.condroid.ui.listeners;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.ui.activities.ProgramActivity;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 27.4.12
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class DisableFilterListener implements View.OnClickListener {
    private ProgramActivity activity;

    public DisableFilterListener(ProgramActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab.setTitle(R.string.disableFilter);
        ab.setItems(R.array.disableFilterBy, new DisableFilterTypeSelected(activity, SearchProvider.getSearchQueryBuilder(TabListener.activeFragment.getClass().getName())));

        ab.create().show();
    }
}

class DisableFilterTypeSelected implements Dialog.OnClickListener {


    private ProgramActivity activity;
    private SearchQueryBuilder search;

    public DisableFilterTypeSelected(ProgramActivity activity, SearchQueryBuilder searchQueryBuilder) {
        this.activity = activity;
        this.search = searchQueryBuilder;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case 0:
                //fulltext
                search.removeParam(new String());
                break;
            case 1:
                //date
                search.removeParam(new Date());
                break;
            case 2:
                //linie
                search.removeParam(new ProgramLine());
                break;
            case 3:
                //favorites
                search.removeParam(new Object());
                break;
            default:
                search.clear();
                //all
                break;
        }
        activity.applySearch();
    }
}

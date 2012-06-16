package cz.quinix.condroid.ui.listeners;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import cz.quinix.condroid.R;
import cz.quinix.condroid.ui.CondroidFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 16.6.12
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class TabListener implements ActionBar.TabListener {
    public static CondroidFragment activeFragment;
    public CondroidFragment fragment;

    public TabListener(CondroidFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragment_container, fragment);
        activeFragment = fragment;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ((ViewGroup) fragment.getView().getParent()).removeView(fragment.getView());
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Toast.makeText(fragment.getActivity(), "Reselected "+fragment.getClass().getName(), Toast.LENGTH_LONG).show();
    }
}

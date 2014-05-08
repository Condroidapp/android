package cz.quinix.condroid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Jan on 8. 5. 2014.
*/
public class RefreshRegistry {
    private List<CondroidFragment> list = new ArrayList<CondroidFragment>();

    public void registerInstance(CondroidFragment p) {
        list.add(p);
    }

    public void performRefresh() {
        for (CondroidFragment p : list) {
            p.applySearch();
        }
    }
}

package cz.quinix.condroid.ui.listeners;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.ui.fragments.NewCondroidFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 27.4.12
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class DisableFilterListener implements View.OnClickListener {

	private NewCondroidFragment parent;

	public DisableFilterListener(NewCondroidFragment activity) {
		this.parent = activity;
	}

	@Override
	public void onClick(View view) {
		String[] data = parent.getResources().getStringArray(R.array.disableFilterBy);

		Map<Integer, Object> selected = new HashMap<Integer, Object>();
		List<String> values = new ArrayList<String>();

		SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(parent.getClass().getName());

		if (sb.hasParam(String.class.getName())) {
			values.add(data[0]);
			selected.put(values.size() - 1, "");
		}
		if (sb.hasParam(Date.class.getName())) {
			values.add(data[1]);
			selected.put(values.size() - 1, new Date());
		}
		if (sb.hasParam(ProgramLine.class.getName())) {
			values.add(data[2]);
			selected.put(values.size() - 1, new ProgramLine());
		}
		if (sb.hasParam(Object.class.getName())) {
			values.add(data[3]);
			selected.put(values.size() - 1, new Object());
		}
		values.add(data[4]);
		selected.put(values.size() - 1, -1);
		DisableFilterTypeSelected listener = new DisableFilterTypeSelected(parent, sb, selected);

		if (selected.size() <= 2) {
			listener.onClick(null, -1);
			return;
		}
		AlertDialog.Builder ab = new AlertDialog.Builder(parent.getActivity());
		ab.setTitle(R.string.disableFilter);

		ab.setItems(values.toArray(new String[selected.size()]), listener);

		ab.create().show();
	}
}

class DisableFilterTypeSelected implements Dialog.OnClickListener {

	private NewCondroidFragment parent;

	private SearchQueryBuilder search;

	private Map<Integer, Object> selected;

	public DisableFilterTypeSelected(NewCondroidFragment activity, SearchQueryBuilder searchQueryBuilder, Map<Integer, Object> selected) {
		this.parent = activity;
		this.search = searchQueryBuilder;
		this.selected = selected;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		if (i == -1) {
			search.clear();
		} else if (this.selected.containsKey(i)) {
			Object value = selected.get(i);
			if (value instanceof Integer && ((Integer) value).intValue() == -1) {
				search.clear();
			} else {
				search.removeParam(value);
			}
		}
		parent.refresh();
	}

}

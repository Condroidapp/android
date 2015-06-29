package cz.quinix.condroid.ui.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.ICondition;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.ui.fragments.NewCondroidFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 24.4.12
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class FilterListener {

	private NewCondroidFragment parent;

	private DataProvider provider;

	public FilterListener(NewCondroidFragment parent, DataProvider provider) {
		this.parent = parent;

		this.provider = provider;
	}

	public void invoke() {
		AlertDialog.Builder ab = new AlertDialog.Builder(parent.getActivity());
		ab.setTitle(R.string.chooseFilter);
		ab.setItems(R.array.filterBy, new FilterTypeSelected(parent, provider, SearchProvider.getSearchQueryBuilder(parent.getClass().getName())));

		ab.create().show();
	}
}

class FilterTypeSelected implements DialogInterface.OnClickListener {

	private NewCondroidFragment parent;

	private DataProvider provider;

	private SearchQueryBuilder search;

	public FilterTypeSelected(NewCondroidFragment parent, DataProvider provider, SearchQueryBuilder search) {
		this.parent = parent;
		this.provider = provider;
		this.search = search;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int which) {
		if (which == 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
			builder.setTitle(R.string.dPickLine);

			Map<Integer, ProgramLine> pl = provider.getProgramLines();
			int i = 0;
			final String[] pls = new String[pl.size()];

			for (ProgramLine p : pl.values()) {
				pls[i++] = p.getName();
			}
			Arrays.sort(pls);
			builder.setItems(pls, new ProgramLineFilter(parent, provider, search, pls));

			builder.create().show();
		}

		if (which == 0) {
			AlertDialog.Builder build = new AlertDialog.Builder(parent.getActivity());
			build.setTitle(R.string.dPickDate);
			List<Date> dates = provider.getDates();
			if (dates.size() == 0) {
				Toast.makeText(parent.getActivity(), R.string.noDatesAvailable, Toast.LENGTH_LONG).show();
				return;
			}
			String[] ds = new String[dates.size()];
			int j = 0;

			DateFormat df = new SimpleDateFormat(
					"EEEE d. M. yyyy", new Locale("cs",
					"CZ")
			);

			for (Date date : dates) {
				char[] c = df.format(date).toCharArray();
				c[0] = Character.toUpperCase(c[0]);
				ds[j++] = new String(c);
			}
			build.setItems(ds, new DateFilter(parent, search, ds));
			build.create().show();
		}
		if (which == 2) {
			search.addParam(new ICondition() {
				@Override
				public String getCondition() {
					String condition = "";
					List<Integer> f = provider.getFavorited();
					if (f.size() > 0) {
						for (Integer integer : f) {
							if (!condition.equals("")) {
								condition += ", ";
							}
							condition += integer.toString();
						}
						condition = "pid IN (" + condition + ")";
					} else {
						return "1=0";
					}
					return condition;
				}

				@Override
				public String getReadable() {
					return "Oblíbené";
				}
			}, Object.class.getName());
			parent.refresh();
		}
	}
}

class ProgramLineFilter implements DialogInterface.OnClickListener {

	private NewCondroidFragment parent;

	private DataProvider provider;

	private SearchQueryBuilder search;

	private String[] items;

	public ProgramLineFilter(NewCondroidFragment parent, DataProvider provider, SearchQueryBuilder search, String[] items) {
		this.parent = parent;
		this.provider = provider;
		this.search = search;
		this.items = items;
	}

	public void onClick(DialogInterface dialog, int which) {
		int lid = 0;
		String value = items[which];
		if (value
				.equals("- Zrušit filtr")) {
			search
					.removeParam(new ProgramLine());
		} else {
			for (ProgramLine item : provider.getProgramLines().values()) {
				if (item.getName().equals(value)) {
					lid = item.getLid();
					break;
				}
			}

			ProgramLine pl = new ProgramLine();
			pl.setLid(lid);
			pl.setName(value);
			search.addParam(pl);
		}
		parent.refresh();
	}

}

class DateFilter implements DialogInterface.OnClickListener {

	private NewCondroidFragment parent;

	private SearchQueryBuilder search;

	private String[] items;

	public DateFilter(NewCondroidFragment parent, SearchQueryBuilder search, String[] items) {
		this.parent = parent;
		this.search = search;
		this.items = items;
	}

	public void onClick(DialogInterface dialog, int which) {

		DateFormat df = new SimpleDateFormat("EEEE d. M. yyyy", new Locale("cs", "CZ"));
		if (items[which].equals("- Zrušit filtr")) {
			search.removeParam(new Date());
		} else {
			try {
				Date d = df.parse(items[which]);
				search.addParam(d);
			} catch (ParseException ignored) {
			}
		}

		parent.refresh();

	}
}
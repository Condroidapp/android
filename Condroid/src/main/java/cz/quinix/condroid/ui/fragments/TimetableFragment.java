package cz.quinix.condroid.ui.fragments;

import java.util.List;

import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.adapters.GroupedAdapter;
import cz.quinix.condroid.ui.adapters.IAdapterDataProvider;
import cz.quinix.condroid.util.DateTimeFactory;

public class TimetableFragment extends NewCondroidFragment {

	private GroupedAdapter wrapped;

	public static NewCondroidFragment newInstance() {
		return new TimetableFragment();
	}

	@Override
	protected EndlessAdapter createListViewAdapter() {

		final SearchQueryBuilder sb = SearchProvider.getSearchQueryBuilder(this.getClass().getName());
		List<Annotation> annotations = this.loadData(sb, 0);

		wrapped = new GroupedAdapter(this.getActivity(), annotations);
		return new EndlessAdapter(this.getActivity(), wrapped, new IAdapterDataProvider() {
			@Override
			public List getData(int page) {
				return loadData(sb, page);
			}
		});

	}

	protected List<Annotation> loadData(SearchQueryBuilder sb, int page) {
		return this.dataProvider.getRunningAndNext(sb, page);
	}

	@Override
	public void onResume() {
		super.onResume();

		Annotation a = wrapped.getTopItem();
		if (a != null && a.getEnd().before(DateTimeFactory.getNow().toDate())) {
			this.refresh();
		}
	}
}

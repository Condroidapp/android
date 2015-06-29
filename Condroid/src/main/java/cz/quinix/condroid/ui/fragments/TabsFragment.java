package cz.quinix.condroid.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.astuetz.PagerSlidingTabStrip;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import cz.quinix.condroid.R;

public class TabsFragment extends RoboSherlockFragment {

	public static final String TAG = TabsFragment.class
			.getSimpleName();

	private MyPagerAdapter adapter;

	private ViewPager pager;

	public static TabsFragment newInstance() {
		return new TabsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_parent_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view
				.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		pager.getCurrentItem();

		//TODO on tab click
	}

	public void refreshFragments() {
		for (int i = 0; i < 2; i++) {
			NewCondroidFragment fragment = (NewCondroidFragment) this.getChildFragmentManager().findFragmentByTag(this.getFragmentTag(i));
			if (fragment != null) {
				fragment.refresh();
			}

		}
	}

	public void handleSearch(String query) {
		NewCondroidFragment activeFragment = this.getActiveFragment();

		activeFragment.handleSearch(query);
	}

	public NewCondroidFragment getActiveFragment() {
		return (NewCondroidFragment) this.getChildFragmentManager().findFragmentByTag(getFragmentTag(pager.getCurrentItem()));
	}

	private String getFragmentTag(int item) {
		return "android:switcher:" + R.id.pager + ":" + item;
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		// private SparseArray<NewCondroidFragment> pageReferenceMap;

		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
			// pageReferenceMap = new SparseArray<NewCondroidFragment>();
		}

		private final int[] TITLES = {R.string.tRunning, R.string.tAll};

		@Override
		public CharSequence getPageTitle(int position) {
			return TabsFragment.this.getString(TITLES[position]);
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public SherlockFragment getItem(int position) {
			NewCondroidFragment fragment = null;
			switch (position) {
				case 0:
					fragment = TimetableFragment.newInstance();
					break;
				case 1:
					fragment = FullListFragment.newInstance();
					break;
			}
			/*if(fragment != null) {
				this.pageReferenceMap.put(position, fragment);
            }*/
			return fragment;
		}

        /* public NewCondroidFragment getFragment(int position) {
            return this.pageReferenceMap.get(position);
        }*/

		//  public SparseArray<NewCondroidFragment> getFragments() {
		// return pageReferenceMap;
		//  }
	}

}

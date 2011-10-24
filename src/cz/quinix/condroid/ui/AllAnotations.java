package cz.quinix.condroid.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.SearchQueryBuilder;
import cz.quinix.condroid.abstracts.CondroidListActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;

public class AllAnotations extends CondroidListActivity {

	private static SearchQueryBuilder searchQuery = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(searchQuery == null)
			searchQuery = new SearchQueryBuilder();
		this.provider = DataProvider.getInstance(getApplicationContext());

		annotations = this.provider.getAnnotations(
				searchQuery.buildCondition(), 0);
		this.adapter = new EndlessAdapter(annotations);
		this.setListAdapter(this.adapter);
		this.handleIntent(this.getIntent());
		registerForContextMenu(this.getListView());
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		this.setIntent(intent);
		this.handleIntent(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = this.getMenuInflater();
		
		i.inflate(R.menu.annotations, menu);
		
		super.onCreateOptionsMenu(menu);

		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
			menu.findItem(R.id.annotations_refresh).setVisible(!searchQuery.isEmpty());
		return super.onPrepareOptionsMenu(menu);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.annotations_refresh:
			this.searchQuery.clear();
			// refresh deletes search term and page
			this.annotations.clear();
			this.annotations.addAll(this.provider.getAnnotations(
					searchQuery.buildCondition(), 0));
			((EndlessAdapter) this.adapter).refreshDataset();

			return true;
		case R.id.search:
			onSearchRequested();
			return true;
		case R.id.filter:
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.chooseFilter);
			ab.setItems(R.array.filterBy,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							if (which == 1) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										AllAnotations.this);
								builder.setTitle(R.string.dPickLine);

								HashMap<Integer, String> pl = provider
										.getProgramLines();
								int i = 0;
								final String[] pls;
								if (searchQuery.hasParam(new ProgramLine())) {
									pls = new String[pl.size() + 1];
									pls[0] = "- Zrušit filtr";
									i++;
								} else {
									pls = new String[pl.size()];
								}

								for (String p : pl.values()) {
									pls[i++] = p;
								}
								Arrays.sort(pls);
								builder.setItems(pls,
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												int lid = 0;
												String value = pls[which];
												if (value
														.equals("- Zrušit filtr")) {
													searchQuery
															.removeParam(new ProgramLine());
												} else {
													for (Entry<Integer, String> entry : provider
															.getProgramLines()
															.entrySet()) {
														if (entry.getValue()
																.equals(value)) {
															lid = entry
																	.getKey();
															break;
														}
													}

													ProgramLine pl = new ProgramLine();
													pl.setLid(lid);
													pl.setName(value);
													searchQuery.addParam(pl);
												}
												search();
											}

										});
								builder.create().show();
							}
							if (which == 0) {
								AlertDialog.Builder build = new AlertDialog.Builder(
										AllAnotations.this);
								build.setTitle(R.string.dPickDate);
								List<Date> dates = provider.getDates();
								final String[] ds;
								int j = 0;
								if (searchQuery.hasParam(new Date())) {
									ds = new String[dates.size() + 1];
									ds[0] = "- Zrušit filtr";
									j++;
								} else {
									ds = new String[dates.size()];
								}

								DateFormat df = new SimpleDateFormat(
										"EEEE d. M. yyyy", new Locale("cs",
												"CZ"));

								for (Date date : dates) {
									char[] c = df.format(date).toCharArray();
									c[0] = Character.toUpperCase(c[0]);
									ds[j++] = new String(c);
								}
								build.setItems(ds,
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												DateFormat df = new SimpleDateFormat(
														"EEEE d. M. yyyy",
														new Locale("cs", "CZ"));
												if (ds[which]
														.equals("- Zrušit filtr")) {
													searchQuery
															.removeParam(new Date());
												} else {
													try {
														Date d = df
																.parse(ds[which]);
														searchQuery.addParam(d);
													} catch (ParseException e) {
														// TODO Auto-generated
														// catch
														// block
														e.printStackTrace();
													}
												}

												search();

											}
										});
								build.create().show();
							}
						}
						
					});
			ab.create().show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Annotation selected = (Annotation) l.getItemAtPosition(position);
		Intent intent = new Intent(this, ShowAnnotation.class);
		intent.putExtra("annotation", selected);
		this.startActivity(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			searchQuery.addParam(intent.getStringExtra(SearchManager.QUERY));
			search();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		    menu.setHeaderTitle(this.annotations.get(info.position).getTitle());
		    String[] menuItems = getResources().getStringArray(R.array.annotationContext);
		    for (int i = 0; i<menuItems.length; i++) {
		      menu.add(Menu.NONE, i, i, menuItems[i]);
		    }
		  
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		Annotation an = this.annotations.get(info.position);
		switch (menuItemIndex) {
		case 0:
			new ShareProgramListener(this).invoke(an);
			break;
		case 1:
			new MakeFavoritedListener(this).invoke(an, null);
			this.adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		  
		  return true;
	}

	private void search() {

		List<Annotation> foo = new ArrayList<Annotation>();
		// foo.addAll(this.annotations);
		foo = this.provider.getAnnotations(searchQuery.buildCondition(), 0);
		if (foo.size() > 0) {
			this.annotations.clear();
			this.annotations.addAll(foo);
		} else {
			Toast.makeText(this, R.string.noAnnotationsFound, Toast.LENGTH_LONG)
					.show();
		}

		((EndlessAdapter) this.adapter).refreshDataset();
	}

	class EndlessAdapter extends com.commonsware.cwac.endless.EndlessAdapter {
		private RotateAnimation rotate;
		private List<Annotation> itemsToAdd;
		private int itemsPerPage = 0;

		public EndlessAdapter(List<Annotation> items) {
			super(new ArrayAdapter<Annotation>(AllAnotations.this,
					R.layout.annotation_list_item, android.R.id.text1, items));
			rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(1000);
			rotate.setRepeatMode(Animation.RESTART);
			rotate.setRepeatCount(Animation.INFINITE);
			this.itemsPerPage = items.size();
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if (this.itemsPerPage == 0
					|| ((this.getCount() - 1) % this.itemsPerPage != 0)) {
				return false;
			}
			this.itemsToAdd = provider.getAnnotations(
					searchQuery.buildCondition(),
					(this.getCount() / this.itemsPerPage));

			return (this.itemsToAdd.size() == this.itemsPerPage);
		}

		@Override
		protected void appendCachedData() {
			if (this.itemsToAdd != null && this.itemsToAdd.size() > 0) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Annotation> a = (ArrayAdapter<Annotation>) this
						.getWrappedAdapter();
				for (int i = 0; i < this.itemsToAdd.size(); i++) {
					a.add(this.itemsToAdd.get(i));
				}
				this.itemsToAdd = null;
			}

		}

		@Override
		protected View getPendingView(ViewGroup parent) {
			View row = getLayoutInflater().inflate(R.layout.row, null);

			View child = row.findViewById(R.id.throbber);
			child.startAnimation(rotate);

			return (row);
		}

		@Override
		public void refreshDataset() {
			super.refreshDataset();
			AllAnotations.this.getListView().setSelection(0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			//if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.annotation_list_item, null);
			//}
			Annotation it = null;
			try {
				it = (Annotation) this.getItem(position);
			} catch (IndexOutOfBoundsException e) {

			}
			if (it != null) {
				return inflanteAnnotation(v, it);
			}
			return super.getView(position, convertView, parent);
		}

	}
}

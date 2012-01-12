package cz.quinix.condroid.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.DatabaseLoader;
import cz.quinix.condroid.loader.ConventionLoader;
import cz.quinix.condroid.loader.DataLoader;
import cz.quinix.condroid.model.Convention;

public class WelcomeActivity extends CondroidActivity implements
		AsyncTaskListener {

	private DataProvider dataProvider = null;
	public static final String TAG = "Condroid";
	private ProgressDialog pd;
	private List<Convention> conventionList;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = this.getMenuInflater();
		i.inflate(R.menu.welcome, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mAbout:
			new AboutDialog(this).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		this.setContentView(R.layout.welcome);
		dataProvider = DataProvider.getInstance(getApplicationContext());
		
		SharedPreferences pref = getSharedPreferences(TAG, 0);
		boolean shown = pref.getBoolean("aboutShown", false);
		if(!shown) {
			AlertDialog dialog = new AboutDialog(this);
			dialog.show();
		}
		
		
		if (!dataProvider.hasData()) {
			this.noDataDialog(getString(R.string.noData) + " "
					+ getString(R.string.downloadDialog));
		} else {
			initView();
		}

		Button refresh = (Button) this.findViewById(R.id.bReload);
		refresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				noDataDialog(getString(R.string.downloadDialog));

			}
		});

		Button all = (Button) findViewById(R.id.bShowAll);
		all.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						AllAnotations.class);
				startActivity(intent);

			}
		});

		Button now = (Button) findViewById(R.id.bShowActual);
		now.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final SharedPreferences pref = getSharedPreferences(TAG, 0);
				boolean messageShown = pref.getBoolean("messageShown", false);
				Convention con = dataProvider.getCon();
				if (!messageShown && con != null && con.getMessage() != "") {
						AlertDialog.Builder m = new AlertDialog.Builder(
								WelcomeActivity.this);
						m.setTitle(con.getName());
						m.setMessage(con.getMessage());
						m.setPositiveButton("OK, již nezobrazovat",
								new Dialog.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										SharedPreferences.Editor editor = pref.edit();
										editor.putBoolean("messageShown", true);
										editor.commit();
										launchIntent();
									}
								});
						m.create().show();
					
				} else {
					launchIntent();
				}

			}

			private void launchIntent() {
				Intent intent = new Intent(WelcomeActivity.this,
						RunningActivity.class);
				startActivity(intent);
			}
		});

		Button locations = (Button) findViewById(R.id.bShowLocations);
		locations.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(dataProvider.getCon()
						.getLocationsFile()));
				startActivity(intent);
			}
		});
	}

	private void initView() {
		
		Convention con = dataProvider.getCon();
		if (con != null) {
			TextView tw2 = (TextView) findViewById(R.id.tLoadedInfoCon);
			findViewById(R.id.lLoadedInfo).setVisibility(View.VISIBLE);
			tw2.setText(con.getName());
		}
		LinearLayout l = (LinearLayout) findViewById(R.id.lbShowLocations);
		if (con.getLocationsFile()!= null && !con.getLocationsFile().trim().equals("")) {
			l.setVisibility(View.VISIBLE);
		} else {
			l.setVisibility(View.GONE);
		}
	}

	private void noDataDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setPositiveButton(R.string.yes, new DialogOnClick())
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								if (!dataProvider.hasData()) {
									Toast.makeText(
											WelcomeActivity.this,
											"Condroid nemá data se kterými by mohl pracovat, proto se nyní ukončí.",
											Toast.LENGTH_LONG).show();
									WelcomeActivity.this.finish();
								}
							}
						});
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@SuppressWarnings("unchecked")
	public void onAsyncTaskCompleted(ListenedAsyncTask<?,?> task) {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		
		List<?> list = null;
		
		if(task.hasResult()) {
			list = task.getResult();
		}
		
		if (task instanceof ConventionLoader && list != null) {
			// conventions downloaded
			if (list.size() == 0) {
				Toast.makeText(this,
						"Chyba při stahování, zkuste to prosím později.",
						Toast.LENGTH_LONG).show();
				return;
			}
			conventionList = (List<Convention>) list;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					WelcomeActivity.this);
			builder.setTitle(R.string.dPickConvention);
			String[] items = new String[list.size()];
			int i = 0;
			for (Object con : list) {
				items[i++] = ((Convention) con).getName();
			}

			builder.setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {

					dataProvider.setConvention(conventionList.get(which));
					pd = ProgressDialog.show(WelcomeActivity.this, "",
							getString(R.string.preparing), true);
					new DataLoader(WelcomeActivity.this, pd)
							.execute(conventionList.get(which).getDataUrl());

				}
			});
			AlertDialog d = builder.create();
			d.show();
		}
		if(task instanceof DataLoader) {
			if(list != null) {
				pd = new ProgressDialog(WelcomeActivity.this);
				pd.setMessage(getString(R.string.processing));
				pd.setCancelable(true);
				//new DatabaseLoader(WelcomeActivity.this, dataProvider).execute(list);
				
				this.dataProvider.prepareInsert().setListener(this, pd).execute(list);
			}
		}
		if(task instanceof DatabaseLoader) {
		
			if (!dataProvider.hasData()) {
				Toast.makeText(this,
						"Chyba při stahování, zkuste to prosím později.",
						Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			
			SharedPreferences.Editor editor = getSharedPreferences(TAG, 0).edit();
			editor.remove("messageShown");
			editor.commit();
			initView();
		}
	}

	class DialogOnClick implements DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialog, int which) {

			dialog.cancel();
			pd = ProgressDialog.show(WelcomeActivity.this, "",
					getString(R.string.loading), true);

			try {
				new ConventionLoader(WelcomeActivity.this).execute();
			} catch (Exception e) {
				Log.e(WelcomeActivity.TAG, "", e);
				return;
			}

		}

	}
}

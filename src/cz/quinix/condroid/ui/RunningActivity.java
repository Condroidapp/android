package cz.quinix.condroid.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidListActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RunningActivity extends CondroidListActivity {

    private boolean favoritedOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = DataProvider.getInstance(getApplicationContext());
        annotations = provider.getRunningAndNext(0);
        if (annotations.size() == 0) {
            Toast.makeText(this, "Neexistuje žádný běžící program.", Toast.LENGTH_LONG).show();
            this.finish();
        }
        adapter = new CategoryAdapter(annotations, this);
        this.setListAdapter(adapter);
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = this.getMenuInflater();
        i.inflate(R.menu.running, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_favorite:
                this.favoritedOnly = !this.favoritedOnly;
            case R.id.refresh:
                try {
                    List<Annotation> a = provider.getRunningAndNext(0, this.favoritedOnly);
                    this.annotations.clear();
                    this.annotations.addAll(a);
                    ((CategoryAdapter) this.getListAdapter()).notifyDataSetChanged();
                } catch (IllegalStateException e) {
                    this.favoritedOnly = false;
                    Toast.makeText(this, "Žádné pořady nejsou označené jako oblíbené.", Toast.LENGTH_LONG).show();
                }

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.favoritedOnly) {
            menu.findItem(R.id.run_favorite).setTitle(R.string.mShowAll);
        } else {
            menu.findItem(R.id.run_favorite).setTitle(R.string.mFavoriteOnly);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Annotation selected = (Annotation) l.getItemAtPosition(position);
        if (selected.getTitle() != "break") {

            Intent intent = new Intent(this, ShowAnnotation.class);
            intent.putExtra("annotation", selected);
            this.startActivity(intent);
        }
    }

}

class CategoryAdapter extends ArrayAdapter<Annotation> {

    private DateFormat read = new SimpleDateFormat("EEEE dd.MM. HH:mm",
            new Locale("cs", "CZ"));
    private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
    private RunningActivity caller;

    public CategoryAdapter(List<Annotation> map, RunningActivity caller) {
        super(caller, android.R.layout.simple_list_item_1, map);
        this.caller = caller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Annotation it = this.getItem(position);

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) caller.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.annotation_list_item, null);
        }
        setLayout(v, it);
        if (it != null) {
            if (it.getTitle() == "break") {
                TextView tw = (TextView) v.findViewById(R.id.tRunningTitle);
                if (it.getAnnotation().equals("now")) {
                    tw.setText("Právě běží");
                } else {
                    if (caller.isDateToday(it.getStartTime())) {
                        tw.setText("dnes, " + todayFormat.format(it.getStartTime()));
                    } else {
                        tw.setText(read.format(it.getStartTime()));
                    }
                }
                v.setFocusable(false);
                return v;
            }

            return caller.inflanteAnnotation(v, it);

        }

        return super.getView(position, convertView, parent);
    }

    private void setLayout(View v, Annotation item) {
        LinearLayout title = (LinearLayout) v.findViewById(R.id.ldateLayout);
        FrameLayout itemL = (FrameLayout) v.findViewById(R.id.lItemLayout);
        if (item.getTitle() == "break") {
            title.setVisibility(View.VISIBLE);
            itemL.setVisibility(View.GONE);
        } else {
            itemL.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
        }
    }

}

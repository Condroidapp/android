package cz.quinix.condroid.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockListActivity;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Place;

/**
 * Created by Jan on 22. 6. 2014.
 */
public class NeighbourhoodListActivity extends RoboSherlockListActivity {

    @Inject
    private DataProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setListAdapter(new NeighbourhoodOuterAdapter(this, this.prepareGroups(provider.getPlaces())));
        ListView listView = getListView();
        listView.setBackgroundColor(getResources().getColor(R.color.lightgray));


    }

    private List<PlaceGroup> prepareGroups(List<Place> places) {
        List<PlaceGroup> list = new ArrayList<PlaceGroup>();
        PlaceGroup actual = null;

        for (Place place : places) {

            if (actual == null || !actual.category.equals(place.getCategory())) {
                actual = new PlaceGroup();
                actual.category = place.getCategory();
                list.add(actual);
            }
            actual.places.add(place);
        }
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PlaceGroup {
        public String category;
        public List<Place> places = new ArrayList<Place>();
    }

    private class NeighbourhoodOuterAdapter extends ArrayAdapter<PlaceGroup> {

        public NeighbourhoodOuterAdapter(Context context, List<PlaceGroup> objects) {
            super(context, R.layout.neighbourhood_list_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PlaceGroup r = this.getItem(position);

            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.neighbourhood_list_item, parent, false);
            }
            TextView title = (TextView) v.findViewById(R.id.tTitle);
            LinearLayout listView = (LinearLayout) v.findViewById(R.id.item_holder);

            if (r != null) {
                title.setText(r.category);

                this.createInnerContent(listView, r.places);
                return v;
            }

            return super.getView(position, convertView, parent);
        }

        private void createInnerContent(LinearLayout listView, List<Place> places) {
            for (final Place place : places) {
                View child = getLayoutInflater().inflate(R.layout.neighbourhood_list_item_item, null);

                TextView item = (TextView) child.findViewById(R.id.tTitle);
                item.setText(place.getName());
                item.setClickable(true);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(NeighbourhoodListActivity.this, NeighbourhoodActivity.class);
                        i.putExtra("place", place);
                        startActivity(i);
                    }
                });

                TextView open = (TextView) child.findViewById(R.id.tOpenIcon);
                this.setIcon(open, place.isOpen());


                listView.addView(child);
            }
        }

        private void setIcon(TextView open, int state) {
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
            open.setTypeface(type);

            open.setText(R.string.fa_circle);

            switch (state) {
                case Place.STATE_OPEN:
                    open.setTextColor(getResources().getColor(R.color.condroidGreen));
                    break;
                case Place.STATE_CLOSED:
                    open.setTextColor(getResources().getColor(R.color.red));
                    break;
                default:
                    open.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

}

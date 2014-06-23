package cz.quinix.condroid.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

import cz.quinix.condroid.R;
import cz.quinix.condroid.model.Place;
import cz.quinix.condroid.model.PlaceHours;

/**
 * Created by Jan on 22. 6. 2014.
 */
public class NeighbourhoodActivity extends RoboSherlockActivity {

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.neighbourhood_activity);

        this.place = (Place) this.getIntent().getSerializableExtra(
                "place");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.annot_title);
        title.setText(place.getName());

        TextView tAddress = (TextView) findViewById(R.id.tAddress);

        if (place.getAddress() != null) {
            tAddress.setText(StringUtils.join(place.getAddress(), "\n"));
        } else {
            tAddress.setVisibility(View.GONE);
        }

        TextView tDescription = (TextView) findViewById(R.id.tDescription);

        if (place.getDescription() != null) {
            tDescription.setText(place.getDescription());
        } else {
            tDescription.setVisibility(View.GONE);
        }

        TextView tUrl = (TextView) findViewById(R.id.tUrl);
        if (place.getUrl() != null) {
            tUrl.setText(place.getUrl());
        } else {
            tUrl.setVisibility(View.GONE);
        }

        if (place.getGps() != null) {
            LinearLayout header = (LinearLayout) findViewById(R.id.lPlaceHeader);
            header.setClickable(true);

            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (place.getGps() != null) {
                        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f(%s)&z=19", place.getGps().lat, place.getGps().lon, place.getName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                }
            });
        }

        this.setupOpening();

    }

    private void setupOpening() {
        if (place.getHours() != null) {
            findViewById(R.id.lMissing).setVisibility(View.GONE);
            LinearLayout parent = (LinearLayout) findViewById(R.id.lOpeningHours);
            PlaceHours hours = place.getHours();
            for (int key : hours.getKeys()) {


                View child = getLayoutInflater().inflate(R.layout.neighbourhood_activity_item, null);

                TextView item = (TextView) child.findViewById(R.id.tDay);
                item.setText(hours.getReadableTitleFor(key));

                TextView item2 = (TextView) child.findViewById(R.id.tTitle);
                String[] items = hours.getHoursFor(key);
                if (items == null) {
                    item2.setText(getString(R.string.lClosed));
                } else {
                    item2.setText(items[0] + " - " + items[1]);
                }
                if (hours.isToday(key)) {
                    child.setBackgroundResource(R.color.mediumgray);
                }

                parent.addView(child);
            }
        } else {
            findViewById(R.id.lMissing).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, NeighbourhoodListActivity.class);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

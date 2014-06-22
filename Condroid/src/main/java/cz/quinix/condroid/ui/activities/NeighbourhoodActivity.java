package cz.quinix.condroid.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import cz.quinix.condroid.R;
import cz.quinix.condroid.model.Place;

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

        Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
    }
}

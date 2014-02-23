package cz.quinix.condroid.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;

import cz.quinix.condroid.R;

/**
 * Created by Jan on 23.2.14.
 */
public class WelcomeActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.welcome);

        this.findViewById(R.id.lEventSelector).setVisibility(View.GONE);
        this.findViewById(R.id.tCopyright).setVisibility(View.GONE);

        this.load();
    }

    private void load() {
        ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.pbEvents);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
    }

}

package cz.quinix.condroid.ui.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import cz.quinix.condroid.ui.AboutDialog;
import cz.quinix.condroid.ui.Preferences;
import cz.quinix.condroid.ui.ReminderList;
import cz.quinix.condroid.ui.activities.MainActivity;
import cz.quinix.condroid.ui.activities.WelcomeActivity;

/**
 * Created by Jan on 1. 6. 2014.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private MainActivity parentActivity;

    public DrawerItemClickListener(MainActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //load event
                Intent intent = new Intent(parentActivity, WelcomeActivity.class);
                intent.putExtra("force", true);
                parentActivity.startActivity(intent);

                break;
            case 1:
                Intent in = new Intent(parentActivity, ReminderList.class);
                parentActivity.startActivityForResult(in, 0);
                break;
            case 2:
                Intent i = new Intent(parentActivity, Preferences.class);
                parentActivity.startActivity(i);
                break;
            case 3:
                parentActivity.setActivityResultListener(new AboutDialog(parentActivity));

                break;
        }
        this.parentActivity.closeDrawer();


    }
}

package cz.quinix.condroid.ui;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Reminder;
import cz.quinix.condroid.service.ReminderManager;
import cz.quinix.condroid.ui.adapters.EndlessAdapter;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 21.5.12
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public class ReminderList extends ListActivity {
    List<Reminder> data;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lw = this.getListView();
        data = DataProvider.getInstance(this).getReminderList();
        this.setListAdapter(new CustomAdapter(this, data));
        this.registerForContextMenu(this.getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v instanceof ListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Reminder an = (Reminder) ((ListView) v).getItemAtPosition(info.position);
            menu.setHeaderTitle(an.annotation.getTitle());
            String[] menuItems = getResources().getStringArray(R.array.reminderContext);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Reminder an = (Reminder) ((ListView) this.getListView()).getItemAtPosition(info.position);
            switch (menuItemIndex) {
                case 0:
                    DataProvider.getInstance(this).removeReminder(an.annotation.getPid());
                    ReminderManager.updateAlarmManager(this);
                    this.data.clear();
                    this.data.addAll(DataProvider.getInstance(this).getReminderList());
                    ((CustomAdapter)this.getListView().getAdapter()).notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        return true;
    }

    private class CustomAdapter extends ArrayAdapter<Reminder> {

        public CustomAdapter(Context context, List<Reminder> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Reminder r = this.getItem(position);

            View v = convertView;
            if(v == null) {
                v= ((LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminders_list, null);
            }
            TextView title = (TextView) v.findViewById(R.id.tReminderListTitle);
            TextView date = (TextView) v.findViewById(R.id.tReminderListDate);
            TextView remind = (TextView) v.findViewById(R.id.tReminderListRemind);

            if(r != null) {
                DateFormat todayFormat = new SimpleDateFormat("HH:mm");
                DateFormat dayFormat = new SimpleDateFormat(
                        "EE dd.MM. HH:mm", new Locale("cs", "CZ"));
                title.setText(r.annotation.getTitle());
                Date today = new Date();
                Date st = r.annotation.getStartTime();
                if((st.getYear() == today.getYear() && st.getMonth() == today.getMonth() && st.getDate() == today.getDate())) {
                    date.setText("dnes, "+todayFormat.format(r.annotation.getStartTime()));
                } else {
                    date.setText(dayFormat.format(r.annotation.getStartTime()));
                }

                remind.setText(r.reminder + " min před");
                return v;
            }

            return super.getView(position, convertView, parent);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
package cz.quinix.condroid.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.quinix.condroid.R;
import cz.quinix.condroid.model.Convention;

/**
 * Created by Jan on 27. 4. 2014.
 */
public class EventAdapter extends ArrayAdapter<Convention> {

    private int layout;
    private List<Convention> data;

    private DateFormat generalFormat = new SimpleDateFormat("EE dd.MM.",
            new Locale("cs", "CZ"));

    public EventAdapter(Context context, List<Convention> data) {
        super(context, R.layout.event_item_layout, data);
        this.layout = R.layout.event_item_layout;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EventViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) this.getContext()).getLayoutInflater();
            row = inflater.inflate(layout, parent, false);

            holder = new EventViewHolder();
            holder.title = (TextView) row.findViewById(R.id.tEventTitle);
            holder.subtitle = (TextView) row.findViewById(R.id.tEventSubtitle);

            row.setTag(holder);
        } else {
            holder = (EventViewHolder) row.getTag();
        }

        Convention event = data.get(position);
        holder.title.setText(event.getName());

        String date = "";
        if (event.getDate() != null) {
            date = event.getDate();
        }
        holder.subtitle.setText(date);

        return row;
    }

    static class EventViewHolder {
        TextView title;
        TextView subtitle;
    }
}

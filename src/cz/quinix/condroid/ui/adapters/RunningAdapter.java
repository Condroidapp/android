package cz.quinix.condroid.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.model.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 15.4.12
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class RunningAdapter extends EndlessAdapter {

    private DateFormat read = new SimpleDateFormat("EEEE dd.MM. HH:mm",
            new Locale("cs", "CZ"));
    private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");

    public RunningAdapter(List<Annotation> map, CondroidActivity caller) {
        super(caller, map);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Annotation it = null;
        try {
            it = (Annotation) this.getItem(position);
        } catch (IndexOutOfBoundsException e) {
        }

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.annotation_list_item, null);
        }

        if (it != null) {
            setLayout(v, it);
            if (it.getTitle() == "break") {
                TextView tw = (TextView) v.findViewById(R.id.tRunningTitle);
                if (it.getAnnotation().equals("now")) {
                    tw.setText("Právě běží");
                } else {
                    if(false /*caller.isDateToday(it.getStartTime())*/) {
                        tw.setText("dnes, "+todayFormat.format(it.getStartTime()));
                    }
                    else {
                        tw.setText(read.format(it.getStartTime()));
                    }
                }
                v.setFocusable(false);
                return v;
            }

            return this.inflanteAnnotation(v, it);

        }

        return super.getView(position, convertView, parent);
    }

    private void setLayout(View v, Annotation item) {
        LinearLayout title = (LinearLayout) v.findViewById(R.id.ldateLayout);
        FrameLayout itemL = (FrameLayout) v.findViewById(R.id.lItemLayout);
        if(item.getTitle() == "break") {
            title.setVisibility(View.VISIBLE);
            itemL.setVisibility(View.GONE);
        } else {
            itemL.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
        }
    }

}

package cz.quinix.condroid.ui.adapters;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
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

    private DateFormat generalFormat = new SimpleDateFormat("EEEE dd.MM. HH:mm",
            new Locale("cs", "CZ"));
    private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");

    public RunningAdapter(List<Annotation> map, CondroidActivity caller) {
        super(caller, map);
    }

    @Override
    protected List<Annotation> getPrecachedData(int page) {
        return DataProvider.getInstance(getActivity()).getRunningAndNext(page);
    }

    @Override
    public View inflateAnnotation(View v, Annotation annotation) {
        this.setLayout(v, annotation);
        if (annotation.getTitle() == "break") {
            ViewHolder vh = (ViewHolder) v.getTag(R.id.listItem);
            if (annotation.getAnnotation().equals("now")) {
                vh.runningTitle.setText(R.string.runningNow);
            } else {
                if (isDateToday(annotation.getStartTime())) {
                    vh.runningTitle.setText(getActivity().getString(R.string.today) + ", " + todayFormat.format(annotation.getStartTime()));
                } else {
                    vh.runningTitle.setText(generalFormat.format(annotation.getStartTime()));
                }
            }
            v.setFocusable(false);
            return v;
        }
        return super.inflateAnnotation(v, annotation);
    }

    @Override
    protected EndlessAdapter.ViewHolder initializeItemLayout(View convertView) {
        EndlessAdapter.ViewHolder evh = super.initializeItemLayout(convertView);
        ViewHolder vh = new ViewHolder();
        vh.author = evh.author;
        vh.favorited = evh.favorited;
        vh.line = evh.line;
        vh.place = evh.place;
        vh.programType = evh.programType;
        vh.time = evh.time;
        vh.title = evh.title;
        vh.runningTitle = (TextView) convertView.findViewById(R.id.tRunningTitle);

        vh.time.setVisibility(View.GONE);
        vh.place.setVisibility(View.VISIBLE);
        return vh;
    }

    private void setLayout(View v, Annotation item) {
        LayoutHolder lh = (LayoutHolder) v.getTag(R.id.layoutList);
        if (lh == null) {
            lh = new LayoutHolder();
            lh.titleLayout = (LinearLayout) v.findViewById(R.id.ldateLayout);
            lh.itemLayout = (FrameLayout) v.findViewById(R.id.lItemLayout);
            v.setTag(R.id.layoutList, lh);
        }
        if (item.getTitle() == "break") {
            lh.titleLayout.setVisibility(View.VISIBLE);
            lh.itemLayout.setVisibility(View.GONE);
        } else {
            lh.itemLayout.setVisibility(View.VISIBLE);
            lh.titleLayout.setVisibility(View.GONE);
        }
    }

    class LayoutHolder {
        LinearLayout titleLayout;
        FrameLayout itemLayout;
    }

    class ViewHolder extends EndlessAdapter.ViewHolder {
        TextView runningTitle;
    }

}

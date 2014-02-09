package cz.quinix.condroid.ui.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.Running;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public RunningAdapter(List<Annotation> map, Activity caller) {
        super(caller, AddBreaks.addBreaks(map, null));
        this.totalItems = map.size();
        this.keepOnAppending.set(!(map.size() < DataProvider.ITEMS_PER_PAGE));
    }

    @Override
    protected void insertSettedList(List<Annotation> items, ArrayAdapter<Annotation> a) {
        super.insertSettedList(AddBreaks.addBreaks(items, null), a);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        boolean x = super.cacheInBackground();
        if (this.itemsToAdd.size() > 0) {
            this.itemsToAdd = AddBreaks.addBreaks(this.itemsToAdd, (Annotation) this.getItem(this.getWrappedAdapter().getCount() - 1));
        }
        return x;
    }

    @Override
    protected List<Annotation> getPrecachedData(int page) {
        return DataProvider.getInstance(getActivity()).getRunningAndNext(SearchProvider.getSearchQueryBuilder(Running.class.getName()), page);

    }


    @Override
    public View inflateAnnotation(View v, Annotation annotation) {
        this.setLayout(v, annotation);
        if (annotation.getTitle() == "break" || annotation.getTitle().equals("break-now")) {
            ViewHolder vh = (ViewHolder) v.getTag(R.id.listItem);
            if (annotation.getStartTime().before(new Date())) {
                vh.runningTitle.setText(R.string.runningNow);
            } else {
                if (isDateToday(annotation.getStartTime())) {
                    vh.runningTitle.setText(getActivity().getString(R.string.today) + ", " + todayFormat.format(annotation.getStartTime()));
                } else {
                    vh.runningTitle.setText(generalFormat.format(annotation.getStartTime()));
                }
            }
            v.setFocusable(false);
            v.setClickable(false);
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

    private static class AddBreaks {
        static List<Annotation> addBreaks(List<Annotation> annotations, Annotation lastItem) {
            Date previous = new Date();
            List<Annotation> formatted = new ArrayList<Annotation>();
            for (int i = 0; annotations.size() > i; i++) {
                Annotation a = annotations.get(i);
                if (i == 0) {
                    if (lastItem == null) {
                        formatted.add(getTimeHeader(a));
                        previous = a.getStartTime();
                    } else {
                        if (!lastItem.getStartTime().equals(a.getStartTime())) {
                            formatted.add(getTimeHeader(a));
                        }
                        previous = a.getStartTime();
                    }
                } else if (a.getStartTime().after(previous)) {
                    formatted.add(getTimeHeader(a));
                    previous = a.getStartTime();
                }
                formatted.add(a);
            }
            return formatted;
        }

        private static Annotation getTimeHeader(Annotation x) {
            Annotation a = new Annotation();
            a.setStartTime(x.getStartTime());
            a.setTitle("break");
            return a;
        }
    }

}

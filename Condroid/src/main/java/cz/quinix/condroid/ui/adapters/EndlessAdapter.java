package cz.quinix.condroid.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.fragments.AllAnnotations;
import roboguice.RoboGuice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 15.4.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class EndlessAdapter<Type> extends com.commonsware.cwac.endless.EndlessAdapter {
    private RotateAnimation rotate;
    protected List<Type> itemsToAdd;
    protected int totalItems = 0;
    private Activity activity;
    @Inject protected DataProvider provider;

    private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
    private static DateFormat dayFormat = new SimpleDateFormat(
            "EE dd.MM. HH:mm", new Locale("cs", "CZ"));


    public EndlessAdapter(Activity activity, List<Type> items) {
        this(activity, new ArrayAdapter<Type>(activity,
                R.layout.annotation_list_item, android.R.id.text1, items));
    }

    public EndlessAdapter(Activity activity, ListAdapter wrapped) {
        super(wrapped);

        RoboGuice.getInjector(activity).injectMembers(this);
        this.activity = activity;
        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        totalItems = wrapped.getCount();
        this.keepOnAppending.set(!(totalItems < DataProvider.ITEMS_PER_PAGE));
    }


    /*  @Override
    public Object getItem(int position) {
        return items.get(position);
    }*/

    public void setItems(List<Type> items, boolean refresh) {
        ArrayAdapter<Type> a = (ArrayAdapter<Type>) this.getWrappedAdapter();
        a.clear();
        this.insertSettedList(items, a);
        this.totalItems = items.size();
        this.keepOnAppending.set(!(items.size() < DataProvider.ITEMS_PER_PAGE));
        if (refresh) {
            this.notifyDataSetChanged();
        }
    }

    protected void insertSettedList(List<Type> items, ArrayAdapter<Type> a) {
        for (int i = 0; i < items.size(); i++) {
            a.add(items.get(i));
        }
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        if (this.getCount() == 0) {
            return false;
        }
        this.itemsToAdd = this.getPrecachedData(totalItems);
        totalItems += itemsToAdd.size();
        return !(itemsToAdd.size() < DataProvider.ITEMS_PER_PAGE);
    }

    protected List<Annotation> getPrecachedData(int skip) {
        return this.provider.getAnnotations(SearchProvider.getSearchQueryBuilder(AllAnnotations.class.getName()), skip);
    }

    @Override
    protected void appendCachedData() {
        if (this.itemsToAdd != null && this.itemsToAdd.size() > 0) {
            @SuppressWarnings("unchecked")
            ArrayAdapter<Annotation> a = (ArrayAdapter<Annotation>) this
                    .getWrappedAdapter();
            for (int i = 0; i < this.itemsToAdd.size(); i++) {
                a.add(this.itemsToAdd.get(i));
            }
            this.itemsToAdd = null;
        }

    }

    @Override
    protected View getPendingView(ViewGroup parent) {
        View row = activity.getLayoutInflater().inflate(R.layout.row, null);

        View child = row.findViewById(R.id.throbber);
        row.setClickable(false);
        row.setFocusable(false);
        child.startAnimation(rotate);

        return (row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.annotation_list_item, null);
        }
        if (convertView.getTag(R.id.listItem) == null) {
            convertView.setTag(R.id.listItem, this.initializeItemLayout(convertView));
        }

        Annotation it = null;
        try {
            it = (Annotation) this.getItem(position);
        } catch (IndexOutOfBoundsException e) {

        }
        if (it != null && convertView.getTag(R.id.listItem) != null) {
            return this.inflateAnnotation(convertView, it);
        }
        return super.getView(position, convertView, parent);
    }

    protected ViewHolder initializeItemLayout(View convertView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.firstRow = (RelativeLayout) convertView.findViewById(R.id.lFirstRow);
        viewHolder.author = (TextView) convertView.findViewById(R.id.alAuthor);
        viewHolder.title = (TextView) convertView.findViewById(R.id.alTitle);
        viewHolder.line = (TextView) convertView.findViewById(R.id.alLine);
        viewHolder.place = (TextView) convertView.findViewById(R.id.alPlace);
        viewHolder.time = (TextView) convertView.findViewById(R.id.alTime);
        viewHolder.favorited = (ImageView) convertView.findViewById(R.id.iFavorited);
        viewHolder.itemLayout = (FrameLayout) convertView.findViewById(R.id.lItemLayout);
        return viewHolder;
    }

    public View inflateAnnotation(View v, Annotation annotation) {
        ViewHolder vh = (ViewHolder) v.getTag(R.id.listItem);
        if (provider.getFavorited().contains(Integer.valueOf(annotation.getPid()))) {
            vh.favorited.setVisibility(View.VISIBLE);
        } else {
            vh.favorited.setVisibility(View.GONE);
        }

        if (vh.title != null) {
            vh.title.setText(annotation.getTitle());
        }
        if (vh.author != null) {
            if(annotation.getAuthor() != null && !annotation.getAuthor().trim().equals("")) {
                vh.author.setText(annotation.getAuthor());
                vh.author.setVisibility(View.VISIBLE);
            } else {
                vh.author.setVisibility(View.GONE);
            }
        }

        if (vh.line != null) {
            if(annotation.getLid() > 0) {
                vh.line.setText(provider.getProgramLine(annotation.getLid()).getName());
                vh.line.setVisibility(View.VISIBLE);
            } else {
                vh.line.setVisibility(View.GONE);
            }
        }
        if (annotation.getStart() != null && annotation.getEnd() != null) {
            vh.time.setText(formatDate(annotation.getStart()) + " - "
                    + todayFormat.format(annotation.getEnd()));
            vh.time.setVisibility(View.VISIBLE);
        } else {
            vh.time.setVisibility(View.GONE);
        }
        if (annotation.getLocation() != null && !annotation.getLocation().trim().equals("")) {
            vh.place.setText(annotation.getLocation());
            vh.place.setVisibility(View.VISIBLE);
        } else {
            vh.place.setVisibility(View.GONE);
        }

        vh.itemLayout.setBackgroundResource(annotation.getType().getTypeColor());

        return v;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public int getDataSize() {
        return this.totalItems;
    }

    public boolean isDateToday(Date date) {
        Calendar today = Calendar.getInstance(TimeZone.getDefault(), new Locale("cs", "CZ"));
        today.setTime(new Date());

        Calendar compared = Calendar.getInstance();
        compared.setTime(date);

        if (compared.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && compared.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && compared.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            //its today
            return true;
        } else {
            return false;
        }
    }

    private String formatDate(Date date) {

        if (isDateToday(date)) {
            //its today
            return todayFormat.format(date);
        } else {
            return dayFormat.format(date);
        }

    }

    class ViewHolder {
        ImageView favorited;
        TextView title;
        TextView author;
        TextView line;
        TextView time;
        TextView place;
        RelativeLayout firstRow;
        FrameLayout itemLayout;
    }

}


package cz.quinix.condroid.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.ProgramActivity;

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
public class EndlessAdapter extends com.commonsware.cwac.endless.EndlessAdapter {
    private RotateAnimation rotate;
    protected List<Annotation> itemsToAdd;
    protected int totalItems = 0;
    private CondroidActivity activity;
    protected DataProvider provider;

    private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
    private static DateFormat dayFormat = new SimpleDateFormat(
            "EE dd.MM. HH:mm", new Locale("cs", "CZ"));


    public EndlessAdapter(CondroidActivity activity, List<Annotation> items) {
        super(new ArrayAdapter<Annotation>(activity,
                R.layout.annotation_list_item, android.R.id.text1, items));
        this.activity = activity;
        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        this.provider = DataProvider.getInstance(activity);
        totalItems = items.size();
        this.keepOnAppending.set(!(items.size() < DataProvider.ITEMS_PER_PAGE));

    }

  /*  @Override
    public Object getItem(int position) {
        return items.get(position);
    }*/

    public void setItems(List<Annotation> items, boolean refresh) {
        ArrayAdapter<Annotation> a = (ArrayAdapter<Annotation>) this.getWrappedAdapter();
        a.clear();
        this.insertSettedList(items, a);
        this.totalItems = items.size();
        this.keepOnAppending.set(!(items.size() < DataProvider.ITEMS_PER_PAGE));
        if(refresh) {
            this.notifyDataSetChanged();
        }
        Log.i("xxxxx", "i"+this.getCount());
        Log.i("xxxxx", "i"+items.size());
        Log.i("xxxxx", "i"+this.getWrappedAdapter().getCount());
    }

    protected void insertSettedList(List<Annotation> items, ArrayAdapter<Annotation> a) {
        for(int i = 0; i<items.size(); i++) {
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
        return DataProvider.getInstance(activity).getAnnotations(SearchProvider.getSearchQueryBuilder(ProgramActivity.SCREEN_ALL), skip);
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
        viewHolder.author = (TextView) convertView.findViewById(R.id.alAuthor);
        viewHolder.title = (TextView) convertView.findViewById(R.id.alTitle);
        viewHolder.line = (TextView) convertView.findViewById(R.id.alLine);
        viewHolder.place = (TextView) convertView.findViewById(R.id.alPlace);
        viewHolder.time = (TextView) convertView.findViewById(R.id.alTime);
        viewHolder.favorited = (ImageView) convertView.findViewById(R.id.iFavorited);
        viewHolder.programType = (ImageView) convertView.findViewById(R.id.iProgramType);
        viewHolder.place.setVisibility(View.GONE);
        viewHolder.time.setVisibility(View.VISIBLE);
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
            vh.author.setText(annotation.getAuthor());
        }

        if (vh.line != null) {
            vh.line.setText(provider.getProgramLine(annotation.getLid()).getName());
        }
        if (vh.time.getVisibility() == View.VISIBLE &&
                annotation.getStartTime() != null && annotation.getEndTime() != null) {
            vh.time.setText(formatDate(annotation.getStartTime()) + " - "
                    + todayFormat.format(annotation.getEndTime()));
            if (annotation.getLid() > 0) {
                vh.line.setText(vh.line.getText() + ",");
            }
        } else {
            vh.time.setText("");
        }
        if (vh.place.getVisibility() == View.VISIBLE && annotation.getLocation() != null) {
            vh.place.setText(annotation.getLocation());
            if (annotation.getLid() > 0) {
                vh.line.setText(vh.line.getText() + ",");
            }
        } else {
            vh.place.setText("");
        }
        if (vh.programType != null) {
            vh.programType.setImageResource(annotation.getProgramIcon());
        }
        return v;
    }

    public CondroidActivity getActivity() {
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
        ImageView programType;
    }

}


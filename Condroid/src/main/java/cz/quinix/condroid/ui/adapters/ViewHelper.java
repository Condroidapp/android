package cz.quinix.condroid.ui.adapters;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;

/**
 * Created by Jan on 8. 5. 2014.
 */
class ViewHelper {

    @Inject DataProvider provider;

    public final static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
    public final static DateFormat dayFormat = new SimpleDateFormat(
            "EE dd.MM. HH:mm", new Locale("cs", "CZ"));

    public View inflateAnnotation(View v, Annotation annotation) {
        return this.inflateAnnotation(v, annotation, this.getViewHolder(v));
    }


    public View inflateAnnotation(View v, Annotation annotation, ViewHolder vh) {

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

    public ViewHolder getViewHolder(View v) {
        if (v.getTag(R.id.listItem) == null) {
            ViewHolder vh = new ViewHolder();

            this.initializeItemLayout(v, vh);
            v.setTag(R.id.listItem, vh);
        }

        return (ViewHolder) v.getTag(R.id.listItem);
    }

    public ViewHolder initializeItemLayout(View convertView, ViewHolder viewHolder) {
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


    boolean isDateToday(Date date) {
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

    static class ViewHolder {
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

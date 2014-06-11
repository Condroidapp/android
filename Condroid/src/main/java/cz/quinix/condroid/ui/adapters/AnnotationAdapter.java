package cz.quinix.condroid.ui.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.model.Annotation;
import roboguice.RoboGuice;

/**
 * Created by Jan on 8. 5. 2014.
 */
public class AnnotationAdapter extends ArrayAdapter<Annotation> implements IAppendable, IReplaceable {

    @Inject
    ViewHelper viewHelper;

    public AnnotationAdapter(Context context, List<Annotation> objects) {
        super(context, R.layout.annotation_list_item, objects);
        RoboGuice.getInjector(context).injectMembers(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) this.getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.annotation_list_item, parent, false);
            if (convertView == null) {
                return null;
            }

            viewHelper.setFavoritedIcon(this.getContext().getAssets(), convertView);
        }


        Annotation it;
        try {
            it = this.getItem(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        if (it != null) {
            return this.viewHelper.inflateAnnotation(convertView, it);
        }
        return null;
    }

    @Override
    public void append(List<Annotation> items) {
        for (Annotation annotation : items) {
            this.add(annotation);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void replace(List<Annotation> items) {
        this.clear();
        this.append(items);
    }
}

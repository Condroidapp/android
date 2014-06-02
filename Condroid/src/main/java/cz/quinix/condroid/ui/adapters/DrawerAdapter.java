package cz.quinix.condroid.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import cz.quinix.condroid.R;

/**
 * Created by Jan on 1. 6. 2014.
 */
public class DrawerAdapter extends ArrayAdapter<String> {


    public DrawerAdapter(Context context, String[] objects) {
        super(context, R.layout.drawer_item, R.id.drawerText, objects);
    }
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) this.getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.drawer_item, parent, false);
        }
        if(convertView == null) {
            return null;
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

    }*/
}

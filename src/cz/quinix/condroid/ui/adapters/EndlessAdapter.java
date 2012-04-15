package cz.quinix.condroid.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.AllAnotations;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 15.4.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class EndlessAdapter extends com.commonsware.cwac.endless.EndlessAdapter {
    private RotateAnimation rotate;
    private List<Annotation> itemsToAdd;
    private int itemsPerPage = 0;
    private CondroidActivity activity;

    public EndlessAdapter(CondroidActivity activity, List<Annotation> items) {
        super(new ArrayAdapter<Annotation>(activity,
                R.layout.annotation_list_item, android.R.id.text1, items));
        this.activity = activity;
        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        this.itemsPerPage = items.size();
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        if (this.itemsPerPage == 0
                || ((this.getCount() - 1) % this.itemsPerPage != 0)) {
            return false;
        }
        this.itemsToAdd = DataProvider.getInstance(activity).getAnnotations(
                /*activity.getSearchQuery().buildCondition()*/"",
                (this.getCount() / this.itemsPerPage));

        return (this.itemsToAdd.size() == this.itemsPerPage);
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
    public void refreshDataset() {
        super.refreshDataset();
        ((ListView)activity.findViewById(R.id.listView)).setSelection(0);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {


        //if (v == null) {
        LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.annotation_list_item, null);
        //}
        Annotation it = null;
        try {
            it = (Annotation) this.getItem(position);
        } catch (IndexOutOfBoundsException e) {

        }
        if (it != null) {
            return this.inflanteAnnotation(v, it);
        }
        return super.getView(position, v, parent);
    }

    public View inflanteAnnotation(View v, Annotation annotation) {

        if(false /*provider.getFavorited().contains(Integer.valueOf(annotation.getPid()))*/) {
            ((ImageView) v.findViewById(R.id.iFavorited)).setVisibility(View.VISIBLE);
        } else {
            ((ImageView) v.findViewById(R.id.iFavorited)).setVisibility(View.GONE);
        }
        TextView tw = (TextView) v.findViewById(R.id.alTitle);
        if (tw != null) {
            tw.setText(annotation.getTitle());
        }
        TextView tw2 = (TextView) v.findViewById(R.id.alSecondLine);

        if (tw != null) {

            tw2.setText(annotation.getAuthor());
        }
        TextView tw3 = (TextView) v.findViewById(R.id.alThirdLine);
        if (tw2 != null) {
            String date = "";
            /*if (annotation.getStartTime() != null
                    && annotation.getEndTime() != null) {
                date = ", " + formatDate(annotation.getStartTime()) + " - "
                        + todayFormat.format(annotation.getEndTime());
            }     */
            /*tw3.setText(provider.getProgramLine(annotation.getLid()).getName() +  date);  */
        }
        ImageView iw = (ImageView) v.findViewById(R.id.iProgramType);
        if(iw != null) {
            iw.setImageResource(annotation.getProgramIcon());
        }
        return v;
    }

    public CondroidActivity getActivity() {
        return this.activity;
    }

}

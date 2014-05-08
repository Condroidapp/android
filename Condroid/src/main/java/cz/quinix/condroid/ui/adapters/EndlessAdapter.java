package cz.quinix.condroid.ui.adapters;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 15.4.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class EndlessAdapter extends com.commonsware.cwac.endless.EndlessAdapter {
    private IAdapterDataProvider dataLoader;
    private RotateAnimation rotate;
    protected List<Annotation> itemsToAdd;
   // protected int totalItems = 0;
    private Activity activity;




    public EndlessAdapter(Activity activity, BaseAdapter wrapped, IAdapterDataProvider<Annotation> dataLoader) {
        super(wrapped);

        this.activity = activity;
        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        if(wrapped.getCount() < DataProvider.ITEMS_PER_PAGE) {
            this.stopAppending();
        }
        this.dataLoader = dataLoader;
        wrapped.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                EndlessAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                EndlessAdapter.this.notifyDataSetInvalidated();
            }
        });
    }


    public void setItems(List<Annotation> items) {
        IReplaceable a = (IReplaceable) this.getWrappedAdapter();

        a.replace(items);

        if(items.size() < DataProvider.ITEMS_PER_PAGE) {
            this.stopAppending();
        }
        else {
            this.restartAppending();
        }

    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        if (this.getCount() == 0) {
            return false;
        }
        this.itemsToAdd = this.getPrecachedData(this.getWrappedAdapter().getCount());

        return !(itemsToAdd.size() < DataProvider.ITEMS_PER_PAGE);
    }

    private List<Annotation> getPrecachedData(int skip) {
        return this.dataLoader.getData(skip);
    }

    @Override
    protected void appendCachedData() {
        if (this.itemsToAdd != null && this.itemsToAdd.size() > 0) {
            IAppendable a = (IAppendable) this.getWrappedAdapter();

            a.append(this.itemsToAdd);

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

    public Activity getActivity() {
        return this.activity;
    }
}


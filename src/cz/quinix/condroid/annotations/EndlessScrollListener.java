package cz.quinix.condroid.annotations;

import java.util.concurrent.ExecutionException;

import android.R;
import android.app.Activity;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

public class EndlessScrollListener implements OnScrollListener {
	
	private int visibleThreshold = 1;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private AnnotationsActivity activity;
    
    public EndlessScrollListener() {
		// TODO Auto-generated constructor stub
	}
    
    public EndlessScrollListener(AnnotationsActivity acivity) {
    	this.activity = acivity;
    }

	public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (this.loading) {
            if (totalItemCount > this.previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
                TextView tw = (TextView) this.activity.findViewById(cz.quinix.condroid.R.id.annotation_loading);
    			tw.setText("");
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + this.visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
        	this.activity.urlBuilder.addParam("page", String.valueOf(this.currentPage +1));
            try {
				this.activity.addAnnotations(new XMLLoader().execute(this.activity.getUrl()).get());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TextView tw = (TextView) this.activity.findViewById(cz.quinix.condroid.R.id.annotation_loading);
			tw.setText(this.activity.getString(cz.quinix.condroid.R.string.loading));
            loading = true;
        }

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}

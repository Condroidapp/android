package cz.quinix.condroid.annotations;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import com.commonsware.cwac.endless.EndlessAdapter;

import cz.quinix.condroid.R;

import java.util.ArrayList;

public class EndlessDemo extends ListActivity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.annotation_list);
		
		ArrayList<Integer> items=new ArrayList<Integer>();
		
		for (int i=0;i<25;i++) { items.add(i); }
		
		setListAdapter(new DemoAdapter(items));
	}
	
	class DemoAdapter extends EndlessAdapter {
		private RotateAnimation rotate=null;
		
		DemoAdapter(ArrayList<Integer> list) {
			super(new ArrayAdapter<Integer>(EndlessDemo.this,
																			R.layout.row,
																			android.R.id.text1,
																			list));
			
			rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
																	0.5f, Animation.RELATIVE_TO_SELF,
																	0.5f);
			rotate.setDuration(600);
			rotate.setRepeatMode(Animation.RESTART);
			rotate.setRepeatCount(Animation.INFINITE);
		}
		
		@Override
		protected View getPendingView(ViewGroup parent) {
			View row=getLayoutInflater().inflate(R.layout.row, null);
			
			View child=row.findViewById(android.R.id.text1);
			
			child.setVisibility(View.GONE);
			
			child=row.findViewById(R.id.throbber);
			child.setVisibility(View.VISIBLE);
			child.startAnimation(rotate);
			
			return(row);
		}
		
		@Override
		protected boolean cacheInBackground() {
			SystemClock.sleep(10000);				// pretend to do work
			
			return(getWrappedAdapter().getCount()<75);
		}
		
		@Override
		protected void appendCachedData() {
			if (getWrappedAdapter().getCount()<75) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Integer> a=(ArrayAdapter<Integer>)getWrappedAdapter();
				
				for (int i=0;i<25;i++) { a.add(a.getCount()); }
			}
		}
	}
}

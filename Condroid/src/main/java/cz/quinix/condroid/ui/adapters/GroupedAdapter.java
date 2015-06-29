package cz.quinix.condroid.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.util.DateHelper;
import cz.quinix.condroid.util.DateTimeFactory;
import roboguice.RoboGuice;

public class GroupedAdapter extends BaseAdapter implements IAppendable, IReplaceable {

	private final Context context;

	List<Entry> entries;

	@Inject
	ViewHelper viewHelper;

	public GroupedAdapter(Context context, List<Annotation> items) {
		this.context = context;
		RoboGuice.getInjector(context).injectMembers(this);
		this.entries = new ArrayList<Entry>();

		this.addAnnotations(items);
	}

	private void addAnnotations(List<Annotation> items) {
		if (items.size() == 0) {
			return;
		}

		Collections.sort(items, new Comparator<Annotation>() {
			@Override
			public int compare(Annotation lhs, Annotation rhs) {
				return lhs.getStart().compareTo(rhs.getStart());
			}
		});
		Date previous = null;
		if (!this.entries.isEmpty()) {
			for (int i = this.entries.size() - 1; i > 0; i--) {
				Entry item = this.entries.get(i);
				if (!item.isSeparator()) {
					previous = item.annotation.getStart();
					break;
				}
			}
		}
		if (previous == null) {
			previous = items.get(0).getStart();
			this.entries.add(new Entry(previous));
		}
		boolean nowHeader = DateHelper.isBeforeNow(previous);

		for (Annotation annotation : items) {
			if (!annotation.getStart().equals(previous)) {
				if (!DateHelper.isBeforeNow(annotation.getStart()) || !nowHeader) {
					this.entries.add(new Entry(annotation.getStart()));
					if (DateHelper.isBeforeNow(previous)) {
						nowHeader = true;
					}
				}
			}
			this.entries.add(new Entry(annotation));
			previous = annotation.getStart();
		}

	}

	@Override
	public int getCount() {
		return this.entries.size();
	}

	@Override
	public Entry getItem(int position) {
		return this.entries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.annotation_list_item, parent, false);

			if (convertView == null) {
				return null;
			}
			viewHelper.setFavoritedIcon(this.context.getAssets(), convertView);
			ViewHolder holder = new ViewHolder();
			this.viewHelper.initializeItemLayout(convertView, holder);
			holder.runningTitle = (TextView) convertView.findViewById(R.id.tRunningTitle);
			convertView.setTag(R.id.listItem, holder);
		}

		Entry entry;
		try {
			entry = this.getItem(position);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		if (entry != null) {
			this.setLayout(convertView, entry);
			if (entry.isSeparator()) {
				ViewHolder holder = (ViewHolder) this.viewHelper.getViewHolder(convertView);
				return this.inflanteSeparator(convertView, entry.header, holder);
			} else {
				return this.viewHelper.inflateAnnotation(convertView, entry.annotation);
			}
		}

		return null;
	}

	private void setLayout(View v, Entry item) {
		LayoutHolder lh = (LayoutHolder) v.getTag(R.id.layoutList);
		if (lh == null) {
			lh = new LayoutHolder();
			lh.separatorLayout = v.findViewById(R.id.ldateLayout);
			lh.itemLayout = v.findViewById(R.id.lItemLayout);
			v.setTag(R.id.layoutList, lh);
		}
		if (item.isSeparator()) {
			lh.separatorLayout.setVisibility(View.VISIBLE);
			lh.itemLayout.setVisibility(View.GONE);
		} else {
			lh.itemLayout.setVisibility(View.VISIBLE);
			lh.separatorLayout.setVisibility(View.GONE);
		}
	}

	private View inflanteSeparator(View view, Date header, ViewHolder holder) {
		if (header.before(DateTimeFactory.getNow().toDate())) {
			holder.runningTitle.setText(R.string.runningNow);
		} else {
			if (this.viewHelper.isDateToday(header)) {
				holder.runningTitle.setText(this.context.getString(R.string.today) + ", " + ViewHelper.todayFormat.format(header));
			} else {
				holder.runningTitle.setText(ViewHelper.dayFormat.format(header));
			}
		}
		view.setFocusable(false);
		view.setClickable(false);
		return view;
	}

	@Override
	public void append(List<Annotation> items) {
		this.addAnnotations(items);
		this.notifyDataSetChanged();
	}

	@Override
	public void replace(List<Annotation> items) {
		this.entries = new ArrayList<Entry>();
		this.addAnnotations(items);
		this.notifyDataSetChanged();
	}

	public Annotation getTopItem() {
		Annotation earliest = null;
		for (Entry e : this.entries) {
			if (e.isSeparator() && !DateHelper.isBeforeNow(e.header)) {
				return earliest;
			}
			if (!e.isSeparator() && (earliest == null || earliest.getEnd().after(e.annotation.getEnd()))) {
				earliest = e.annotation;
			}
		}
		return null;
	}

	public static class Entry {

		public Annotation annotation;

		public Date header;

		public Entry(Annotation a) {
			this.annotation = a;
		}

		public Entry(Date a) {
			this.header = a;
		}

		public boolean isSeparator() {
			return annotation == null;
		}

	}

	class ViewHolder extends ViewHelper.ViewHolder {

		TextView runningTitle;
	}

	class LayoutHolder {

		View separatorLayout;

		View itemLayout;
	}
}

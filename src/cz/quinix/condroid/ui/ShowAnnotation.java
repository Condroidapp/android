package cz.quinix.condroid.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;

public class ShowAnnotation extends CondroidActivity {

	private Annotation annotation;
	private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
	private static DateFormat dayFormat = new SimpleDateFormat(
			"EE dd.MM. HH:mm", new Locale("cs", "CZ"));
	private DataProvider provider;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		provider = DataProvider.getInstance(getApplicationContext());
		this.annotation = (Annotation) this.getIntent().getSerializableExtra(
				"annotation");
		this.setContentView(R.layout.annotation);

		TextView title = (TextView) this.findViewById(R.id.annot_title);
		title.setText(this.annotation.getTitle());

		TextView author = (TextView) this.findViewById(R.id.annot_author);
		author.setText(this.annotation.getAuthor());
		
		TextView pid = (TextView) this.findViewById(R.id.annot_pid);
		pid.setText(this.annotation.getPid());

		String date = "";
		if (annotation.getStartTime() != null
				&& annotation.getEndTime() != null) {
			date = formatDate(annotation.getStartTime()) + " - "
					+ todayFormat.format(annotation.getEndTime());
		}

		TextView line = (TextView) this.findViewById(R.id.annot_line);
		line.setText(", " + DataProvider.getInstance(getApplicationContext())
				.getProgramLine(this.annotation.getLid()).getName());
		if(this.annotation.getLocation() != null && this.annotation.getLocation() != "") {
			TextView location = (TextView) this.findViewById(R.id.annot_location);
			location.setText(this.annotation.getLocation());
		}
		else {
			this.findViewById(R.id.annot_location).setVisibility(View.GONE);
		}
		
		TextView info = (TextView) this.findViewById(R.id.annot_time);
		if(date != "") {
			
			info.setText(date);
			
		} else {
			findViewById(R.id.lDate).setVisibility(View.GONE);
		}
		((TextView) this.findViewById(R.id.annot_type)).setText(", " +this
				.getTextualTypes());
		TextView text = (TextView) this.findViewById(R.id.annot_text);
		text.setText(this.annotation.getAnnotation());
		text.setMovementMethod(new ScrollingMovementMethod());
		((ImageView) this.findViewById(R.id.iProgramIcon))
				.setImageResource(annotation.getProgramIcon());
		ImageView share = (ImageView) this.findViewById(R.id.iShare);
		share.setOnClickListener(new ShareProgramListener(this));
		
		ImageView favorite = (ImageView) this.findViewById(R.id.iFavorite);
		if(provider.getFavorited().contains(Integer.valueOf(annotation.getPid()))) {
			favorite.setImageResource(R.drawable.star_active);
		}
		favorite.setOnClickListener(new MakeFavoritedListener(this));

	}

	private CharSequence getTextualTypes() {
		String ret = getTextualType(annotation.getType());
		String[] aT = annotation.getAdditionalTypes();
		for(int i = 0; i<aT.length; i++) {
			if(aT[i].length() > 0)
				ret+=", "+getTextualType(aT[i]);
		}
		return ret;
	}

	private String getTextualType(String type) {
		if (type.equalsIgnoreCase("P"))
			return getString(R.string.lecture);

		if (type.equalsIgnoreCase("B"))
			return getString(R.string.discussion);

		if (type.equalsIgnoreCase("C"))
			return getString(R.string.theatre);

		if (type.equalsIgnoreCase("D"))
			return getString(R.string.document);

		if (type.equalsIgnoreCase("F"))
			return getString(R.string.projection);

		if (type.equalsIgnoreCase("G"))
			return getString(R.string.game);

		if (type.equalsIgnoreCase("H"))
			return getString(R.string.music);

		if (type.equalsIgnoreCase("Q"))
			return getString(R.string.quiz);

		if (type.equalsIgnoreCase("W"))
			return getString(R.string.workshop);

		return "";
	}

	private String formatDate(Date date) {
		Calendar today = Calendar.getInstance(new Locale("cs", "CZ"));
		today.setTime(new Date());

		Calendar compared = Calendar.getInstance();
		compared.setTime(date);

		if (compared.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& compared.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& compared.get(Calendar.DAY_OF_MONTH) == today
						.get(Calendar.DAY_OF_MONTH)) {
			// its today
			return todayFormat.format(date);
		} else {
			return dayFormat.format(date);
		}

	}

	public Annotation getAnnotation() {
		return annotation;
	}
}

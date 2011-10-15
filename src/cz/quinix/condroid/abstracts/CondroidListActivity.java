package cz.quinix.condroid.abstracts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.AboutActivity;

public abstract class CondroidListActivity extends ListActivity {

	protected DataProvider provider;



	private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
	private static DateFormat dayFormat = new SimpleDateFormat(
			"EE dd.MM. HH:mm", new Locale("cs","CZ"));
	static {
		todayFormat.setTimeZone(TimeZone.getDefault());
		dayFormat.setTimeZone(TimeZone.getDefault());
	}

	public View inflanteAnnotation(View v, Annotation annotation) {

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
			if (annotation.getStartTime() != null
					&& annotation.getEndTime() != null) {
				date = ", " + formatDate(annotation.getStartTime()) + " - "
						+ todayFormat.format(annotation.getEndTime());
			}
			tw3.setText(provider.getProgramLine(annotation.getLid()).getName() +  date);
		}
		ImageView iw = (ImageView) v.findViewById(R.id.iProgramType);
		if(iw != null) {
			iw.setImageResource(annotation.getProgramIcon());
		}
		return v;
	}
	
	

	public boolean isDateToday (Date date) {
		Calendar today = Calendar.getInstance(TimeZone.getDefault(), new Locale("cs", "CZ"));
		today.setTime(new Date());

		Calendar compared = Calendar.getInstance();
		compared.setTime(date);

		if (compared.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& compared.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& compared.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
			//its today
			return true;
		}
		else {
			return false;
		}
	}

	private String formatDate(Date date) {
		
		if (isDateToday(date)) {
			//its today
			return todayFormat.format(date);
		}
		else {
			return dayFormat.format(date);
		}

	}
}

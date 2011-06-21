package cz.quinix.condroid.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.widget.TextView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;

public class ShowAnnotation extends CondroidActivity {

	private Annotation annotation;
	private static DateFormat todayFormat = new SimpleDateFormat("HH:mm");
	private static DateFormat dayFormat = new SimpleDateFormat(
			"EE dd.MM. HH:mm", new Locale("cs","CZ"));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.annotation = (Annotation) this.getIntent().getSerializableExtra(
				"annotation");
		this.setContentView(R.layout.annotation);

		TextView title = (TextView) this.findViewById(R.id.annot_title);
		title.setText(this.annotation.getTitle());

		TextView author = (TextView) this.findViewById(R.id.annot_author);
		author.setText(this.annotation.getAuthor());

		String date = "";
		if (annotation.getStartTime() != null
				&& annotation.getEndTime() != null) {
			date = formatDate(annotation.getStartTime()) + " - "
					+ todayFormat.format(annotation.getEndTime()) + ", ";
		}
		
		TextView line = (TextView) this.findViewById(R.id.annot_line);
		line.setText(DataProvider.getInstance(getApplicationContext())
				.getProgramLine(this.annotation.getLid()).getName());
		
		TextView info = (TextView) this.findViewById(R.id.annot_info);
		info.setText(this.annotation.getPid()
				+ ", "
				+ date
				//+ this.annotation.getLength()
				//+ ", "
				+ this.annotation.getType());

		TextView text = (TextView) this.findViewById(R.id.annot_text);
		text.setText(this.annotation.getAnnotation());

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
}

package cz.quinix.condroid.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jan on 25. 6. 2015.
 */
public class DateHelper {

	public static boolean isToday(Date date) {
		Calendar today = Calendar.getInstance(TimeZone.getDefault(), new Locale("cs", "CZ"));
		today.setTime(DateTimeFactory.getNow().toDate());

		Calendar compared = Calendar.getInstance();
		compared.setTime(date);

		//its today
		return compared.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& compared.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& compared.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
	}

	public static boolean isBeforeNow(Date previous) {
		return previous.before(DateTimeFactory.getNow().toDate());
	}
}

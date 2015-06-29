package cz.quinix.condroid.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFactory {

	private final static DateTime now = DateTime.now();

	public static DateTime create() {
		return new DateTime();
	}

	public static DateTime getNow() {
		DateTime now = DateTime.now();
		now = now.plusDays(3);

		return now;
	}

}
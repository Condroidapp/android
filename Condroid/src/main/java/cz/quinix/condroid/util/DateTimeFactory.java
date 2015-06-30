package cz.quinix.condroid.util;

import org.joda.time.DateTime;

public class DateTimeFactory {

	private final static DateTime now = DateTime.now();

	public static DateTime create() {
		return new DateTime();
	}

	public static DateTime getNow() {
		return now;
	}

}

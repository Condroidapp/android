package cz.quinix.condroid.util;

import org.joda.time.DateTime;

public class DateTimeFactory {

	public static DateTime create() {
		return new DateTime();
	}

	public static DateTime getNow() {
		return DateTime.now();
	}

}

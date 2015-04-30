package cz.quinix.condroid.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.util.Date;

public class DateTypeAdapter implements JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		DateTimeFormatter format = ISODateTimeFormat
				.dateTimeNoMillis();

		return format.parseDateTime(json.getAsJsonPrimitive().getAsString()).toDate();
	}
}

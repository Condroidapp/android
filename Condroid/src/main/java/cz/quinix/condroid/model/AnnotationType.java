package cz.quinix.condroid.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.quinix.condroid.R;

public class AnnotationType implements Serializable {

	public static final char DISCUSSION = 'B';

	public static final char CEREMONY = 'C';

	public static final char DOCUMENTARY = 'D';

	public static final char PROJECTION = 'F';

	public static final char TOURNAMENT = 'G';

	public static final char MUSIC = 'H';

	public static final char LECTURE = 'P';

	public static final char COMPETITION = 'Q';

	public static final char WORKSHOP = 'W';

	public String mainType = "";

	public List<String> secondaryTypes = new ArrayList<String>();

	public int getTypeColor() {
		switch (this.mainType.charAt(0)) {
			case DISCUSSION:
				return R.color.discussion;
			case CEREMONY:
				return R.color.ceremony;
			case DOCUMENTARY:
				return R.color.documentary;
			case PROJECTION:
				return R.color.projection;
			case TOURNAMENT:
				return R.color.tournament;
			case MUSIC:
				return R.color.music;
			case LECTURE:
				return R.color.lecture;
			case COMPETITION:
				return R.color.competition;
			case WORKSHOP:
				return R.color.workshop;
			default:
				return R.color.unknownType;
		}
	}

	public static int getTextualType(String x) {
		if (x.equalsIgnoreCase("P"))
			return (R.string.lecture);

		if (x.equalsIgnoreCase("B"))
			return (R.string.discussion);

		if (x.equalsIgnoreCase("C"))
			return (R.string.theatre);

		if (x.equalsIgnoreCase("D"))
			return (R.string.document);

		if (x.equalsIgnoreCase("F"))
			return (R.string.projection);

		if (x.equalsIgnoreCase("G"))
			return (R.string.game);

		if (x.equalsIgnoreCase("H"))
			return (R.string.music);

		if (x.equalsIgnoreCase("Q"))
			return (R.string.quiz);

		if (x.equalsIgnoreCase("W"))
			return (R.string.workshop);

		throw new IllegalStateException("Invalid type");
	}

}

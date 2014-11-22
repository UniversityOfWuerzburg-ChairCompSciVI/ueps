package de.uniwue.info6.misc;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 *
 * @author Michael
 */
public class DateFormatTools {

	/**
	 *
	 *
	 * @param date
	 * @return
	 */
	public static String getGermanFormat(Date date) {
		if (date != null) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, new Locale("de", "DE"));
			return df.format(date);
		}
		return null;
	}

}

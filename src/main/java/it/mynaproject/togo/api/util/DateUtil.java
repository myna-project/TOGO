/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class DateUtil {

	public static String formatDateString(Date date, String format) {

		final SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		return dateFormat.format(date);
	}

	public static Date add(Date date, int field, int amount) {

		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}

	static public Date extractStartDate(Date start, Date end, int hour) {

		if (start == null)
			start = DateUtil.add(end, Calendar.HOUR_OF_DAY, -hour);

		return start;
	}

	static public Date extractStartDate(Date start, Date end) {
		return extractStartDate(start, end, 1);
	}

	static public Date extractEndDate(Date end) {

		if (end == null)
			end = new Date();

		return end;
	}
}

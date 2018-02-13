package com.qount.invoice.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class DateUtils {
	private static final Logger LOGGER = Logger.getLogger(DateUtils.class);

	public static Date getDateFromString(String date, String format) {
		Date parsedDate = null;
		try {
			if (StringUtils.isNoneBlank(date, format)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				parsedDate = dateFormat.parse(date);
			}
		} catch (Exception e) {
			LOGGER.error("Error parsing date", e);
		}
		return parsedDate;
	}
	
	public static Date getDateFromString(String date, SimpleDateFormat dateFormat) {
		Date parsedDate = null;
		try {
			if (StringUtils.isNoneBlank(date) && dateFormat!=null) {
				parsedDate = dateFormat.parse(date);
			}
		} catch (Exception e) {
			LOGGER.error("Error parsing date", e);
		}
		return parsedDate;
	}

	public static String getStringFromDate(Date date, String format) {
		String formattedDate = null;
		if (date != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				formattedDate = dateFormat.format(date);
			} catch (Exception e) {
				LOGGER.error("Error formating date", e);
			}
		}
		return formattedDate;
	}
	public static Long getDatesDifference(String date1, String dateFormat1, String date2, String dateFormat2) {
		Long difference = null;
		try {
			DateTime d1 = new DateTime(getDateFromString(date1, dateFormat1));
			DateTime d2 = new DateTime(getDateFromString(date2, dateFormat2));
			difference = new Interval(d1.getMillis(), d2.getMillis()).toDuration().getStandardDays();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return difference;

	}
	public static String getCurrentDate(SimpleDateFormat sdf){
		return sdf.format(new Date());
	}
	
	public static Timestamp getTimestampFromString(String date) {
		Timestamp timestamp = null;
		if (date != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date parsedDate = dateFormat.parse(date);
				timestamp = new java.sql.Timestamp(parsedDate.getTime());
			} catch (Exception e) {// this generic but you can control another
									// types of exception look the origin of
									// exception
				LOGGER.error("Error while formating", e);
			}
		}

		return timestamp;

	}
	
	public static Timestamp getTimestampFromString(String date, String format) {
		Timestamp timestamp = null;
		if (date != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				Date parsedDate = dateFormat.parse(date);
				timestamp = new java.sql.Timestamp(parsedDate.getTime());
			} catch (Exception e) {// this generic but you can control another
									// types of exception look the origin of
									// exception
				LOGGER.error("Error while formating", e);
			}
		}

		return timestamp;

	}

	public static String formatToString(Timestamp timestamp) {
		String formatedDate = null;
		if (timestamp != null) {
			long millsec = timestamp.getTime();
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				formatedDate = dateFormat.format(new Date(millsec));
			} catch (Exception e) {// this generic but you can control another
									// types of exception look the origin of
									// exception
				LOGGER.error("Error while formating", e);
			}
		}
		return formatedDate;

	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(getTimestampFromString("2017-11-08 11:07:40", "yyyy-MM-dd hh:mm:ss"));
		getTimestampFromString(null);
	}
}

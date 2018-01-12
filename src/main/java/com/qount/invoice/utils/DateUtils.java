package com.qount.invoice.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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
		String str_date = "11/01/18";
		
		 DateFormat formatter;
	      formatter = new SimpleDateFormat("yyyy-MM-dd");
	       // you can change format of date
	      Date date = formatter.parse(str_date);
	      java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
	      System.out.println(timeStampDate);
	}
}

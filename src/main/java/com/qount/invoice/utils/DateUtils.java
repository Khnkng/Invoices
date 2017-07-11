package com.qount.invoice.utils;

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
	
	public static String getCurrentDate(SimpleDateFormat sdf){
		return sdf.format(new Date());
	}
	

	public static void main(String[] args) {
		System.out.println(getCurrentDate(Constants.DATE_TO_INVOICE_FORMAT));
	}
}

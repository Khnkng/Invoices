package com.qount.invoice.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.model.Company2;
import com.qount.invoice.model.InvoiceMetrics;

public class CurrencyConverter {

	private static final Logger LOGGER = Logger.getLogger(CurrencyConverter.class);

	private static final CloseableHttpClient HTTPCLIENT = HttpClients.createDefault();

	private Map<String, Float> coversionRateMappings = new HashMap<>();

	private static final String ID = "a269ec8ac06bc87d9e0b18a44cfc0d42";

	public float convert(String currencyFrom, String currencyTo) {
		Float value = coversionRateMappings.get(currencyFrom + currencyTo);
		try {
			if (value != null) {
				return value;
			}
			HttpGet httpGet = new HttpGet("http://apilayer.net/api/convert?access_key=" + ID + "&from=" + currencyFrom + "&to=" + currencyTo + "&amount=1");
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = HTTPCLIENT.execute(httpGet, responseHandler);
			JSONObject responseJSON = new JSONObject(responseBody);
			Double result = responseJSON.optDouble("result");
			value = result.floatValue();
			coversionRateMappings.put(currencyFrom + currencyTo, value);
		} catch (Exception e) {
			LOGGER.error("Error while converting amount", e);
		}
		return value;
	}

	public float convert(String currencyFrom, String currencyTo, String date) throws IOException {
		Float value = coversionRateMappings.get(currencyFrom + currencyTo + date);
		if (value != null) {
			return value;
		}
		HttpGet httpGet = new HttpGet("http://apilayer.net/api/convert?access_key=" + ID + "&from=" + currencyFrom + "&to=" + currencyTo + "&amount=1&date=" + date);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = HTTPCLIENT.execute(httpGet, responseHandler);
		JSONObject responseJSON = new JSONObject(responseBody);
		Double result = responseJSON.optDouble("result");
		value = result.floatValue();
		coversionRateMappings.put(currencyFrom + currencyTo + date, value);
		return value;
	}
	
	public String convertToDashboardCurrency(Double value, Company2 company) {
        Double customizedValue = value;
        if (company.getReportCurrency() != null && !"".equals(company.getReportCurrency()) && !(company.getConversionValue() <= 0)) {
            customizedValue = customizedValue * company.getConversionValue();
        }
        return customizedValue.toString();
    }

	public InvoiceMetrics converterValues(InvoiceMetrics invoiceMetrics, Company2 company){
		CurrencyConverter currencyConverter = new CurrencyConverter();
		double value = Double.parseDouble(invoiceMetrics.getAvgOutstandingAmount());
		invoiceMetrics.setAvgOutstandingAmount(currencyConverter.convertToDashboardCurrency(value, company));
		
		double value1 = Double.parseDouble(invoiceMetrics.getTotalPastDueAmount());
		invoiceMetrics.setTotalPastDueAmount(currencyConverter.convertToDashboardCurrency(value1, company));
		
		double value2 = Double.parseDouble(invoiceMetrics.getTotalReceivableAmount());
		invoiceMetrics.setTotalReceivableAmount(currencyConverter.convertToDashboardCurrency(value2, company));
		
		double value3 = Double.parseDouble(invoiceMetrics.getTotalReceivedLast30Days());
		invoiceMetrics.setTotalReceivedLast30Days(currencyConverter.convertToDashboardCurrency(value3, company));
		
		return invoiceMetrics;
		
	}
	public static void main(String[] args) {
		CurrencyConverter ycc = new CurrencyConverter();
		try {
			float current = ycc.convert("INR", "USD", "2005-01-01");
			System.out.println(current);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

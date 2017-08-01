package com.qount.invoice.utils;

import java.util.HashMap;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;

/**
 * 
 * @author MateenAhmed Date 15 Jun 2017 utilities class for payment spring
 */
public class PaymentSpringUtilities {

	private static final Logger LOGGER = Logger.getLogger(PaymentSpringUtilities.class);
	private static final HashMap<String, String> urlMap = new HashMap<String, String>();

	public static JSONObject invokePaymentSpringApi(String companyId, JSONObject payloadObj, String urlAction, String httpMethod) throws Exception {
		try {
			LOGGER.debug("entered invokePaymentSpringApi companyId:" + companyId + " payloadObj:" + payloadObj + " urlAction:" + urlAction + " httpMethod:" + httpMethod);
			if (!CommonUtils.isValidStrings(companyId, urlAction, httpMethod) || !CommonUtils.isValidJSON(payloadObj)) {
				throw new WebApplicationException("invalid input");
			}
			String path = urlMap.get(companyId + urlAction);
			if (StringUtils.isEmpty(path)) {
				path = LTMUtils.getHostAddress("payment.spring.docker.hostname", "payment.spring.docker.port", "payment.spring.base.url");
				if (StringUtils.isEmpty(path)) {
					throw new WebApplicationException("unable to connect to qount payment server");
				}
				path = path + "PaymentSpring/companies/" + companyId + "/" + urlAction;
				path = path.replace("{comapnyID}", companyId);
			}
			urlMap.put(companyId + urlAction, path);
			if (StringUtils.isNotBlank(PropertyManager.getProperty("print.payment.spring"))) {
				System.out.println("*******************************************");
				System.out.println(path);
				System.out.println(payloadObj);
				System.out.println("*******************************************");
			}
			JSONObject responseJson = null;
			if (StringUtils.equals(httpMethod, Constants.POST)) {
				responseJson = HTTPClient.post(path, payloadObj.toString());
			}
			if (responseJson != null && responseJson.length() != 0) {
				return responseJson;
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited invokePaymentSpringApi companyId:" + companyId + " payloadObj:" + payloadObj + " urlAction:" + urlAction);
		}
		return null;
	}
}

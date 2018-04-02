package com.qount.invoice.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.qount.invoice.model.Notification;
import com.qount.invoice.utils.Constants;

public class NotificationService {

	private static final Logger LOGGER = Logger.getLogger(NotificationService.class);

	public static void notifyOnInvoicePayment(String companyId, String invoiceId, String entityId, String entityName, Double amount) {
		try {
			LOGGER.debug("Writing comment to que for notification");
			String mode = System.getenv("SERVER_INSTANCE_MODE");
//			if ("production".equalsIgnoreCase(mode)) {
				// enabling only for prod test company
				String formattedAmount = String.format("%.2f", amount);
				Notification notification = new Notification();
				notification.setId(UUID.randomUUID().toString());
				notification.setCompanyID(companyId);
				notification.setEntityID(entityId);
				notification.setEntityName(entityName);
				notification.setType("invoicePayment");
				Map<String, String> notificationsParams = new HashMap<>();
				notificationsParams.put("amount", formattedAmount);
				notificationsParams.put("invoiceId", invoiceId);
				notification.setNotificationParameters(notificationsParams);
				String notificationString = Constants.GSON.toJson(notification);
				SQSClient.sendMessageAsync(notificationString);
//			}
		} catch (Exception e) {
			LOGGER.error("Error writing comment details to que", e);
		}
	}

}

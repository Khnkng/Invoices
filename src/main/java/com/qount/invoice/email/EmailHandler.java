package com.qount.invoice.email;

import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.json.JSONObject;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoiceMail;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.JersyClientUtilities;
import com.qount.invoice.utils.Utilities;

public class EmailHandler {

	private static final Logger LOGGER = Logger.getLogger(EmailHandler.class);

	public static boolean sendEmail(File file, JSONObject inputJson,String  invoiceId) throws Exception{
		MultiPart multipartEntity = null;
		FormDataMultiPart dataMultiPart = null;
		try {
			String fileName = inputJson.optString("fileName");
			String template = inputJson.optString("template");
			if(StringUtils.isEmpty(template)){
				InvoiceMail invoiceMail = MySQLManager.getInvoiceDAOInstance().getInvoiceMailDetails(invoiceId);
				String currencySymbol = Utilities.getCurrencyHtmlSymbol(invoiceMail.getCurrencyHtml_symbol()	);
				template = PropertyManager.getProperty("invocie.mail.template");
				String invocieCreatedAtStr = Utilities.convertDate(invoiceMail.getInvoiceCreatedAt(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_MAIL_FORMAT);
				String invocieDateStr = Utilities.convertDate(invoiceMail.getInvocieDate(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_MAIL_FORMAT);
				template = template.replace("${invoiceNumber}", StringUtils.isEmpty(invoiceMail.getInvoiceNumber())?"":invoiceMail.getInvoiceNumber())
						.replace("${customerName}", StringUtils.isEmpty(invoiceMail.getCustomerName())?"":invoiceMail.getCustomerName())
						.replace("${createdDate}", StringUtils.isEmpty(invocieCreatedAtStr)?"":invocieCreatedAtStr)
						.replace("${companyName}", StringUtils.isEmpty(invoiceMail.getCompanyName())?"":invoiceMail.getCompanyName())
						.replace("${currencySymbol}", StringUtils.isEmpty(currencySymbol)?"":currencySymbol)
						.replace("${amount}", StringUtils.isEmpty(invoiceMail.getAmount()+"")?"":invoiceMail.getAmount()+"")
						.replace("${currencyCode}", StringUtils.isEmpty(invoiceMail.getCurrencyCode())?"":invoiceMail.getCurrencyCode())
						.replace("${invoiceDate}", StringUtils.isEmpty(invocieDateStr)?"":invocieDateStr)
						.replace("${customerEmail}", StringUtils.isEmpty(invoiceMail.getCustomerEmail())?"":invoiceMail.getCustomerEmail())
						.replace("${message}", inputJson.optString("message"));
			}
			System.out.println(template);
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url+ "HalfService/emails/attachment";
//			String url = "https://dev-services.qount.io/HalfService/emails/attachment";
			String authorization = inputJson.optString("Authorization");
			FormDataBodyPart filePart = new FormDataBodyPart(file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			filePart.setContentDisposition(FormDataContentDisposition.name("file").fileName(fileName).build());
			JSONObject emailJson = inputJson.optJSONObject("emailJson");
			if (template != null) {
				emailJson.put("body", template);
			}
			dataMultiPart = new FormDataMultiPart();
			multipartEntity = dataMultiPart.field("emailRequest", emailJson.toString(), MediaType.APPLICATION_JSON_TYPE).bodyPart(filePart);
			Response response = JersyClientUtilities.constructMultipartRequest(url, authorization).post(Entity.entity(multipartEntity, MediaType.MULTIPART_FORM_DATA));
			int responseStatus = response.getStatus();
			String responseString = response.readEntity(String.class);
			if (responseStatus == 200) {
				Response finalResponse = Response.ok(responseString).build();
				String responseStr = finalResponse.getEntity().toString();
				if (responseStr.equalsIgnoreCase("true")) {
					return true;
				}
			}
			dataMultiPart.close();
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
			throw e;
		} finally {
			JersyClientUtilities.closeMultiPart(multipartEntity);
			JersyClientUtilities.closeFormDataMultiPart(dataMultiPart);
		}
		return false;
	}
	
	public static void main(String[] args) {
		try {
			String str = "{\"emailJson\":{\"recipients\":[\"mateen.khan@qount.io\"],\"cc_recipients\":[],\"subject\":\"Your A/P Aging Summary\",\"reportName\":\"A/P Aging Summary\",\"companyName\":\"cathy\",\"userName\":\"Uday Koorella\",\"mailBodyContentType\":\"text/html\"},\"template\":\"asdf\",\"fileName\":\"as.pdf\",\"authorization\":\"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi1hcHAucW91bnQuaW8vIiwidXNlcl9pZCI6InVkYXkua29vcmVsbGFAcW91bnQuaW8iLCJ1c2VybmFtZSI6InVkYXkua29vcmVsbGFAcW91bnQuaW8ifQ.GkrkWOHsK3G2cUBtFAOlb8W1MsJ3EUx7CJUPtIc5XQg\"}";
			JSONObject obj = new JSONObject(str);
			File f = new File("F:/1.pdf");
			System.err.println(sendEmail(f, obj,null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

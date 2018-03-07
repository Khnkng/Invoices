package com.qount.invoice.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceHistory;

/**
 * 
 * @author MateenAhmed 14th Dec 2017
 */
public class InvoiceHistoryHelper {

	private static final Logger LOGGER = Logger.getLogger(InvoiceHistoryHelper.class);

	public static List<InvoiceHistory> getInvoiceHistory(List<String> ids, String userId, String companyId, String action, String description) {
		try {
			LOGGER.debug("entered in getInvoiceHistory(List<String> ids:"+ids+" userId:"+userId+ " companyId:"+companyId + "action:"+action+ "description:"+description);
			List<InvoiceHistory> result  = null;
			if(ids!=null && !ids.isEmpty()){
				result = new ArrayList<InvoiceHistory>();
				Iterator<String> idsItr = ids.iterator();
				while(idsItr.hasNext()){
					String id = idsItr.next();
					Invoice invoice = new Invoice();
					invoice.setId(id);
					invoice.setUser_id(userId);
					invoice.setCompany_id(companyId);
					InvoiceHistory history = getInvoiceHistory(invoice, description, action);
					result.add(history);
				}
				return result;
			}
		} catch (Exception e) {
			LOGGER.error("error in getInvoiceHistory(List<String> ids:"+ids+" userId:"+userId+" companyId:"+companyId + "action:"+action+ "description:"+description, e);
		} finally {
			LOGGER.debug("exited getInvoiceHistory(List<String> ids:"+ids+" userId:"+userId+ " companyId:"+companyId + "action:"+action+ "description:"+description);
		}
		return null;
	}
	
	public static InvoiceHistory getInvoiceHistory(Invoice invoice, String description, String action) {
		try {
			LOGGER.debug("entered in getInvoiceHistory(Invoice invoice:" + invoice + "description:" + description +" action:"+action);
			if (invoice != null) {
				InvoiceHistory invoiceHistory = new InvoiceHistory();
				invoiceHistory.setAction(WordUtils.capitalize(action));
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				invoiceHistory.setAction_at(timestamp.toString());
				invoiceHistory.setCreated_at(timestamp.toString());
				invoiceHistory.setCompany_id(invoice.getCompany_id());
				invoiceHistory.setLast_updated_at(timestamp.toString());
				invoiceHistory.setLast_updated_by(invoice.getUser_id());
				invoiceHistory.setCreated_by(invoice.getUser_id());
				invoiceHistory.setAction_at_mills(new Date().getTime());
				invoiceHistory.setDescription(description);
				
				invoiceHistory.setEmail_from(invoice.getFrom());
				invoiceHistory.setEmail_subject(invoice.getSubject());
				if (invoice.isSendMail()) {
					invoiceHistory.setEmail_to(new JSONArray(invoice.getRecepientsMails()).toString());
				}
				invoiceHistory.setId(UUID.randomUUID().toString());
				invoiceHistory.setInvoice_id(invoice.getId());
				invoiceHistory.setUser_id(invoice.getUser_id());
				invoiceHistory.setAmount(invoice.getAmount());
				invoiceHistory.setAmount_by_date(invoice.getAmount_by_date());
				invoiceHistory.setAmount_due(invoice.getAmount_due());
				invoiceHistory.setAmount_paid(invoice.getAmount_paid());
				invoiceHistory.setTax_amount(invoice.getTax_amount());
				invoiceHistory.setCurrency(invoice.getCurrency());
				invoiceHistory.setSub_totoal(invoice.getSub_total());
				LOGGER.debug("invoiceHistory:" + invoiceHistory);
				return invoiceHistory;
			}
		} catch (Exception e) {
			LOGGER.debug("error in getInvoiceHistory(Invoice invoice:" + invoice + "description:" + description +" action:"+action, e);
		} finally {
			LOGGER.debug("exited from getInvoiceHistory(Invoice invoice:" + invoice + "description:" + description +" action:"+action);
		}
		return null;
	}
}

package com.qount.invoice.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.utils.CommonUtils;

/**
 * 
 * @author MateenAhmed 14th Dec 2017
 */
public class InvoiceHistoryHelper {

	private static final Logger LOGGER = Logger.getLogger(InvoiceHistoryHelper.class);

	public static void updateInvoiceHisotryAction(Invoice invoice, String action) {
		try {
			LOGGER.debug("entered updateInvoiceHisotry(Invoice invoice:" + invoice + " String action:" + action);
			if (invoice != null) {
				InvoiceHistory invoiceHistory = new InvoiceHistory();
				invoiceHistory.setAction(action);
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				invoiceHistory.setAction_at(timestamp.toString());
				invoiceHistory.setCompany_id(invoice.getCompany_id());
				invoiceHistory.setCreated_at(timestamp.toString());
				invoiceHistory.setCreated_by(invoice.getUser_id());
				invoiceHistory.setEmail_from(invoice.getFrom());
				invoiceHistory.setEmail_subject(invoice.getSubject());
				if (invoice.isSendMail()) {
					invoiceHistory.setEmail_to(new JSONArray(invoice.getRecepientsMails()).toString());
				}
				invoiceHistory.setId(UUID.randomUUID().toString());
				invoiceHistory.setInvoice_id(invoice.getId());
				invoiceHistory.setLast_updated_at(timestamp.toString());
				invoiceHistory.setLast_updated_by(invoice.getUser_id());
				invoiceHistory.setUser_id(invoice.getUser_id());
				invoiceHistory.setAmount(invoice.getAmount());
				invoiceHistory.setAmount_by_date(invoice.getAmount_by_date());
				invoiceHistory.setAmount_due(invoice.getAmount_due());
				invoiceHistory.setAmount_paid(invoice.getAmount_paid());
				invoiceHistory.setTax_amount(invoice.getTax_amount());
				invoiceHistory.setCurrency(invoice.getCurrency());
				invoiceHistory.setSub_totoal(invoice.getSub_total());
				invoiceHistory.setAction_at_mills(new Date().getTime());
				List<InvoiceHistory> histories = null;
				if (invoice.getHistories() == null) {
					histories = new ArrayList<InvoiceHistory>();
				} else {
					histories = invoice.getHistories();
				}
				histories.add(invoiceHistory);
				invoice.setHistories(histories);
				invoice.setCreateHistory(true);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited updateInvoiceHisotry(Invoice invoice" + invoice + " String action:" + action);
		}
	}
}

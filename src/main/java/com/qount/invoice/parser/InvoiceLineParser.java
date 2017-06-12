package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;

public class InvoiceLineParser {
	private static final Logger LOGGER = Logger.getLogger(InvoiceLineParser.class);

	public static List<InvoiceLine> getInvoiceLineList(String userID, String invoiceID,
			List<InvoiceLine> invocieLines) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(invoiceID) && invocieLines == null) {
				return null;
			}

			Iterator<InvoiceLine> InvoiceLineItr = invocieLines.iterator();
			while (InvoiceLineItr.hasNext()) {
				InvoiceLine invoiceLineObj = InvoiceLineItr.next();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				if (invoiceLineObj.getId() == null) {
					invoiceLineObj.setId(UUID.randomUUID().toString());
				}
				invoiceLineObj.setInvoice_id(invoiceID);
				invoiceLineObj.setLast_updated_at(timestamp.toString());
				invoiceLineObj.setLast_updated_by(userID);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
		}
		return invocieLines;
	}

	public static InvoiceLine getInvoiceLineObj(String userID, String invoiceID, String lineID,
			InvoiceLine invoiceLine) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(invoiceID) && invoiceLine == null) {
				return null;
			}
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			invoiceLine.setId(lineID);
			invoiceLine.setInvoice_id(invoiceID);
			invoiceLine.setLast_updated_at(timestamp.toString());
			invoiceLine.setLast_updated_by(userID);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
		}
		return invoiceLine;
	}

	public static List<InvoiceLineTaxes> getInvoiceLineTaxesList(List<InvoiceLine> invoiceLinesList) {
		List<InvoiceLineTaxes> result = new ArrayList<InvoiceLineTaxes>();
		Iterator<InvoiceLine> invoiceLineItr = invoiceLinesList.iterator();
		while (invoiceLineItr.hasNext()) {
			InvoiceLine invoiceLine = invoiceLineItr.next();
			List<InvoiceLineTaxes> lineTaxesList = invoiceLine.getInvoiceLineTaxes();
			if (lineTaxesList != null) {
				Iterator<InvoiceLineTaxes> invoiceLineTaxesItr = lineTaxesList.iterator();
				while (invoiceLineTaxesItr.hasNext()) {
					InvoiceLineTaxes invoiceLineTaxes = invoiceLineTaxesItr.next();
					invoiceLineTaxes.setInvoice_line_id(invoiceLine.getId());
					result.add(invoiceLineTaxes);
				}
			}
		}
		return result;
	}

	public static InvoiceLine getInvoiceLineObjToDelete(String invoiceLineID) {
		try {
			if (StringUtils.isEmpty(invoiceLineID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			InvoiceLine invoiceLine = new InvoiceLine();
			invoiceLine.setId(invoiceLineID);
			return invoiceLine;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
	}


}

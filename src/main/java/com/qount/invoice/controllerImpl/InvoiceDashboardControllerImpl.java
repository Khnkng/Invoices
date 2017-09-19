package com.qount.invoice.controllerImpl;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 12 Sept 2016
 *
 */

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;
import com.qount.invoice.utils.SqlQuerys;

public class InvoiceDashboardControllerImpl {

	private static final Logger LOGGER = Logger.getLogger(InvoiceDashboardControllerImpl.class);

	public static List<Invoice> getInvoiceDashboardList(String userID, String companyID, String filter) {
		LOGGER.debug("Entered into InvoiceDashboardControllerImpl.getInvoiceDashboardList with  userID [ " + userID
				+ " ] and companyID [ " + companyID + " ]");
		if (StringUtils.isAnyBlank(companyID, filter)) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.PRECONDITION_FAILED_STR + ":companyID and filter are mandatory",
					Status.PRECONDITION_FAILED));
		}
		List<Invoice> result = null;
		try {
			String query = null;
			if (filter == null || filter.equals("receivables")) {
				query = SqlQuerys.Invoice.RETRIEVE_INVOICES_FOR_DASHBOARD_RECEIVABLES_QRY;
			} else if (filter.equals("past_due")) {
				query = SqlQuerys.Invoice.RETRIEVE_INVOICES_FOR_DASHBOARD_PAST_DUE_QRY;
			} else if (filter.equals("opened")) {
				query = SqlQuerys.Invoice.RETRIEVE_INVOICES_FOR_DASHBOARD_OPENED_QRY;
			} else if (filter.equals("sent")) {
				query = SqlQuerys.Invoice.RETRIEVE_INVOICES_FOR_DASHBOARD_SENT_QRY;
			} else if (filter.equals("recvdin30days")) {
				
			}

			if (StringUtils.isEmpty(query)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR + ":check the query parameter", Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance()
					.retrieveInvoicesByCurrentStateAndCompany(companyID, query);
			InvoiceParser.formatInvoices(invoiceLst);
			// result = InvoiceParser.prepareInvoiceDashboardResponse(invoiceLst);
			return invoiceLst;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("Exited InvoiceDashboardControllerImpl.getInvoiceDashboardList with  userID [ " + userID
					+ " ] and companyID [ " + companyID + " ] result [" + result + " ] ");
		}
	}

}

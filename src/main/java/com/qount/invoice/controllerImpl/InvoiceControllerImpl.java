package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.model.InvoiceTaxes;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 6 Feb 2016
 *
 */
public class InvoiceControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoiceControllerImpl.class);

	public static Invoice createInvoice(String userID, Invoice invoice) {
		Connection connection = null;
		try {
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice);
			if (invoiceObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().save(connection, invoice);
			if (invoiceResult != null) {
				List<InvoiceTaxes> incoiceTaxesList = invoiceObj.getInvoiceTaxes();
				if (incoiceTaxesList == null) {
					incoiceTaxesList = new ArrayList<InvoiceTaxes>();
				}
				List<InvoiceTaxes> invoiceTaxResult = MySQLManager.getInvoiceTaxesDAOInstance().save(connection, invoiceObj.getId(), incoiceTaxesList);
				if (invoiceTaxResult != null) {
					List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
					if (!invoiceLineResult.isEmpty()) {
						List<InvoiceLineTaxes> invoiceLineTaxesList = InvoiceParser.getInvoiceLineTaxesList(invoiceObj.getInvoiceLines());
						List<InvoiceLineTaxes> invoiceLineTaxesResult = MySQLManager.getInvoiceLineTaxesDAOInstance().save(connection, invoiceLineTaxesList);
						if (invoiceLineTaxesResult != null) {
							connection.commit();
							return InvoiceParser.convertTimeStampToString(invoiceObj);
						}
					}
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getStackTrace().toString(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}

	}

	public static Invoice updateInvoice(String userID, String invoiceID, Invoice invoice) {
		Connection connection = null;
		try {
			invoice.setId(invoiceID);
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice);
			if (invoiceObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj);
			if (invoiceResult != null) {
				List<InvoiceTaxes> invoiceTaxesList = invoiceObj.getInvoiceTaxes();
				InvoiceTaxes invoiceTax = new InvoiceTaxes();
				invoiceTax.setInvoice_id(invoiceID);
				InvoiceTaxes deletedInvoiceTaxResult = MySQLManager.getInvoiceTaxesDAOInstance().deleteByInvoiceId(connection, invoiceTax);
				if (deletedInvoiceTaxResult != null) {
					if (invoiceTaxesList == null) {
						invoiceTaxesList = new ArrayList<>();
					}
					List<InvoiceTaxes> invoiceTaxesResult = MySQLManager.getInvoiceTaxesDAOInstance().save(connection, invoiceID, invoiceTaxesList);
					if (invoiceTaxesResult != null) {
						InvoiceLine invoiceLine = new InvoiceLine();
						invoiceLine.setInvoice_id(invoiceID);
						InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().deleteByInvoiceId(connection, invoiceLine);
						if (deletedInvoiceLineResult != null) {
							List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
							if (invoiceLineResult != null) {
								List<InvoiceLineTaxes> invoiceLineTaxesList = InvoiceParser.getInvoiceLineTaxesList(invoiceObj.getInvoiceLines());
								List<InvoiceLineTaxes> invoiceLineTaxesResult = MySQLManager.getInvoiceLineTaxesDAOInstance().save(connection, invoiceLineTaxesList);
								if (invoiceLineTaxesResult != null) {
									connection.commit();
									return invoiceResult;
								}
							}
						}
					}

				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getStackTrace().toString(), Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static List<Invoice> getInvoices(String userID) {
		try {
			if (StringUtils.isEmpty(userID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getStackTrace().toString(), Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static Invoice getInvoice(String userID, String invoiceID) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			Invoice result = MySQLManager.getInvoiceDAOInstance().get(invoiceID);
			if (result != null) {
				InvoiceTaxes invoiceTax = new InvoiceTaxes();
				invoiceTax.setInvoice_id(invoiceID);
				List<InvoiceTaxes> invoiceTaxesList = MySQLManager.getInvoiceTaxesDAOInstance().getByInvoiceID(invoiceTax);
				result.setInvoiceTaxes(invoiceTaxesList);
			}
			return result;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getStackTrace().toString(), Status.INTERNAL_SERVER_ERROR));
		}

	}

	public static Invoice deleteInvoiceById(String userID, String invoiceID) {
		try {
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceDAOInstance().delete(invoice);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getStackTrace().toString(), Status.INTERNAL_SERVER_ERROR));
		}
	}

}

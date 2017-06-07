package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

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

	public static Invoice createInvoice(String userID, String companyID, Invoice invoice) {
		LOGGER.debug("entered createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		Connection connection = null;
		try {
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID);
			if (invoiceObj == null || StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			invoiceObj.setCompany_id(companyID);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			invoice.setState("email sent");
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
							if (sendInvoiceEmail(invoiceResult)) {
								connection.commit();
								return InvoiceParser.convertTimeStampToString(invoiceObj);
							}
						}
					}
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (WebApplicationException e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		}

	}

	public static Invoice updateInvoice(String userID, String companyID, String invoiceID, Invoice invoice) {
		LOGGER.debug("entered updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		Connection connection = null;
		try {
			invoice.setId(invoiceID);
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID);
			if (invoiceObj == null || StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID) || StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			invoiceObj.setCompany_id(companyID);
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
					MySQLManager.getInvoiceTaxesDAOInstance().save(connection, invoiceID, invoiceTaxesList);
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setInvoice_id(invoiceID);
					InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().deleteByInvoiceId(connection, invoiceLine);
					if (deletedInvoiceLineResult != null) {
						List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
						if (invoiceLineResult != null) {
							List<InvoiceLineTaxes> invoiceLineTaxesList = InvoiceParser.getInvoiceLineTaxesList(invoiceObj.getInvoiceLines());
							MySQLManager.getInvoiceLineTaxesDAOInstance().save(connection, invoiceLineTaxesList);
							connection.commit();
							return InvoiceParser.convertTimeStampToString(invoiceResult);
						}
					}

				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	public static Response getInvoices(String userID, String companyID, String state) {
		try {
			LOGGER.debug("entered get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
			if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			Map<String, String> badges = MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			JSONObject result = InvoiceParser.createInvoiceLstResult(invoiceLst, badges);
			return Response.status(200).entity(result.toString()).build();
			// return
			// MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID,
			// companyID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
		}
	}

	public static Invoice getInvoice(String invoiceID) {
		try {
			LOGGER.debug("entered getInvoice invocieId:" + invoiceID);
			if (StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			Invoice result = MySQLManager.getInvoiceDAOInstance().get(invoiceID);
			if (result != null) {
				InvoiceTaxes invoiceTax = new InvoiceTaxes();
				invoiceTax.setInvoice_id(invoiceID);
				List<InvoiceTaxes> invoiceTaxesList = MySQLManager.getInvoiceTaxesDAOInstance().getByInvoiceID(invoiceTax);
				result.setInvoiceTaxes(invoiceTaxesList);
			}
			return InvoiceParser.convertTimeStampToString(result);
			// return result;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited getInvoice invocieId:" + invoiceID);
		}

	}

	public static Invoice deleteInvoiceById(String userID, String invoiceID) {
		try {
			LOGGER.debug("entered deleteInvoiceById userID: " + userID + " invoiceID" + invoiceID);
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			Invoice invoiceObj = MySQLManager.getInvoiceDAOInstance().delete(invoice);
			return InvoiceParser.convertTimeStampToString(invoiceObj);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited deleteInvoiceById userID: " + userID + " invoiceID" + invoiceID);
		}
	}

	private static boolean sendInvoiceEmail(Invoice invoice) {
		try {
			LOGGER.debug("entered sendInvoiceEmail invoice: " + invoice);
			if (invoice == null) {
				return false;
			}
			Response emailRespone = InvoiceReportControllerImpl.createPdfAndSendEmail(invoice);
			if (emailRespone != null && emailRespone.getStatus() == 200) {
				String resultStr = emailRespone.getEntity().toString();
				if (StringUtils.isNotEmpty(resultStr)) {
					if (resultStr.equals("Email sent successfully!")) {
						return true;
					}
				}
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			LOGGER.debug("exited sendInvoiceEmail  invoice: " + invoice);
		}
		return false;
	}
}

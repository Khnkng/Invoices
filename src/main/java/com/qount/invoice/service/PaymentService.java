package com.qount.invoice.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.dao.impl.PaymentDAOImpl;
import com.qount.invoice.helper.InvoiceHistoryHelper;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class PaymentService {

	private static final Logger LOGGER = Logger.getLogger(PaymentService.class);
	private static PaymentService instance = new PaymentService();

	private PaymentService() {

	}

	public static PaymentService getInstance() {
		return instance;
	}

	public Payment createOrUpdatePayment(Payment payment, String companyId, String userID) {
		LOGGER.debug("entered createOrUpdatePayment(Payment payment:" + payment + ", String companyId:" + companyId + ", String userID:" + userID);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		if (connection == null) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
		}
		Payment pymt = null;
		try {
			connection.setAutoCommit(false);
			payment.setCompanyId(companyId);
			if (StringUtils.isBlank(payment.getId())) {
				payment.setId(UUID.randomUUID().toString());
			}
			InvoiceParser.calculateCollectionPaymentStatus(payment);
			pymt = PaymentDAOImpl.getInstance().save(payment, connection, true);
			connection.commit();
			CommonUtils.createJournal(new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(), userID, companyId);
		} catch (SQLException e) {
			// connection.rollback();
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.BAD_REQUEST));

		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createOrUpdatePayment(Payment payment:" + payment + ", String companyId:" + companyId + ", String userID:" + userID);
		}
		return pymt;
	}

	public Payment UpdatePayment(Payment payment, String companyId, String userID,String paymentId) throws Exception {
		LOGGER.debug("entered createOrUpdatePayment(Payment payment:" + payment + ", String companyId:" + companyId + ", String userID:" + userID);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		if (connection == null) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
		}
		Payment pymt = null;
		List<Invoice> updatedInvoiceList = null;
		Set<String> invoiceIds = new HashSet<String>();
		try {
			connection.setAutoCommit(false);
			payment.setCompanyId(companyId);
			InvoiceParser.calculateCollectionPaymentStatus(payment);
			pymt = PaymentDAOImpl.getInstance().update(payment, connection, paymentId);
			List<PaymentLine> paymentLines = payment.getPaymentLines();
			//add new lines and update old one
			PaymentDAOImpl.getInstance().batchaddPaymentLine(connection, paymentLines, payment.getId());
			//delete remaining lines
			PaymentDAOImpl.getInstance().batchdeletePaymentLines(paymentLines, connection);
			
        	Payment dbpayment = PaymentDAOImpl.getInstance().getById(paymentId);
        	List<PaymentLine> dblines = dbpayment.getPaymentLines();
        	for (PaymentLine paymentLine : paymentLines) {
        		invoiceIds.add(paymentLine.getInvoiceId());
			}for (PaymentLine dbline : dblines) {
				invoiceIds.add(dbline.getInvoiceId());
			}
        	List<Invoice>  invoiceList = InvoiceDAOImpl.getInvoiceDAOImpl().getByInQuery(invoiceIds);
        	updatedInvoiceList = PaymentDAOImpl.getInstance().updateInvoiceForPaymentLines(payment, dblines,invoiceList);
    		
            InvoiceDAOImpl.getInvoiceDAOImpl().batchupdate(connection,updatedInvoiceList);
    		InvoiceHistoryHelper.createInvoiceHistory(connection, invoiceList, payment.getReferenceNo());
            connection.commit();
			CommonUtils.createJournal(new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(), userID, companyId);
		} catch (SQLException e) {
			LOGGER.error("",e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.BAD_REQUEST));

		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createOrUpdatePayment(Payment payment:" + payment + ", String companyId:" + companyId + ", String userID:" + userID);
		}
		return pymt;
	}
	
	public List<Payment> getList(String companyId,boolean unapplied) {
		List<Payment> result = null;
		try {
			LOGGER.debug("entered getList(String companyId :" + companyId+ "unapplied:"+unapplied);
			if (unapplied) {
				result = PaymentDAOImpl.getInstance().getUnappliedPayments(companyId);
			}else {
				result = PaymentDAOImpl.getInstance().list(companyId);}
				String paymentIds = InvoiceParser.getCommaSeparatedIds(result);
				Map<String, Double> paidAmountMap = PaymentDAOImpl.getInstance().getPaidAmountMap(paymentIds);
				InvoiceParser.mergePayments(result, paidAmountMap, unapplied, false);
		} catch (Exception e) {
			LOGGER.error("error in getList(String companyId :" + companyId+" unapplied:"+unapplied, e);
		} finally {
			LOGGER.debug("exited getList(String companyId :" + companyId+" unapplied:"+unapplied);
		}
		return result;
	}
	
	public List<Payment> getListByInvoice(String invoiceId, boolean unapplied) {
		List<Payment> result = null;
		try {
			LOGGER.debug("entered getListByInvoice(String invoiceId :" + invoiceId +" unapplied:"+unapplied);
			if(StringUtils.isBlank(invoiceId)){
				return result;
			}
			result = PaymentDAOImpl.getInstance().listByInvoiceId(invoiceId);
			String paymentIds = InvoiceParser.getCommaSeparatedIds(result);
			Map<String, Double> paidAmountMap = PaymentDAOImpl.getInstance().getPaidAmountMap(paymentIds);
			InvoiceParser.mergePayments(result, paidAmountMap, unapplied, true);
		} catch (Exception e) {
			LOGGER.error("error in getListByInvoice(String invoiceId :" + invoiceId +" unapplied:"+unapplied, e);
		} finally {
			LOGGER.debug("exited getListByInvoice(String invoiceId :" + invoiceId +" unapplied:"+unapplied);
		}
		return result;
	}

	public List<Payment> getunmappedPayments(String companyID, String bankAccountID, String entityID, String depositId) {
		try {
			LOGGER.debug("entered getunmappedPayments(String companyID:," + companyID + " String bankAccountID:" + bankAccountID + ", String entityID:" + entityID);
			List<Payment> payments = new ArrayList<Payment>();
			if (entityID == null || entityID.isEmpty()) {
				payments = PaymentDAOImpl.getInstance().getUnmappedPayment(companyID, bankAccountID, depositId);
			} else {
				payments = PaymentDAOImpl.getInstance().getUnmappedPaymentWithEntityId(companyID, bankAccountID, entityID, depositId);
			}
			return payments;
		} catch (Exception e) {
			LOGGER.error("error in getunmappedPayments(String companyID:," + companyID + " String bankAccountID:" + bankAccountID + ", String entityID:" + entityID, e);
		} finally {
			LOGGER.debug("exited getunmappedPayments(String companyID:," + companyID + " String bankAccountID:" + bankAccountID + ", String entityID:" + entityID);
		}
		return null;
	}

	public Payment getById(String companyId, String paymentId) {
		try {
			LOGGER.debug("entered getById(String companyId:" + companyId + ", String paymentId:" + paymentId);
			return PaymentDAOImpl.getInstance().getById(paymentId);
		} catch (Exception e) {
			LOGGER.debug("error in getById(String companyId:" + companyId + ", String paymentId:" + paymentId, e);
		} finally {
			LOGGER.debug("exited getById(String companyId:" + companyId + ", String paymentId:" + paymentId);
		}
		return null;
	}
	
	public Payment getByPaymentId(String companyId, String paymentId) {
		Payment payment = null;
		List<PaymentLine> paymentLines = null;
		try {
			LOGGER.debug("entered getById(String companyId:" + companyId + ", String paymentId:" + paymentId);
			
			payment = PaymentDAOImpl.getInstance().getById(paymentId);
			if (!payment.getPayment_status().equalsIgnoreCase("Applied")) {
				paymentLines = PaymentDAOImpl.getInstance().getunmappedLinesOfcustomer(payment.getReceivedFrom(), payment);
				payment.setPaymentLines(paymentLines);
			}
		} catch (Exception e) {
			LOGGER.debug("error in getById(String companyId:" + companyId + ", String paymentId:" + paymentId, e);
		} finally {
			LOGGER.debug("exited getById(String companyId:" + companyId + ", String paymentId:" + paymentId);
		}
		System.out.println(payment);
		return payment;
	}

	public List<PaymentLine> getLinesByCustomerIdOrPaymentId(String companyId,String customerID, String paymentId) {
		LOGGER.debug("entered getById(String companyId:" + companyId + ", String paymentId:" + paymentId);
		Payment payment = null;
		List<PaymentLine> paymentLines = null;
		Connection connection = null;
		try {
			connection= DatabaseUtilities.getReadWriteConnection();
			if (connection!=null) {
				if (StringUtils.isBlank(paymentId)) {
					paymentLines = PaymentDAOImpl.getInstance().getunmappedLinesOfcustomer(customerID, null);
				}else{
				payment = PaymentDAOImpl.getInstance().getById(paymentId);
				if (!payment.getPayment_status().equalsIgnoreCase("Applied")) {
					paymentLines = PaymentDAOImpl.getInstance().getunmappedLinesOfcustomer(payment.getReceivedFrom(), payment);
				}
				}
			}
		} catch (Exception e) {
			LOGGER.debug("error in getById(String companyId:" + companyId + ", String paymentId:" + paymentId, e);
		} finally {
			LOGGER.debug("exited getById(String companyId:" + companyId + ", String paymentId:" + paymentId);
		}
		return paymentLines;
	}
	
	public List<Invoice> getIvoicesByPaymentID(String companyId, String paymentId) {
		try {
			LOGGER.debug("entered getIvoicesByPaymentID(String companyId:" + companyId + ", String paymentId:" + paymentId);
			return PaymentDAOImpl.getInstance().getIvoicesByPaymentID(paymentId);
		} catch (Exception e) {
			LOGGER.debug("error in getIvoicesByPaymentID(String companyId:" + companyId + ", String paymentId:" + paymentId, e);
		} finally {
			LOGGER.debug("exited getIvoicesByPaymentID(String companyId:" + companyId + ", String paymentId:" + paymentId);
		}
		return null;
	}
}

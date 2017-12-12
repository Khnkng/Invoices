package com.qount.invoice.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.database.dao.impl.PaymentDAOImpl;
import com.qount.invoice.model.Payment;
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

	public List<Payment> getList(String companyId) {
		List<Payment> result = null;
		try {
			LOGGER.debug("entered getList(String companyId :" + companyId);
			result = PaymentDAOImpl.getInstance().list(companyId);
		} catch (Exception e) {
			LOGGER.error("error in getList(String companyId :" + companyId, e);
		} finally {
			LOGGER.debug("exited getList(String companyId :" + companyId);
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
}

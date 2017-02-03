package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.parser.ProposalLineParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
public class ProposalLineControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(ProposalLineControllerImpl.class);

	public static List<ProposalLine> createProposalLine(String userID, String proposalID,
			List<ProposalLine> proposalLines) {
		Connection connection = null;
		try {
			List<ProposalLine> proposalLineObjLst = ProposalLineParser.getProposalLineList(userID, proposalID,
					proposalLines);
			if (proposalLineObjLst == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance().batchSave(connection,
					proposalLineObjLst);
			if (proposalLineResult != null) {
				List<ProposalLineTaxes> proposalTaxesList = ProposalLineParser.getProposalLineTaxesList(proposalLineResult);
				List<ProposalLineTaxes> proposaLineTaxResult = MySQLManager.getProposalLineTaxesDAOInstance()
						.batchSave(connection, proposalTaxesList);
				if (!proposaLineTaxResult.isEmpty()) {
					connection.commit();
					return proposalLineObjLst;
				}
			}

			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static ProposalLine updateProposalLine(String userID, String proposalID, String proposalLineId,
			ProposalLine proposalLine) {
		Connection connection = null;
		try {
			ProposalLine proposalLineObj = ProposalLineParser.getProposalLineObj(userID, proposalID, proposalLineId,
					proposalLine);
			if (proposalLineObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			ProposalLine proposalLineResult = MySQLManager.getProposalLineDAOInstance().update(connection,
					proposalLineObj);
			if (proposalLineResult != null) {
				List<ProposalLineTaxes> proposalLineTaxesResult = MySQLManager.getProposalLineTaxesDAOInstance()
						.batchDeleteAndSave(connection, proposalID, proposalLineId,
								proposalLineResult.getProposalLineTaxes());
				if (proposalLineTaxesResult != null) {
					connection.commit();
					return proposalLineObj;
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}

	}
}
package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.impl.ProposalDAOImpl;
import com.qount.invoice.database.dao.impl.ProposalLineDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.parser.ProposalParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class ProposalControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(ProposalControllerImpl.class);

	public static Proposal createProposal(String userId, Proposal proposal) {
		Connection connection = null;
		try {
			Proposal proposalObj = ProposalParser.getProposalObj(userId, proposal);
			if (proposalObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			if (MySQLManager.getProposalDAOInstance().save(connection, proposalObj)) {
				if (MySQLManager.getProposalLineDAOInstance().batchSave(connection, proposalObj.getProposalLines())) {
					connection.commit();
					return proposalObj;
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

	public static Proposal updateProposal(String userID, String proposalId, Proposal proposal) {
		Connection connection = null;
		try {
			Proposal proposalObj = ProposalParser.getProposalObj(userID, proposal);
			if (proposalObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			if (MySQLManager.getProposalDAOInstance().deleteAndCreateProposal(connection, proposalId, proposalObj)) {
				if (MySQLManager.getProposalLineDAOInstance().batchSave(connection, proposalObj.getProposalLines())) {
					connection.commit();
					return proposalObj;
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

	public static List<Proposal> getProposals(String userId) {
		try {
			if (StringUtils.isEmpty(userId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getProposalDAOInstance().getProposalList(userId);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static Proposal getProposal(String userId, String proposalId) {
		try {
			if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(proposalId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return ProposalDAOImpl.getProposalDAOImpl().get(proposalId, userId);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}

	}

	public static Proposal deleteProposalById(String userId, String proposalId) {
		try {
			Proposal proposal = ProposalParser.getProposalObjToDelete(userId, proposalId);
			if (proposal == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return ProposalDAOImpl.getProposalDAOImpl().delete(proposal);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static ProposalLine deleteProposalLine(String userId, String proposalId, String proposalLineId) {
		try {
			ProposalLine proposalLine = ProposalParser.getProposalLineObjToDeleteProposalLine(proposalId,
					proposalLineId);
			if (proposalLine == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return ProposalLineDAOImpl.getProposalLineDAOImpl().deleteProposalLine(proposalLine);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}
	}
}

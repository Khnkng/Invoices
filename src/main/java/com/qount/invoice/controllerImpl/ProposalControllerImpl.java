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

	public static Proposal createProposal(String user_id, Proposal proposal) {
		Connection connection = null;
		try {
			Proposal proposalObj = ProposalParser.getProposalObj(user_id,proposal);
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

	public static List<Proposal> getProposals(String user_id) {
		Connection connection = null;
		try {
			if (StringUtils.isEmpty(user_id)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			return MySQLManager.getProposalDAOInstance().getProposalList(connection, user_id);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static Proposal updateProposal(String user_id, String proposal_id, Proposal proposal) {
		Connection connection = null;
		try {
			Proposal proposalObj = ProposalParser.getProposalObjToUpdate(user_id, proposal_id, proposal);
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
			List<ProposalLine> oldLines = MySQLManager.getProposalLineDAOInstance().getLines(connection, proposal_id);
			List<ProposalLine> newLines = proposalObj.getProposalLines();
			// lines to be deleted
			oldLines.removeAll(newLines);

			if (MySQLManager.getProposalDAOInstance().update(connection, proposalObj)) {
				if (MySQLManager.getProposalLineDAOInstance().batchSaveAndDelete(connection, newLines, oldLines)) {
					connection.commit();
					return proposal;
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

	public static Proposal getProposal(String user_id, String proposal_id) {
		try {
			if (StringUtils.isEmpty(user_id) && StringUtils.isEmpty(proposal_id)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return ProposalDAOImpl.getProposalDAOImpl().get(proposal_id, user_id);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}

	}

	public static Proposal deleteProposalById(String user_id, String proposal_id) {
		try {
			Proposal proposal = ProposalParser.getProposalObjToDelete(user_id, proposal_id);
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

	public static ProposalLine deleteProposalLine(String user_id, String proposal_id, String proposalLine_id) {
		try {
			ProposalLine proposalLine = ProposalParser.getProposalLineObjToDeleteProposalLine(proposal_id,
					proposalLine_id);
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

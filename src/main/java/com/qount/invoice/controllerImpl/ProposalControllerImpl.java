package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.parser.ProposalParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;
import com.qount.invoice.utils.Utilities;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public class ProposalControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(ProposalControllerImpl.class);

	public static Proposal createProposal(String userID, String companyID, Proposal proposal) {
		LOGGER.debug("entered createProposal(String userID:" + userID + ",companyID:" + companyID + " Proposal proposal)" + proposal);
		Connection connection = null;
		try {
			if (proposal == null || StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR + ":userID and companyID are mandatory", Status.PRECONDITION_FAILED));
			}
			Proposal proposalObj = ProposalParser.getProposalObj(userID, proposal, companyID);
			if (StringUtils.isEmpty(proposal.getState())) {
				proposal.setState(Constants.INVOICE_STATE_DRAFT);
			}
			if (proposal.isSendMail()) {
				if (sendProposalEmail(proposalObj)) {
					proposal.setState(Constants.INVOICE_STATE_SENT);
				}
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Proposal proposalResult = MySQLManager.getProposalDAOInstance().save(connection, proposalObj);
			if (proposalResult != null) {
				List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance().save(connection, proposalObj.getProposalLines());
				if (!proposalLineResult.isEmpty()) {
					connection.commit();
				}
				return ProposalParser.convertTimeStampToString(proposalObj);
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (WebApplicationException e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createProposal(String userID:" + userID + ",companyID:" + companyID + " Proposal proposal)" + proposal);
		}

	}

	public static Proposal updateProposal(String userID, String companyID, String proposalID, Proposal proposal) {
		LOGGER.debug("entered updateProposal userid:" + userID + " companyID:" + companyID + " proposalID:" + proposalID + ": proposal" + proposal);
		Connection connection = null;
		try {
			proposal.setId(proposalID);
			Proposal proposalObj = ProposalParser.getProposalObj(userID, proposal, companyID);
			if (proposalObj == null || StringUtils.isAnyBlank(userID, companyID, proposalID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			if (proposal.isSendMail()) {
				if (sendProposalEmail(proposalObj)) {
					proposal.setState(Constants.INVOICE_STATE_SENT);
				}
				if (StringUtils.isEmpty(proposal.getState())) {
					proposal.setState(Constants.INVOICE_STATE_DRAFT);
				}
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Proposal proposalResult = MySQLManager.getProposalDAOInstance().update(connection, proposalObj);
			if (proposalResult != null) {
				ProposalLine proposalLine = new ProposalLine();
				proposalLine.setProposal_id(proposalID);
				ProposalLine deletedProposalLineResult = MySQLManager.getProposalLineDAOInstance().deleteByProposalId(connection, proposalLine);
				if (deletedProposalLineResult != null) {
					List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance().save(connection, proposalObj.getProposalLines());
					if (proposalLineResult != null) {
						connection.commit();
						return ProposalParser.convertTimeStampToString(proposalResult);
					}
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateProposal userid:" + userID + " companyID:" + companyID + " proposalID:" + proposalID + ": proposal" + proposal);
		}
	}

	public static Proposal updateProposalState(String proposalID, Proposal proposal) {
		LOGGER.debug("entered updateProposalState proposalID:" + proposalID + ": proposal" + proposal);
		Connection connection = null;
		try {
			if (proposal == null || StringUtils.isAnyEmpty(proposalID, proposal.getState())) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			proposal.setId(proposalID);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			switch (proposal.getState()) {
			case "accept":
				return acceptProposal(connection, proposal);
			case "deny":
				return denyProposal(connection, proposal);
			case "convertToInvoice":
				return convertToInvoice(connection, proposal);
			default:
				break;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateProposalState proposalID:" + proposalID + ": proposal" + proposal);
		}
	}

	private static Proposal acceptProposal(Connection connection, Proposal proposal) throws Exception {
		// TBD
		return null;
	}

	private static Proposal denyProposal(Connection connection, Proposal proposal) throws Exception {
		// TBD
		return null;
	}

	private static Proposal convertToInvoice(Connection connection, Proposal proposal) throws Exception {
		// TBD
		return null;
	}

	public static Response getProposals(String userID, String companyID, String state) {
		try {
			LOGGER.debug("entered get proposals userID:" + userID + " companyID:" + companyID + " state:" + state);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			List<Proposal> proposalLst = MySQLManager.getProposalDAOInstance().getProposalList(userID, companyID, state);
			JSONObject result = ProposalParser.createProposalLstResult(proposalLst, null);
			return Response.status(200).entity(result.toString()).build();
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get proposals userID:" + userID + " companyID:" + companyID + " state:" + state);
		}
	}

	public static Proposal getProposal(String proposalID) {
		try {
			LOGGER.debug("entered getProposal invocieId:" + proposalID);
			if (StringUtils.isEmpty(proposalID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Proposal result = ProposalParser.convertTimeStampToString(MySQLManager.getProposalDAOInstance().get(proposalID));
			LOGGER.debug("getProposal result:" + result);
			return result;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited getProposal invocieId:" + proposalID);
		}

	}

	public static Proposal deleteProposalById(String userID, String companyID, String proposalID) {
		try {
			LOGGER.debug("entered deleteProposalById userID: " + userID + " companyID: " + companyID + " proposalID" + proposalID);
			Proposal proposal = ProposalParser.getProposalObjToDelete(userID, companyID, proposalID);
			if (proposal == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Proposal proposalObj = MySQLManager.getProposalDAOInstance().delete(proposal);
			return ProposalParser.convertTimeStampToString(proposalObj);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited deleteProposalById userID: " + userID + " companyID: " + companyID + " proposalID" + proposalID);
		}
	}

	public static boolean deleteProposalsById(String userID, String companyID, List<String> ids) {
		try {
			LOGGER.debug("entered deleteProposalsById userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			if (StringUtils.isAnyBlank(userID, companyID) || ids == null || ids.isEmpty()) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(ids);
			return MySQLManager.getProposalDAOInstance().deleteLst(userID, companyID, commaSeparatedLst);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited deleteProposalsById userID: " + userID + " companyID:" + companyID + " ids:" + ids);
		}
	}

	private static boolean sendProposalEmail(Proposal proposal) throws Exception {
		try {
			LOGGER.debug("entered sendProposalEmail proposal: " + proposal);
			JSONObject emailJson = new JSONObject();
			emailJson.put("recipients", proposal.getRecepientsMailsArr());
			emailJson.put("subject", PropertyManager.getProperty("proposal.subject"));
			emailJson.put("mailBodyContentType", PropertyManager.getProperty("mail.body.content.type"));
			String template = PropertyManager.getProperty("invocie.mail.template");
			String proposalLinkUrl = PropertyManager.getProperty("proposal.payment.link") + proposal.getId();
			template = template.replace("${proposalNumber}", StringUtils.isBlank(proposal.getNumber()) ? "" : proposal.getNumber())
					.replace("${companyName}", StringUtils.isEmpty(proposal.getCompanyName()) ? "" : proposal.getCompanyName())
					.replace("${currencySymbol}",
							StringUtils.isEmpty(Utilities.getCurrencyHtmlSymbol(proposal.getCurrency())) ? "" : Utilities.getCurrencyHtmlSymbol(proposal.getCurrency()))
					.replace("${amount}", StringUtils.isEmpty(proposal.getAmount() + "") ? "" : proposal.getAmount() + "")
					.replace("${currencyCode}", StringUtils.isEmpty(proposal.getCurrency()) ? "" : proposal.getCurrency())
					.replace("${proposalDate}", StringUtils.isEmpty(proposal.getDue_date()) ? "" : proposal.getDue_date()).replace("${proposalLinkUrl}", proposalLinkUrl);
			emailJson.put("body", template);
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url + "HalfService/emails";
			// String url = "https://dev-services.qount.io/HalfService/emails";
			Object result = HTTPClient.postObject(url, emailJson.toString());
			if (result != null && result instanceof java.lang.String && result.equals("true")) {
				return true;
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			LOGGER.debug("exited sendProposalEmail  proposal: " + proposal);
		}
		return false;
	}

	public static boolean updateProposalState1(String userID, String companyID, String state,
			List<String> proposalIdList) {
		LOGGER.debug("request for ProposalControllerImpl.updateProposalState1 userID [" + userID + "] companyID ["
				+ companyID + " ] proposalIdList [" + proposalIdList + " ] state [" + state + " ]");
		if (StringUtils.isAnyBlank(state)) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.PRECONDITION_FAILED_STR + ":state is mandatory", Status.PRECONDITION_FAILED));
		}
		try {
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(proposalIdList);
			switch (state) {
			case "accept":
				return acceptProposal(userID, companyID, state, proposalIdList);
			case "deny":
				return denyProposal(userID, companyID, state, commaSeparatedLst);
			case "convertToInvoice":
				return delete(userID, companyID, state, commaSeparatedLst);
			default:
				break;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));

		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		}
	}

	private static boolean acceptProposal(String userID, String companyID, String state, List<String> proposalIdList) {
		if (StringUtils.isAnyBlank(userID, companyID) || proposalIdList == null || proposalIdList.isEmpty()) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.PRECONDITION_FAILED_STR + ":userID and companyID and proposalIdList are mandatory",
					Status.PRECONDITION_FAILED));
		}
		Connection connection = null;
		try {
			List<Proposal> proposalList = new ArrayList<Proposal>();
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
			for (int i = 0; i < proposalIdList.size(); i++) {
				String proposalId = proposalIdList.get(i);
				Proposal proposal = MySQLManager.getProposalDAOInstance().get(proposalId);
				if (null != proposal) {
					Invoice invoice = new Invoice();
					BeanUtils.copyProperties(invoice, proposal);
					String invoiceId = UUID.randomUUID().toString();
					invoice.setId(invoiceId);
					proposal.setInvoice_id(invoiceId);
					invoice.setProposal_id(proposal.getId());
					// proposalList.add(proposal);
					// invoiceList.add(invoice);

					List<ProposalLine> proposalLineList = proposal.getProposalLines();
					for (int j = 0; j < proposalLineList.size(); j++) {
						InvoiceLine invoiceLine = new InvoiceLine();
						BeanUtils.copyProperties(invoiceLine, proposalLineList.get(j));
						String invoiceLineId = UUID.randomUUID().toString();
						invoiceLine.setId(invoiceLineId);
						invoiceLineList.add(invoiceLine);
					}
					invoice.setInvoiceLines(invoiceLineList);
					proposalList.add(proposal);
					invoiceList.add(invoice);
				}
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			List<Proposal> resultProposalList = MySQLManager.getProposalDAOInstance().updateProposal(connection,
					proposalList);
			if (resultProposalList.isEmpty()) {
				List<Invoice> resultInvoiceList = MySQLManager.getInvoiceDAOInstance().saveInvoice(connection,
						invoiceList);
				if (resultInvoiceList.isEmpty()) {
					List<InvoiceLine> resultInvoiceLineList = MySQLManager.getInvoiceLineDAOInstance().save(connection,
							invoiceLineList);
					if (resultInvoiceLineList.isEmpty()) {
						connection.commit();
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited acceptProposal(String userID:" + userID + ",companyID:" + companyID + ", state:"
					+ state + ", proposalIdList:" + proposalIdList);
		}
		return false;
	}

	private static boolean denyProposal(String userID, String companyID, String state, String proposalIdList) {
		if (StringUtils.isAnyBlank(userID, companyID, proposalIdList)) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.PRECONDITION_FAILED_STR + ":userID and companyID and proposalIdList are mandatory",
					Status.PRECONDITION_FAILED));
		}
		try {
			return MySQLManager.getProposalDAOInstance().denyProposal(userID, companyID, proposalIdList);

		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		}
	}

	private static boolean delete(String userID, String companyID, String state, String proposalIdList) {
		if (StringUtils.isAnyBlank(userID, companyID, proposalIdList)) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.PRECONDITION_FAILED_STR + ":userID and companyID and proposalIdList are mandatory",
					Status.PRECONDITION_FAILED));
		}
		try {
			return MySQLManager.getProposalDAOInstance().deleteLst(userID, companyID, proposalIdList);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited deleteProposalsById userID: " + userID + " companyID:" + companyID + " proposalIdList:"
					+ proposalIdList);
		}
	}
}

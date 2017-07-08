package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.qount.invoice.database.dao.impl.ProposalDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.parser.ProposalParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public class ProposalControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(ProposalControllerImpl.class);

	public static Proposal createProposal(String userId, String companyId, Proposal proposal) {
		Connection connection = null;
		LOGGER.debug("entered create Proposal() userID:" + userId + " companyId:" + companyId + " proposal:" + proposal);
		try {
			Proposal proposalObj = ProposalParser.getProposalObj(userId, proposal, companyId);
			if (proposalObj == null || StringUtils.isEmpty(userId) || StringUtils.isEmpty(companyId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			proposalObj.setCompany_id(companyId);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Proposal proposalResult = MySQLManager.getProposalDAOInstance().save(connection, proposalObj);
			if (proposalResult != null) {
				List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance().batchSave(connection, proposalObj.getProposalLines());
				if (proposalLineResult != null) {
					connection.commit();
					return proposalObj;
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited create Proposal() userID:" + userId + " companyId:" + companyId + " proposal:" + proposal);
		}
	}

	public static Proposal updateProposal(String userID, String companyId, String proposalId, Proposal proposal) {
		Connection connection = null;
		LOGGER.debug("entered update Proposal() userID:" + userID + " companyId:" + companyId + " proposal:" + proposal);
		try {
			proposal.setId(proposalId);
			Proposal proposalObj = ProposalParser.getProposalObj(userID, proposal, companyId);
			if (proposalObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			proposalObj.setCompany_id(companyId);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Proposal proposalResult = MySQLManager.getProposalDAOInstance().updateProposal(connection, proposalObj);
			if (proposalResult != null) {
				ProposalLine proposalLine = new ProposalLine();
				proposalLine.setProposal_id(proposalId);
				ProposalLine deletedProposalLineResult = MySQLManager.getProposalLineDAOInstance().delete(connection, proposalLine);
				if (deletedProposalLineResult != null) {
					List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance().batchSave(connection, proposalObj.getProposalLines());
					if (proposalLineResult != null) {
						Invoice invoiceObjToInsert = new Invoice();
						if (proposalResult.getState().equals("accept")) {
							List<InvoiceLine> invoiceLinesList = new ArrayList<>();
							BeanUtils.copyProperties(invoiceObjToInsert, proposalObj);
							List<ProposalLine> proposalLinesList = proposalObj.getProposalLines();
							Iterator<ProposalLine> ProposalLinesListItr = proposalLinesList.iterator();
							while (ProposalLinesListItr.hasNext()) {
								ProposalLine ProposalLine = ProposalLinesListItr.next();
								InvoiceLine invoiceLine = new InvoiceLine();
								BeanUtils.copyProperties(invoiceLine, ProposalLine);
								invoiceLine.setId(ProposalLine.getId());
								invoiceLine.setInvoice_id(ProposalLine.getProposal_id());
								invoiceLinesList.add(invoiceLine);
							}
							invoiceObjToInsert.setInvoiceLines(invoiceLinesList);
							invoiceObjToInsert.setInvoice_date(new Date().toString());
							invoiceObjToInsert.setId(proposalObj.getId());
							Invoice checkIfRecordPresent = MySQLManager.getInvoiceDAOInstance().get(proposalId);
							if (checkIfRecordPresent.getId() == null) {
								Invoice invoiceCreated = InvoiceControllerImpl.createInvoice(userID, proposal.getCompany_id(), invoiceObjToInsert);
								if (invoiceCreated == null) {
									throw new WebApplicationException(
											ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PARTIAL_SUCCESS, Status.INTERNAL_SERVER_ERROR));
								}
							}
						}
						connection.commit();
						return proposalObj;
					}
				}

			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited update Proposal() userID:" + userID + " companyId:" + companyId + " proposal:" + proposal);
		}

	}

	public static List<Proposal> getProposals(String userId, String comapnyId) {
		try {
			LOGGER.debug("entered get Proposals() userID:" + userId + " companyId:" + comapnyId);
			if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(comapnyId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getProposalDAOInstance().getProposalList(userId, comapnyId);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get Proposal() userID:" + userId + " companyId:" + comapnyId);
		}
	}

	public static Proposal getProposal(String proposalId) {
		try {
			LOGGER.debug("entered get Proposal() proposalId:" + proposalId);
			if (StringUtils.isEmpty(proposalId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Proposal result = ProposalDAOImpl.getProposalDAOImpl().get(proposalId);
			return result;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get Proposal() proposalId:" + proposalId);
		}

	}

	public static Proposal deleteProposalById(String proposalId) {
		try {
			LOGGER.debug("entered delete Proposal() proposalId:" + proposalId);
			Proposal proposal = ProposalParser.getProposalObjToDelete(proposalId);
			if (proposal == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			return ProposalDAOImpl.getProposalDAOImpl().delete(proposal);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited delete Proposal() proposalId:" + proposalId);
		}
	}

	// private static File createPdf() {
	// File file = new File("F:/1.pdf");
	// Document document = new Document();
	// try {
	// PdfWriter.getInstance(document, new FileOutputStream(file));
	// int pageWidth = (12 + 1) * 150;
	// Rectangle two = new Rectangle(pageWidth, 600);
	// document.setPageSize(two);
	// document.open();
	// Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 24,
	// Font.BOLD);
	// Paragraph headerParagrah = new Paragraph("MATEEN", headerFont);
	// Chapter chapter = new Chapter(headerParagrah, 1);
	// headerFont.setColor(BaseColor.BLUE);
	// headerParagrah.setAlignment(Element.ALIGN_CENTER);
	// chapter.setNumberDepth(0);
	// document.add(chapter);
	// System.out.println("done");
	// return file;
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// document.close();
	// }
	// return null;
	// }

	public static SyncInvoker constructMultipartRequest(String url, String header) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		return client.target(url).request().header("Authorization", header);
	}
}

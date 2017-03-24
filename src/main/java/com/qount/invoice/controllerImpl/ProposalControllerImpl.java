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
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.model.InvoiceTaxes;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.model.ProposalTaxes;
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
			Proposal proposalResult = MySQLManager.getProposalDAOInstance().save(connection, proposalObj);
			if (proposalResult != null) {
				List<ProposalTaxes> proposalTaxesList = proposalObj.getProposalTaxes();
				List<ProposalTaxes> proposalTaxResult = MySQLManager.getProposalTaxesDAOInstance()
						.saveProposalTaxes(connection, proposalObj.getId(), proposalTaxesList);
				if (proposalTaxResult != null) {
					List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance()
							.batchSave(connection, proposalObj.getProposalLines());
					if (proposalLineResult != null) {
						List<ProposalLineTaxes> proposalLineTaxesList = ProposalParser
								.getProposalLineTaxesList(proposalObj.getProposalLines());
						List<ProposalLineTaxes> proposalLineTaxesResult = MySQLManager.getProposalLineTaxesDAOInstance()
								.batchSave(connection, proposalLineTaxesList);
						if (proposalLineTaxesResult != null) {
							connection.commit();
							return proposalObj;
						}
					}
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
			proposal.setId(proposalId);
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
			Proposal proposalResult = MySQLManager.getProposalDAOInstance().updateProposal(connection, proposalObj);
			if (proposalResult != null) {
				List<ProposalTaxes> proposalTaxesList = proposalObj.getProposalTaxes();
				ProposalTaxes proposalTaxes = new ProposalTaxes();
				proposalTaxes.setProposal_id(proposalId);
				ProposalTaxes deletedProposalTaxResult = MySQLManager.getProposalTaxesDAOInstance()
						.deleteProposalTax(connection, proposalTaxes);
				if (deletedProposalTaxResult != null) {
					List<ProposalTaxes> proposalTaxesResult = MySQLManager.getProposalTaxesDAOInstance()
							.saveProposalTaxes(connection, proposalId, proposalTaxesList);
					if (proposalTaxesResult != null) {
						ProposalLine proposalLine = new ProposalLine();
						proposalLine.setProposal_id(proposalId);
						ProposalLine deletedProposalLineResult = MySQLManager.getProposalLineDAOInstance()
								.delete(connection, proposalLine);
						if (deletedProposalLineResult != null) {
							List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance()
									.batchSave(connection, proposalObj.getProposalLines());
							if (proposalLineResult != null) {
								List<ProposalLineTaxes> proposalLineTaxesList = ProposalParser
										.getProposalLineTaxesList(proposalObj.getProposalLines());
								List<ProposalLineTaxes> proposalLineTaxesResult = MySQLManager
										.getProposalLineTaxesDAOInstance().batchSave(connection, proposalLineTaxesList);
								if (proposalLineTaxesResult != null) {
									if (proposalResult.getState().equals("accept")) {
										Invoice invoice = new Invoice();
										List<InvoiceTaxes> invoiceTaxesList = new ArrayList<>();
										List<InvoiceLine> invoiceLinesList = new ArrayList<>();
										List<InvoiceLineTaxes> invoiceLineTaxesList = null;

										BeanUtils.copyProperties(invoice, proposalObj);

										List<ProposalTaxes> ProposalTaxesList = proposalObj.getProposalTaxes();
										Iterator<ProposalTaxes> ProposalTaxesListItr = ProposalTaxesList.iterator();
										while (ProposalTaxesListItr.hasNext()) {
											ProposalTaxes proposalTaxes2 = ProposalTaxesListItr.next();
											InvoiceTaxes invoiceTaxes = new InvoiceTaxes();
											BeanUtils.copyProperties(invoiceTaxes, proposalTaxes2);
											invoiceTaxes.setInvoice_id(proposalTaxes2.getProposal_id());
											invoiceTaxesList.add(invoiceTaxes);
										}
										invoice.setInvoiceTaxes(invoiceTaxesList);

										List<ProposalLine> proposalLinesList = proposalObj.getProposalLines();
										Iterator<ProposalLine> ProposalLinesListItr = proposalLinesList.iterator();
										while (ProposalLinesListItr.hasNext()) {
											ProposalLine ProposalLine = ProposalLinesListItr.next();
											InvoiceLine invoiceLine = new InvoiceLine();
											BeanUtils.copyProperties(invoiceLine, ProposalLine);
											invoiceLine.setId(ProposalLine.getId());
											invoiceLine.setInvoice_id(ProposalLine.getProposal_id());

											List<ProposalLineTaxes> proposalLineTaxesList2 = ProposalLine
													.getProposalLineTaxes();
											Iterator<ProposalLineTaxes> ProposalLinesTaxesListItr = proposalLineTaxesList2
													.iterator();
											invoiceLineTaxesList = new ArrayList<>();
											while (ProposalLinesTaxesListItr.hasNext()) {
												ProposalLineTaxes ProposalLineTax = ProposalLinesTaxesListItr.next();
												InvoiceLineTaxes invoiceLineTaxes = new InvoiceLineTaxes();
												BeanUtils.copyProperties(invoiceLineTaxes, ProposalLineTax);
												invoiceLineTaxes
														.setInvoice_line_id(ProposalLineTax.getProposal_line_id());
												invoiceLineTaxesList.add(invoiceLineTaxes);
											}
											invoiceLine.setInvoiceLineTaxes(invoiceLineTaxesList);
											invoiceLinesList.add(invoiceLine);
										}
										invoice.setInvoiceLines(invoiceLinesList);
										invoice.setInvoice_date(new Date().toString());
										invoice.setId(proposalObj.getId());

									}
									connection.commit();
									return proposalObj;
								}
							}
						}
					}

				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}

	}

	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
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
			Proposal result = ProposalDAOImpl.getProposalDAOImpl().get(proposalId);
			if (result != null) {
				List<ProposalTaxes> proposalTaxesList = MySQLManager.getProposalTaxesDAOInstance()
						.getByProposalID(proposalId);
				result.setProposalTaxes(proposalTaxesList);
			}
			return result;
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

//	private static File createPdf() {
//		File file = new File("F:/1.pdf");
//		Document document = new Document();
//		try {
//			PdfWriter.getInstance(document, new FileOutputStream(file));
//			int pageWidth = (12 + 1) * 150;
//			Rectangle two = new Rectangle(pageWidth, 600);
//			document.setPageSize(two);
//			document.open();
//			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
//			Paragraph headerParagrah = new Paragraph("MATEEN", headerFont);
//			Chapter chapter = new Chapter(headerParagrah, 1);
//			headerFont.setColor(BaseColor.BLUE);
//			headerParagrah.setAlignment(Element.ALIGN_CENTER);
//			chapter.setNumberDepth(0);
//			document.add(chapter);
//			System.out.println("done");
//			return file;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			document.close();
//		}
//		return null;
//	}

	public static SyncInvoker constructMultipartRequest(String url, String header) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		return client.target(url).request().header("Authorization", header);
	}
}

package com.qount.invoice.controllerImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.JSONObject;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.qount.invoice.database.dao.impl.ProposalDAOImpl;
import com.qount.invoice.database.dao.impl.ProposalLineDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
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
				if (!proposalTaxResult.isEmpty()) {
					List<ProposalLine> proposalLineResult = MySQLManager.getProposalLineDAOInstance()
							.batchSave(connection, proposalObj.getProposalLines());
					if (!proposalLineResult.isEmpty()) {
						List<ProposalLineTaxes> proposalLineTaxesList = ProposalParser
								.getProposalLineTaxesList(proposalObj.getProposalLines());
						List<ProposalLineTaxes> proposalLineTaxesResult = MySQLManager.getProposalLineTaxesDAOInstance()
								.batchSave(connection, proposalLineTaxesList);
						if (!proposalLineTaxesResult.isEmpty()) {
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
				List<ProposalTaxes> proposalTaxResult = MySQLManager.getProposalTaxesDAOInstance()
						.batchDeleteAndSave(connection, proposalId, proposalTaxesList);
				if (proposalTaxResult != null) {
					connection.commit();
					return proposalResult;
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

	private static File createPdf() {
		File file = new File("F:/1.pdf");
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
			int pageWidth = (12 + 1) * 150;
			Rectangle two = new Rectangle(pageWidth, 600);
			document.setPageSize(two);
			document.open();
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
			Paragraph headerParagrah = new Paragraph("MATEEN", headerFont);
			Chapter chapter = new Chapter(headerParagrah, 1);
			headerFont.setColor(BaseColor.BLUE);
			headerParagrah.setAlignment(Element.ALIGN_CENTER);
			chapter.setNumberDepth(0);
			document.add(chapter);
			System.out.println("done");
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
		return null;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			// File file = new File("F:/1.pdf");
			File file = createPdf();
			MultiPart multipartEntity = null;
			try {
				String url = "https://dev-services.qount.io/HalfService/emails/attachment";
				FormDataBodyPart filePart = new FormDataBodyPart(file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
				filePart.setContentDisposition(FormDataContentDisposition.name("file").fileName("test.pdf").build());
				JSONObject emailJson = new JSONObject(
						"{\"recipients\":[\"mateen.khan@qount.io\"],\"cc_recipients\":[],\"subject\":\"Your A/P Aging Summary\",\"reportName\":\"A/P Aging Summary\",\"companyName\":\"cathy\",\"userName\":\"Uday Koorella\",\"mailBodyContentType\":\"text/html\",\"body\":\"asdf\"}");
				multipartEntity = new FormDataMultiPart()
						.field("emailRequest", emailJson.toString(), MediaType.APPLICATION_JSON_TYPE)
						.bodyPart(filePart);
				String auth = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi1hcHAucW91bnQuaW8vIiwidXNlcl9pZCI6InVkYXkua29vcmVsbGFAcW91bnQuaW8iLCJ1c2VybmFtZSI6InVkYXkua29vcmVsbGFAcW91bnQuaW8ifQ.GkrkWOHsK3G2cUBtFAOlb8W1MsJ3EUx7CJUPtIc5XQg";
				Response response = constructMultipartRequest(url, auth)
						.post(Entity.entity(multipartEntity, MediaType.MULTIPART_FORM_DATA));
				int responseStatus = response.getStatus();
				String responseString = response.readEntity(String.class);
				System.out.println("responseStatus:" + responseStatus);
				System.out.println("responseString:" + responseString);
			} catch (Exception e) {
				LOGGER.error(e);
				throw e;
			} finally {
				// document.close();
				if (null != multipartEntity) {
					multipartEntity.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SyncInvoker constructMultipartRequest(String url, String header) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		return client.target(url).request().header("Authorization", header);
	}
}

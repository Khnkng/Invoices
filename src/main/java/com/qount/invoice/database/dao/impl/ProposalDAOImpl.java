package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalDAO;
import com.qount.invoice.model.Coa;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.CustomerContactDetails;
import com.qount.invoice.model.Item;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
public class ProposalDAOImpl implements ProposalDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalDAOImpl() {
	}

	private static ProposalDAOImpl proposalDAOImpl = new ProposalDAOImpl();

	public static ProposalDAOImpl getProposalDAOImpl() {
		return proposalDAOImpl;
	}

	public Proposal save(Connection connection, Proposal proposal) throws Exception {
		LOGGER.debug("entered save(proposal):" + proposal);
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.INSERT_QRY);
				pstmt.setString(ctr++, proposal.getId());
				pstmt.setString(ctr++, proposal.getUser_id());
				pstmt.setString(ctr++, proposal.getCompany_id());
				pstmt.setString(ctr++, proposal.getCustomer_id());
				pstmt.setDouble(ctr++, proposal.getAmount());
				pstmt.setString(ctr++, proposal.getCurrency());
				pstmt.setString(ctr++, proposal.getDescription());
				pstmt.setString(ctr++, proposal.getObjectives());
				pstmt.setString(ctr++, proposal.getLast_updated_by());
				pstmt.setString(ctr++, proposal.getLast_updated_at());
				pstmt.setString(ctr++, proposal.getState());
				pstmt.setString(ctr++, proposal.getProposal_date());
				pstmt.setString(ctr++, proposal.getNotes());
				pstmt.setDouble(ctr++, proposal.getDiscount());
				pstmt.setDouble(ctr++, proposal.getDeposit_amount());
				pstmt.setDouble(ctr++, proposal.getProcessing_fees());
				pstmt.setString(ctr++, proposal.getNumber());
				pstmt.setString(ctr++, proposal.getDocument_id());
				pstmt.setDouble(ctr++, proposal.getSub_totoal());
				pstmt.setDouble(ctr++, proposal.getAmount_by_date());
				pstmt.setString(ctr++, proposal.getCreated_at());
				pstmt.setString(ctr++, proposal.getTerm());
				pstmt.setLong(ctr++, new Date().getTime());
				pstmt.setString(ctr++, proposal.getRecepientsMailsArr()!=null?proposal.getRecepientsMailsArr().toString():null);
				pstmt.setString(ctr++, proposal.getPlan_id());
				pstmt.setBoolean(ctr++, proposal.is_recurring());
				pstmt.setString(ctr++, proposal.getEmail_state());
				pstmt.setString(ctr++, proposal.getSend_to());
				pstmt.setString(ctr++, proposal.getEstimate_date());
//				pstmt.setString(ctr++, proposal.getDue_date());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal:" + proposal.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited save(proposal):" + proposal);
		}
		return proposal;
	}

	@Override
	public Proposal update(Connection connection, Proposal proposal) throws Exception {
		LOGGER.debug("entered proposal update:" + proposal);
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.UPDATE_QRY);
				pstmt.setString(ctr++, proposal.getUser_id());
				pstmt.setString(ctr++, proposal.getCompany_id());
				pstmt.setString(ctr++, proposal.getCustomer_id());
				pstmt.setDouble(ctr++, proposal.getAmount());
				pstmt.setString(ctr++, proposal.getCurrency());
				pstmt.setString(ctr++, proposal.getDescription());
				pstmt.setString(ctr++, proposal.getObjectives());
				pstmt.setString(ctr++, proposal.getLast_updated_by());
				pstmt.setString(ctr++, proposal.getLast_updated_at());
				pstmt.setString(ctr++, proposal.getState());
				pstmt.setString(ctr++, proposal.getProposal_date());
				pstmt.setString(ctr++, proposal.getNotes());
				pstmt.setDouble(ctr++, proposal.getDiscount());
				pstmt.setDouble(ctr++, proposal.getDeposit_amount());
				pstmt.setDouble(ctr++, proposal.getProcessing_fees());
				pstmt.setString(ctr++, proposal.getNumber());
				pstmt.setString(ctr++, proposal.getDocument_id());
				pstmt.setDouble(ctr++, proposal.getSub_totoal());
				pstmt.setDouble(ctr++, proposal.getAmount_by_date());
				pstmt.setString(ctr++, proposal.getTerm());
				pstmt.setString(ctr++, proposal.getRecepientsMailsArr()!=null?proposal.getRecepientsMailsArr().toString():null);
				pstmt.setString(ctr++, proposal.getPlan_id());
				pstmt.setBoolean(ctr++, proposal.is_recurring());
				pstmt.setString(ctr++, proposal.getEmail_state());
				pstmt.setString(ctr++, proposal.getSend_to());
				pstmt.setString(ctr++, proposal.getEstimate_date());
//				pstmt.setString(ctr++, proposal.getDue_date());
				pstmt.setString(ctr++, proposal.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating proposal:" + proposal.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited proposal update:" + proposal);
		}
		return proposal;
	}

	public Proposal get(String proposalID) throws Exception {
		LOGGER.debug("entered get by proposal id:" + proposalID);
		Proposal proposal = null;
		Customer customer = null;
		CustomerContactDetails customerContactDetails = null;
		Company company = null;
		List<ProposalLine> proposalLines = new ArrayList<ProposalLine>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.GET_QRY);
				pstmt.setString(1, proposalID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					if (proposal == null) {
						proposal = new Proposal();
						customer = new Customer();
						customerContactDetails = new CustomerContactDetails();
						company = new Company();
						proposal.setCustomer(customer);
						proposal.setProposalLines(proposalLines);
						proposal.setCustomerContactDetails(customerContactDetails);
						proposal.setCompany(company);
					}
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setId(rset.getString("il_id"));
					int proposalLineIndex = proposal.getProposalLines().indexOf(proposalLine);
					if (proposalLineIndex == -1) {
						proposalLine.setProposal_id(rset.getString("il_proposal_id"));
						proposalLine.setDescription(rset.getString("il_description"));
						Item item = new Item();
						item.setId(rset.getString("il_item_id"));
						item.setName(rset.getString("il_item_name"));
						proposalLine.setItem(item);
						proposalLine.setItem_id(item.getId());
						Coa coa = new Coa();
						coa.setId(rset.getString("il_item_id"));
						coa.setName(rset.getString("il_coa_name"));
						proposalLine.setCoa(coa);
						proposalLine.setObjectives(rset.getString("il_objectives"));
						proposalLine.setTax_id(rset.getString("il_tax_id"));
						proposalLine.setAmount(rset.getDouble("il_amount"));
						proposalLine.setLast_updated_at(rset.getString("il_last_updated_at"));
						proposalLine.setLast_updated_by(rset.getString("il_last_updated_by"));
						proposalLine.setQuantity(rset.getLong("il_quantity"));
						proposalLine.setPrice(rset.getDouble("il_price"));
						proposalLine.setNotes(rset.getString("il_notes"));
						proposalLine.setType(rset.getString("il_type"));
						proposal.getProposalLines().add(proposalLine);
						if (StringUtils.isBlank(proposal.getId())) {
							proposal.setId(rset.getString("id"));
							proposal.setTax_amount(rset.getDouble("tax_amount"));
							proposal.setIs_recurring(rset.getBoolean("is_recurring"));
							proposal.setUser_id(rset.getString("user_id"));
							proposal.setCompany_id(rset.getString("company_id"));
							proposal.setCustomer_id(rset.getString("customer_id"));
							proposal.setAmount(rset.getDouble("amount"));
							proposal.setCurrency(rset.getString("currency"));
							proposal.setDescription(rset.getString("description"));
							proposal.setObjectives(rset.getString("objectives"));
							proposal.setLast_updated_by(rset.getString("last_updated_by"));
							proposal.setLast_updated_at(rset.getString("last_updated_at"));
							proposal.setState(rset.getString("state"));
							proposal.setProposal_date(rset.getString("proposal_date"));
							proposal.setNotes(rset.getString("notes"));
							proposal.setDiscount(rset.getLong("discount"));
							proposal.setDeposit_amount(rset.getDouble("deposit_amount"));
							proposal.setProcessing_fees(rset.getDouble("processing_fees"));
							proposal.setNumber(rset.getString("number"));
							proposal.setDocument_id(rset.getString("document_id"));
							proposal.setSub_totoal(rset.getDouble("sub_totoal"));
							proposal.setAmount_by_date(rset.getDouble("amount_by_date"));
							proposal.setCreated_at(rset.getString("created_at"));
							proposal.setTerm(rset.getString("term"));
							proposal.setRecepientsMails(CommonUtils.getListString(rset.getString("recepients_mails")));
							proposal.setPlan_id(rset.getString("plan_id"));
							proposal.setIs_recurring(rset.getBoolean("is_recurring"));
							proposal.setEmail_state(rset.getString("email_state"));
							proposal.setSend_to(rset.getString("send_to"));
							customer.setCustomer_id(rset.getString("customer_id"));
							customer.setPayment_spring_id(rset.getString("payment_spring_id"));
							customer.setCustomer_name(rset.getString("customer_name"));
							customer.setCard_name(rset.getString("card_name"));
							customer.setCustomer_address(rset.getString("customer_address"));
							customer.setCustomer_city(rset.getString("customer_city"));
							customer.setCustomer_country(rset.getString("customer_country"));
							customer.setCustomer_state(rset.getString("customer_state"));
							customer.setCustomer_ein(rset.getString("customer_ein"));
							customer.setCustomer_zipcode(rset.getString("customer_zipcode"));
							customer.setPhone_number(rset.getString("phone_number"));
							customer.setCoa(rset.getString("coa"));
							customer.setTerm(rset.getString("term"));
							customer.setFax(rset.getString("fax"));
							customer.setStreet_1(rset.getString("street_1"));
							customer.setStreet_2(rset.getString("street_2"));
							Currencies currencies_2 = new Currencies();
							currencies_2.setCode(rset.getString("code"));
							currencies_2.setName(rset.getString("name"));
							currencies_2.setHtml_symbol(rset.getString("html_symbol"));
							currencies_2.setJava_symbol(rset.getString("java_symbol"));
							customerContactDetails.setId(rset.getString("ccd_id"));
							customerContactDetails.setCustomer_id(rset.getString("ccd_customer_id"));
							customerContactDetails.setFirst_name(rset.getString("ccd_first_name"));
							customerContactDetails.setLast_name(rset.getString("ccd_last_name"));
							customerContactDetails.setMobile(rset.getString("ccd_mobile"));
							customerContactDetails.setEmail(rset.getString("ccd_email"));
							customerContactDetails.setOther(rset.getString("ccd_other"));
							company.setActive(rset.getBoolean("com_active"));
							company.setId(rset.getString("com_id"));
							company.setName(rset.getString("com_name"));
							company.setAddress(rset.getString("com_address"));
							company.setCity(rset.getString("com_city"));
							company.setContact_first_name(rset.getString("com_contact_first_name"));
							company.setContact_last_name(rset.getString("com_contact_last_name"));
							company.setCurrency(rset.getString("com_currency"));
							company.setEin(rset.getString("com_ein"));
							company.setEmail(rset.getString("com_email"));
							company.setCountry(rset.getString("com_country"));
							company.setPhone_number(rset.getString("com_phone_number"));
							company.setState(rset.getString("com_state"));
							company.setZipcode(rset.getString("com_zipcode"));
							proposal.setCurrencies(currencies_2);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposal for proposalID [ " + proposalID + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get by proposal id:" + proposalID);
		}
		return proposal;

	}

	@Override
	public List<Proposal> getProposalList(String userID, String companyID, String state) throws Exception {
		LOGGER.debug("entered getProposalList userID:" + userID + " companyID:" + companyID + "state:" + state);
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
			throw new WebApplicationException("userID or companyID cannot be empty");
		}
		List<Proposal> proposalLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Proposal.GET_PROPOSAL_LIST_QRY;
				query += "`user_id`='" + userID + "' AND `company_id`= '" + companyID + "' ";
				if (!StringUtils.isEmpty(state)) {
					query += "AND state='" + state + "'";
				}
				pstmt = connection.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Proposal proposal = new Proposal();
					proposal.setEstimate_date(rset.getString("estimate_date"));
					proposal.setNumber(rset.getString("number"));
					proposal.setId(rset.getString("id"));
					proposal.setProposal_date(rset.getString("proposal_date"));
					proposal.setAmount(rset.getDouble("amount"));
					proposal.setCurrency(rset.getString("currency"));
					proposal.setState(rset.getString("state"));
					proposal.setAmount_by_date(rset.getDouble("amount_by_date"));
					proposalLst.add(proposal);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposals for user_id [ " + userID + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getProposalList userID:" + userID + " companyID:" + companyID + "state:" + state);
		}
		return proposalLst;

	}

	@Override
	public Proposal delete(Proposal proposal) throws Exception {
		LOGGER.debug("entered proposal delete:" + proposal);
		Connection connection = null;
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.DELETE_QRY);
				pstmt.setString(1, proposal.getId());
				pstmt.setString(2, proposal.getUser_id());
				pstmt.setString(3, proposal.getCompany_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
				LOGGER.debug("no of proposal deleted:" + rowCount);
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record deleted:" + proposal.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting proposal:" + proposal.getId() + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited proposal delete:" + proposal);
		}
		return proposal;
	}

	@Override
	public boolean deleteLst(String userId, String companyId, String lst) throws Exception {
		LOGGER.debug("entered proposal delete lst:" + lst);
		Connection connection = null;
		if (StringUtils.isEmpty(lst)) {
			return false;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Proposal.DELETE_LST_QRY;
				query += lst + ") AND `user_id` = '" + userId + "' AND `company_id` ='" + companyId + "';";
				pstmt = connection.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of proposal deleted:" + rowCount);
				if (rowCount > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record deleted:" + lst + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting proposal lst:" + lst + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited proposal delete lst:" + lst);
		}
		return false;
	}

	@Override
	public boolean updateState(String userId, String companyId, String lst, String state) throws Exception {
		LOGGER.debug("entered updateStateAsSent lst:" + lst);
		Connection connection = null;
		if (StringUtils.isEmpty(lst) || StringUtils.isAnyBlank(companyId, userId, state) ) {
			return false;
		}
		if(!StringUtils.equals(state, "accept") || !StringUtils.equals(state, "deny")){
			throw new WebApplicationException("state can only be either 'accept' or 'deny'");
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Proposal.UPDATE_AS_SENT_QRY;
				query += lst + ") AND `user_id` = '" + userId + "' AND `company_id` ='" + companyId + "';";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, state);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of proposal updated:" + rowCount);
				if (rowCount > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record updated:" + lst + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error updateStateAsSent lst:" + lst + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateStateAsSent lst:" + lst);
		}
		return false;
	}


	@Override
	public boolean denyProposal(String userID, String companyID, String proposalList) throws Exception {
		LOGGER.debug("entered denyProposal lst:" + proposalList);
		Connection connection = null;
		if (StringUtils.isEmpty(proposalList) || StringUtils.isAnyBlank(companyID, userID)) {
			return false;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Proposal.DENY_PROPSOAL;
				query += proposalList + ") AND `user_id` = '" + userID + "' AND `company_id` ='" + companyID + "';";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, Constants.PROPOSAL_STATE_DENY);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of proposal updated:" + rowCount);
				if (rowCount > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record updated:" + proposalList + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error update State As denied lst:" + proposalList + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited denyProposal proposalList:" + proposalList);
		}
		return false;
	}

	@Override
	public List<Proposal> updateProposal(Connection connection, List<Proposal> proposalList) throws Exception {
		LOGGER.debug("entered updateProposal:" + proposalList);
		if (proposalList == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.UPDATE_STATE);
				Iterator<Proposal> proposalItr = proposalList.iterator();
				int ctr = 1;
				while (proposalItr.hasNext()) {
					Proposal proposal = proposalItr.next();
					pstmt.setString(ctr++, proposal.getState());
					pstmt.setString(ctr++, proposal.getInvoice_id());
					pstmt.setString(ctr++, proposal.getId());
					ctr = 1;
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return proposalList;
				} else {
					throw new WebApplicationException("unable to update proposal", 500);
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating proposal:" +  e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited updateProposal:" + proposalList);
		}
		return proposalList;
	}

}

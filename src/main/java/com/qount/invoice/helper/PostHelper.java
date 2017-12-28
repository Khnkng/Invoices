package com.qount.invoice.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.qount.invoice.model.Post;
import com.qount.invoice.model.User;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.LTMUtils;

public class PostHelper {

	private static final Logger LOGGER = Logger.getLogger(PostHelper.class);

	/**
	 * 
	 * @param userId
	 * @param companyId
	 * @param invoiceId
	 *            Creates default post and saves the post Id in Invoice table
	 */
	public static void createPost(String userId, String companyId, String invoiceId) {
		try {
			new Thread(new Runnable() {
				public void run() {
					User user = getUser(userId);
					if (user != null) {
						Post post = new Post();
						post.setId(UUID.randomUUID().toString());
						post.setEntityID(invoiceId);
						post.setEntityType("invoice");
						post.setMessage("Invoice sent by " + user.getFirstName() + " " + user.getLastName());
						String response = createPost(userId, companyId, post);
						if (response != null) {
							savePostIdInInvoice(invoiceId, post.getId());
						}
					}
				}
			}).start();
		} catch (Exception e) {
			LOGGER.error("Error creating post for invoice", e);
		}
	}

	private static boolean savePostIdInInvoice(String invoiceId, String postId) {
		boolean result = false;
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement("update invoice set post_id = ? where id = ?");
			pstmt.setString(1, postId);
			pstmt.setString(2, invoiceId);
			int rowCount = pstmt.executeUpdate();
			LOGGER.debug("rows updated [ " + rowCount + " ]");
			result = rowCount > 0;

		} catch (Exception e) {
			LOGGER.error("Error saving post ID", e);
		} finally {
			DatabaseUtilities.closeResources(null, pstmt, connection);
		}
		return result;
	}

	private static User getUser(String userId) {
		User user = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			int qryCtr = 1;
			if (conn != null) {
				pstmt = conn.prepareStatement("SELECT u.id,u.colorCode,  u.firstName,  u.lastName from user u where id = ?");
				pstmt.setString(qryCtr++, userId);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					user = new User();
					user.setId(rset.getString("id"));
					user.setFirstName(rset.getString("firstName"));
					user.setLastName(rset.getString("lastName"));
					user.setColorCode(rset.getString("colorCode"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving user", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, conn);
		}
		return user;
	}

	private static String createPost(String userId, String companyId, Post post) {
		String response = null;
		String payload = Constants.GSON.toJson(post);
		try {
			String path = LTMUtils.getHostAddress("collaboration.service.docker.hostname", "collaboration.service.docker.port",
					"oneapp.base.url");
			path = path + "CollaborationServices/users/" + userId + "/companies/" + companyId + "/posts";
			LOGGER.debug("path = " + path);
			LOGGER.debug("payload = " + payload);
			Response responseEntity = ClientBuilder.newClient().target(path).request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(payload, MediaType.APPLICATION_JSON));
			response = responseEntity.readEntity(String.class);
			System.out.println("response " + response);
			LOGGER.debug("responseString = " + response);
			int status = responseEntity.getStatus();
			if (status != 200) {
				response = null;
			}
		} catch (Exception e) {
			LOGGER.error("Error creating post", e);
		}
		return response;
	}

	public static void main(String[] args) {
		createPost("uday.koorella@qount.io", "495a05f7-4b01-421d-9f64-16d73618a38d", "0239ecda-30b7-4e5a-8b74-f56137665799");
	}

}

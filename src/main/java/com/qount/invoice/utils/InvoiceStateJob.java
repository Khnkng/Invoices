package com.qount.invoice.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.WebApplicationException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class InvoiceStateJob implements Job {
	
	public InvoiceStateJob() {
	}
	
	
	public	boolean run(){
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				String query = "UPDATE invoice_test SET state = 'past_due' WHERE id IN ( SELECT id  FROM (SELECT id FROM invoice_test WHERE state !='paid' AND due_date> ?) AS ids);";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1,"");
				rset = pstmt.executeQuery();
				if (rset.next()) {
				}
			}
		}catch(WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return false;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		
	}

}

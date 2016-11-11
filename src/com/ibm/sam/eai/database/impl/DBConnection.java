package com.ibm.sam.eai.database.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import com.ibm.sam.eai.database.impl.ApprovalInfo;

public class DBConnection {

	private static final String DB_DRIVER = "com.ibm.db2.jcc.DB2Driver";
	private static final String DB_URL = "jdbc:db2://10.51.232.250:50001/EAIData";
	private static final String DB_USERNAME = "db2inst2";
	private static final String DB_PASSWORD = "db2@dmin";
	private Connection connection = null;


	private void initConnection()
	{
		try {
			//Load class into memory
			Class.forName(DB_DRIVER);
			//Establish connection
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);


		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}    
	}
	public Connection getConnection(){       
		//If instance has not been created yet, create it
		if(connection == null){
			initConnection();
		}
		return connection;
	} 
	public void closeConnection()
	{
		try {
			if(connection!=null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * Method to insert Approval Request in database
	 * @param approvalObj
	 * @return integer > 0 if operation executed successfully else returns 0
	 */
	public int insertApprovalRequest(ApprovalInfo approvalObj)
	{
		Connection conn=getConnection();
		PreparedStatement pstmt = null;
		int result=0;

		try
		{
			String query = "insert into SOCIAL_EAI_APPROVAL(REQ_SOCIAL_ID,REQ_FIRSTNAME,REQ_LASTNAME,REQ_SOCIAL_EMAIL,APPROVAL_STATUS,TIME_CREATED) values(?, ?, ?, ?, ?,?)";
			pstmt = conn.prepareStatement(query); 
			pstmt.setString(1,approvalObj.getSocial_ID()); 
			pstmt.setString(2,approvalObj.getFirstName()); 
			pstmt.setString(3,approvalObj.getLastName()); 
			pstmt.setString(4,approvalObj.getSocialEmail());
			pstmt.setInt(5,approvalObj.getApprovalStatus());
			pstmt.setTimestamp(6,approvalObj.getTime_Created());

			result=pstmt.executeUpdate(); 
		}
		catch (Exception e) {
			e.printStackTrace();
		} 

		return result;
	}
	/**
	 * Method to Approve or Reject Registration Request
	 * @param approval_status
	 * @param approvedBy
	 * @param approverComment
	 * @param time_approved
	 * @param social_Email
	 * @return integer > 0 if operation executed successfully else returns 0
	 */
	public int approve_reject_Request(int approval_status, String approvedBy, String approverComment, Timestamp time_approved,String social_Email)
	{
		Connection conn=getConnection();

		PreparedStatement pstmt = null;
		int result=0;

		try
		{
			String query = "update SOCIAL_EAI_APPROVAL set (APPROVAL_STATUS,APPROVED_BY,APPROVER_COMMENT,TIME_APPROVED)=(?,?,?,?) where REQ_SOCIAL_EMAIL=?";
			pstmt = conn.prepareStatement(query); 
			pstmt.setInt(1,approval_status); 
			pstmt.setString(2,approvedBy); 
			pstmt.setString(3,approverComment);
			pstmt.setTimestamp(4,time_approved);
			pstmt.setString(5,social_Email);

			result=pstmt.executeUpdate(); 

		}
		catch (Exception e) {

			System.out.println("EXception:");
			e.printStackTrace();
		} 


		return result;
	}
	/**
	 * Method to get list of pending Requests
	 * @return Array of Approval Objects
	 */
	public ArrayList<ApprovalInfo> getPendingApprovalRequests()
	{
		Connection conn=getConnection();
		ArrayList<ApprovalInfo> approvalList=new ArrayList<ApprovalInfo>();

		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM SOCIAL_EAI_APPROVAL WHERE APPROVAL_STATUS = 0");

			while (rs.next()) {
				ApprovalInfo approvalobj=new ApprovalInfo();
				approvalobj.setSocial_ID(rs.getString("REQ_SOCIAL_ID"));
				approvalobj.setSocialEmail(rs.getString("REQ_SOCIAL_EMAIL"));
				approvalobj.setFirstName(rs.getString("REQ_FIRSTNAME"));
				approvalobj.setLastName(rs.getString("REQ_LASTNAME"));
				approvalobj.setApprovalStatus(rs.getInt("APPROVAL_STATUS"));
				approvalobj.setTime_Created(rs.getTimestamp("TIME_CREATED"));

				approvalList.add(approvalobj);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return approvalList;
	}
	/**
	 * Method to update No of Remainder sent for approval
	 * @param socialEmail
	 * @param remainderCount
	 * @return integer > 0 if operation executed successfully else returns 0
	 */
	public int updateRemainderCount(String socialEmail,int remainderCount)
	{
		Connection conn=getConnection();

		PreparedStatement pstmt = null;
		int result=0;

		try
		{
			String query = "update SOCIAL_EAI_APPROVAL set (NUM_REMINDER_SENT)=(?) where REQ_SOCIAL_EMAIL=?";
			pstmt = conn.prepareStatement(query); 
			pstmt.setInt(1,remainderCount); 
			pstmt.setString(2,socialEmail); 

			result=pstmt.executeUpdate(); 

		}
		catch (Exception e) {
			e.printStackTrace();
		} 


		return result;
	}

}

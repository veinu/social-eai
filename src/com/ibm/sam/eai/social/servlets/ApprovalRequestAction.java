package com.ibm.sam.eai.social.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.sam.eai.database.impl.DBConnection;

/**
 * Servlet implementation class requestAction
 */
public class ApprovalRequestAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApprovalRequestAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String socialId[]=request.getParameterValues("socialId[]");
		String actions[]=request.getParameterValues("actions[]");
		String comments[]=request.getParameterValues("comments[]");
		DBConnection dbconn=new DBConnection();
		
		for (int i=0;i<socialId.length;i++)
		{
			int requestStatus=0;
			java.util.Date date= new java.util.Date();
			Timestamp timestamp=new Timestamp(date.getTime());
			if(actions[i].equals("approved"))
				requestStatus=1;
			else if(actions[i].equals("rejected"))
				requestStatus=2;
			dbconn.approve_reject_Request(requestStatus,"admin", comments[i], timestamp, socialId[i]);
		
		}
	}
}

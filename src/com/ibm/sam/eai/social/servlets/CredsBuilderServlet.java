package com.ibm.sam.eai.social.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.sam.eai.social.util.EAILogger;

/**
 * Servlet implementation class SAMCredsBuilder
 */
@WebServlet("/SAMCredsBuilder")
public class CredsBuilderServlet extends HttpServlet {
	private static final String className = CredsBuilderServlet.class.getName();
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CredsBuilderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method ="doGet";
		EAILogger.debug(className, className + ", " + method);
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method ="doPost";
		EAILogger.debug(className, className + ", " + method);
		// TODO Auto-generated method stub
	}

}

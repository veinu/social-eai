
<%@page import="javax.servlet.http.Cookie"%>
<%@ page 
language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.*,java.io.*,java.net.*,javax.net.ssl.*,com.ibm.sam.eai.social.*"
%>
<html>
<head>
<title>SocialEai : Linkedin Integration</title>
</head>
<body>

<%
	String code = request.getParameter("code");
	request.setAttribute("code", request.getParameter("code"));
	request.setAttribute("provider", "linkedin");
	request.getRequestDispatcher("sociallogin").forward(request, response);
%>
<br><br>

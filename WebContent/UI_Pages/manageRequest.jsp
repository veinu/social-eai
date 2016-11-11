<%@page import="com.ibm.sam.eai.social.util.EAILogger"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Request Manager</title>
</head>
<body>
	<%
		EAILogger.info("ManagerRequest","manageRequest initial session:"+session.toString());
	
		Object socialProviderName = session.getAttribute("socialProviderName");
		Object locale = session.getAttribute("locale");
		Object operation = session.getAttribute("operation");
		Object errorDivTagID = session.getAttribute("targetErrorElementId");
		Object firstName = session.getAttribute("firstName");
		Object lastName = session.getAttribute("lastName");
		Object socialEmail = session.getAttribute("socialEmail");
		Object socialId = session.getAttribute("socialId");
		Object code = session.getAttribute("code");
		Object provider = session.getAttribute("provider");
		Object user = session.getAttribute("User");

		request.setAttribute("socialProviderName", socialProviderName);
		request.setAttribute("locale", locale);
		request.setAttribute("operation", operation);
		request.setAttribute("targetErrorElementId", errorDivTagID);
		request.setAttribute("firstName", firstName);
		request.setAttribute("lastName", lastName);
		request.setAttribute("socialEmail", socialEmail);
		request.setAttribute("socialId", socialId);
		request.setAttribute("code", code);
		request.setAttribute("provider", provider);
		request.setAttribute("User", user);
		
		EAILogger.info("ManagerRequest","manageRequest request:"+request.toString());
		
		session.removeAttribute("socialProviderName");
		session.removeAttribute("firstName");
		session.removeAttribute("lastName");
		session.removeAttribute("socialEmail");
		session.removeAttribute("socialId");
		session.removeAttribute("code");
		session.removeAttribute("provider");
		session.removeAttribute("User");
		
		EAILogger.info("ManagerRequest","manageRequest session attributes removed");
		
		Object obj1 = session.getAttribute("userpassword");
		String userpwd=null;
		if(obj1!=null){
			userpwd= obj1.toString();
			if(userpwd !=null || (userpwd.equals("")==false)){
				session.setAttribute("registerme", "yes");
			}
			else{
				session.setAttribute("registerme", "no");
			}
		}
		
		Object obj2 = session.getAttribute("nativepassword");
		String nativepwd=null;
		if(obj2!=null){
			nativepwd= obj2.toString();
			if(nativepwd !=null || (nativepwd.equals("")==false)){
				session.setAttribute("linkmyaccount", "yes");
			}
			else{
				session.setAttribute("linkmyaccount", "no");
			}
		}
		
		EAILogger.info("ManagerRequest","manageRequest new attributes added");
		EAILogger.info("ManagerRequest","manageRequest final session:"+session.toString());
		EAILogger.info("ManagerRequest","manageRequest forwarding request to sociallogin servlet");
		request.getRequestDispatcher("/sociallogin").forward(request, response);
	%>
</body>
</html>
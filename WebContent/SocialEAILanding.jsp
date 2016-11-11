<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Landing Page</title>
<% 

Object o = session.getAttribute("redirectURL");
Object ec = session.getAttribute("errorCode");
Object em = session.getAttribute("errorMsg");
Object targetErrorElementId = session.getAttribute("targetErrorElementId");
Object operation = session.getAttribute("operation");
System.out.println("portalRedirectUrl: " +o);
System.out.println("errorCode: " +ec);
System.out.println("errorMsg: " +em);
System.out.println("targetErrorElementId: " +targetErrorElementId);
System.out.println("operation: " +operation);

String sessionURL = "";
String eCode = "";
String eMsg = "";
String errorDivTagID = "";
String opr = "";

if(o!=null){
	sessionURL = (String)o;
}
if(ec!=null){
	eCode = (String)ec;
}
if(em!=null){
	eMsg = (String)em;
}
if(targetErrorElementId!=null){
	errorDivTagID = (String)targetErrorElementId;
}
if(operation!=null){
	opr = (String)operation;
}

%>
<%-- <SCRIPT LANGUAGE=JavaScript>
<!--
var sessionURL = '<%=sessionURL%>';
var eCode = '<%=eCode%>';
var eMsg = '<%=eMsg%>';
var errorDivTagID = '<%=errorDivTagID%>';
var errorDivMessageTagID = '<%=errorDivTagID%>' + '_errorMessage';
var opr = '<%=opr%>';

//if(window.name=="1425386002082"){
	if(eMsg!=null && eMsg.length>0){
		alert("ERROR CONDITION, eCode : "+eCode);
		alert("ERROR CONDITION, eMsg : "+eMsg);
		alert("ERROR CONDITION, errorDivTagID : "+errorDivTagID);
		alert("ERROR CONDITION, errorDivMessageTagID : "+errorDivMessageTagID);
		window.opener.document.getElementById(errorDivTagID).style = "display: block";
		window.opener.document.getElementById(errorDivMessageTagID).innerHTML=eMsg;	
		window.close();
	}else if (opr=='registrationcomplete' || opr =='registrationpending'){
		window.location = sessionURL;
		//window.location = decodeURIComponent(sessionURL);
	}else{
		//alert("Reloading opener window with "+sessionURL);
		try{
			window.opener.location = sessionURL;
			//window.opener.location = decodeURIComponent(sessionURL);
			window.close();
		}catch(error){
			alert("Error " + error);
		}
	}
//}
//-->
</SCRIPT> --%>
</head>
<body>
<br><br><br>
<p align='center'> Profile Data : <%=session.getAttribute("profileData")%> <br>
<p align='center'> User Data : <%=session.getAttribute("userData")%> <br>


<p align='center'> Redirecting to <%=sessionURL%> ..<br><br><br> <font color='red'> please uncomment javascript.</font></p>
<br><br><br>
</body>
</html> 

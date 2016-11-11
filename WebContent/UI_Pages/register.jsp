
<%@page import="com.ibm.sam.eai.social.util.EAILogger"%>
<%@page import="javax.servlet.http.Cookie"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*,java.io.*,java.net.*,javax.net.ssl.*,com.ibm.sam.eai.social.*"%>
<html>
<head>
<title>Register</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
	
<!-- <link rel="stylesheet" href="CSS/bootstrap.min.css">
<script src="JS/jquery-1.12.2.min.js"></script>
<script src="JS/bootstrap.min.js"></script> -->

<script>
	$(document).ready(function() {
		$("#userpassword").blur(function(){
			var userpwd=$("#userpassword").val();
			if(userpwd!=""){
			$.ajax({
				type:'POST',
				url:"UI_Pages/setUserPwd.jsp",
				data:"userpassword="+userpwd		
			});
			}
		});
	
		$("#register").change(function() {
			if ($("#register").is(":checked")) {
				$("#passwordlabel").show();	
				$("#userpassword").prop('required',true);
			}
			else{
				$("#passwordlabel").hide();
				$("#userpassword").val("");
				$("#userpassword").prop('required',false);
				$.ajax({
					type:'POST',
					url:"UI_Pages/setUserPwd.jsp",
					data:"userpassword="+""		
				});
			}
		}); 
	});
</script>

<style>
h1 {
	font-size: 2em;
	font-style: italic;
}

/* Add a gray background color and some padding to the footer */
footer {
	background-color: #f2f2f2;
	padding: 25px;
}

.row [class*="col-"] {
	margin-bottom: -99999px;
	padding-bottom: 99999px;
}

.row {
	overflow: hidden;
}
</style>
</head>
<body>

	<%
	EAILogger.info("Register","register request:"+request.toString());
	
	String socialProviderName = request.getParameter("socialProviderName");
	String firstName = request.getParameter("firstName");
	String lastName = request.getParameter("lastName");
	String socialEmail = request.getParameter("socialEmail");
	String socialId = request.getParameter("socialId");
	String code = request.getParameter("code");
	Object provider = request.getAttribute("provider");
	String providerName=(String) provider;
	String user = request.getParameter("User");
	
	EAILogger.info("Register","register initial session:"+session.toString());
	
	session.setAttribute("socialProviderName", socialProviderName);
	session.setAttribute("firstName", firstName);
	session.setAttribute("lastName", lastName);
	session.setAttribute("socialEmail", socialEmail);
	session.setAttribute("socialId", socialId);
	session.setAttribute("code", code);
	session.setAttribute("provider", provider);
	session.setAttribute("User", user);
	
	EAILogger.info("Register","register final session:"+session.toString());
	%>
	
	<form action="UI_Pages/manageRequest.jsp"
		method="post">
		<div class="page-header">
			<div class="container text-center">
				<h1>Register ?</h1>
			</div>
		</div>
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="panel panel-primary">
						<!-- 	<div class="panel-heading">Login with Company</div> -->
						<div class="panel-body" style="text-align: center; color: #443D5F">
							<br />
							<p style="font-size: 18px;" id="eaimessage">
								Looks like you are first time logging in with
								<%=providerName%></p>
							<br />
							<div class="checkbox" style="font-size: 18px;">
								<label><input id="register" type="checkbox"
									name="register"
									style="height: 16px; width: 16px; border: 2px solid #999; color: #fff; background-color: #33b;">Do
									you want to register using your <%=providerName%> profile information ?</label>
							</div>
							<br />
							<div class="input" style="font-size: 16px;">
								<label id="passwordlabel" style="display:none">Please provide a password for the new account: <input
									id="userpassword" type="password" name="userpassword">
								</label>
							</div>
							<br />
							<div>
								<button id="continue" type="submit" class="btn btn-default"
									style="font-weight: bold; color: #443D5F">Continue</button>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
		<br> <br> <br> <br>
	</form>
	<footer class="container-fluid text-center" style="color: #443D5F">
		<p>Copyright Information</p>
	</footer>
</body>
</html>


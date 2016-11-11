<%@page import="com.ibm.sam.eai.social.model.SocialProfileData"%>
<%@page import="com.ibm.sam.eai.social.util.EAILogger"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Link with Native ID</title>
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
		$("#nativepassword").blur(function(){
			var nativepwd=$("#nativepassword").val();
			if(nativepwd!=""){
			$.ajax({
				type:'POST',
				url:"UI_Pages/setNativePwd.jsp",
				data:"nativepassword="+nativepwd		
			});
			}
		});
	
		$("#linkId").change(function() {
			if ($("#linkId").is(":checked")) {
				$("#passwordlabel").show();	
				$("#nativepassword").prop('required',true);
			}
			else{
				$("#passwordlabel").hide();
				$("#nativepassword").val("");
				$("#nativepassword").prop('required',false);
				$.ajax({
					type:'POST',
					url:"UI_Pages/setNativePwd.jsp",
					data:"nativepassword="+""		
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
	EAILogger.info("LinkId","linkId initial session:"+session.toString());
	Object obj= session.getAttribute("socialProfileData");
	SocialProfileData profile =(SocialProfileData)obj;
	String email= profile.getEmail();
	String socialProviderName = request.getParameter("socialProviderName");
	String firstName = request.getParameter("firstName");
	String lastName = request.getParameter("lastName");
	String socialEmail = request.getParameter("socialEmail");
	String socialId = request.getParameter("socialId");
	String code = request.getParameter("code");
	Object provider = request.getAttribute("provider");
	String providerName=(String) provider;
	String user = request.getParameter("User");

	EAILogger.info("LinkId","linkId request:"+request.toString());	
	
	session.setAttribute("socialProviderName", socialProviderName);
	session.setAttribute("firstName", firstName);
	session.setAttribute("lastName", lastName);
	session.setAttribute("socialEmail", socialEmail);
	session.setAttribute("socialId", socialId);
	session.setAttribute("code", code);
	session.setAttribute("provider", provider);
	session.setAttribute("User", user);
	
	EAILogger.info("LinkId","linkId final session:"+session.toString());
	%>
	<form action="UI_Pages/manageRequest.jsp"
		method="post">
		<div class="page-header">
			<div class="container text-center">
				<h1>Link ?</h1>
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
								Looks like your
							email <%=email%> is already registered with Native ID.
								</p>
							<br />
							<div class="checkbox" style="font-size: 18px;">
								<label><input id="linkId" type="checkbox"
									name="linkId" 
									style="height: 16px; width: 16px; border: 2px solid #999; color: #fff; background-color: #33b;">Do
								you want to link your Social Id with your Native ID?</label>
							</div>
							<br />
							<div class="input" style="font-size: 16px;">
								<label id="passwordlabel" style="display:none">Please enter your native account password: <input
									id="nativepassword" type="password" name="nativepassword">
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

<%@page import="java.util.List"%>
<%@page import="com.ibm.sam.eai.ldap.impl.LdapConnectionManager"%>
<%@page import="com.ibm.sam.eai.ldap.impl.LdapDaoImpl"%>
<%@page import="com.ibm.sam.eai.social.model.SocialProfileData"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Manage EAI Approvers</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="CSS/bootstrap.min.css">
<script src="JS/jquery-1.12.2.min.js"></script>
<script src="JS/bootstrap.min.js"></script>
<link rel="stylesheet" href="CSS/Custom.css">
<script>
	$(document).ready(
			function() {
				
				$("#addSearchButton").click(
						function() {
							 window.location.href='searchApprover.jsp';
						});
				$("#removeButton").click(
						function() {
						
							var values = new Array();
							$.each($("input[name='chkbox[]']:checked").closest(
									"td").siblings("td"), function() {
								values.push($(this).text());
							});

							alert("Tese values will be removed ---" + values.join(", "));

						});

			});
</script>
</head>
<body>

<%
LdapDaoImpl ldapDaoImpl=new LdapDaoImpl(LdapConnectionManager.getInstance().getConnection());
List<SocialProfileData> approvers= ldapDaoImpl.listAllApprovers();
%>
	<div class="page-header">
		<div class="container text-center">
			<h1>Manage Approvers</h1>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<div class="panel panel-primary">
					<div class="panel-body" style="text-align: center; color: #443D5F">
						<br />
						
						<table class="table table-bordered" id="table">
							<thead>
								<tr>
									<th></th>
									<th>Approver Name</th>
									<th>Approver ID</th>

								</tr>
							</thead>
							<tbody>
							<% for(SocialProfileData data:approvers){%>
								<tr>
									<td><input type="checkbox" name="chkbox[]" id="checkbox" /></td>
									<td><%=data.getFirstName() %></td>
									<td><%=data.getEmail() %></td>

								</tr>
								<%} %>
							</tbody>
						</table>
						<br/><br/>
						<button type="button" id="addSearchButton" type="submit"
							class="btn btn-primary ">Search and Add</button>  &nbsp;&nbsp;&nbsp;	
						<button type="button" id="removeButton" type="submit"
							class="btn btn-primary ">Remove</button>
							<br/><br/>
					</div>
				</div>
			</div>
		</div>
	</div>
	<br>
	<br>
	<br>
	<br>
	<footer class="container-fluid text-center" style="color: #443D5F">
		<p>Copyright Information</p>
	</footer>
</body>
</html>

<%@page import="com.ibm.sam.eai.social.model.SocialProfileData"%>
<%@page import="java.util.List"%>
<%@page import="com.ibm.sam.eai.ldap.impl.LdapConnectionManager"%>
<%@page import="com.ibm.sam.eai.ldap.impl.LdapDaoImpl"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Search EAI Approver</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="CSS/bootstrap.min.css">
<script src="JS/jquery-1.12.2.min.js"></script>
<script src="JS/bootstrap.min.js"></script>
<link rel="stylesheet" href="CSS/Custom.css">
<script
	src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script
	src="https://cdn.datatables.net/1.10.12/js/dataTables.jqueryui.min.js"></script>
 
<script>
	$(document).ready(
			function() {
				
				$('#table').dataTable({
			          "paging" : false,
			          "ordering" : false,
			          "info"  : true,
			          "searching" : true 
			        }); 
		
				/* $("#searchButton").click(
						function() {
							var searchCategory = $("#searchFilter").val();
							var searchValue = $("#searchValue").val();
							alert("searchCategory :" + searchCategory + "+ "
									+ searchValue);
						}); */

				$("#addButton").click(
						function() {
							alert("User has clicked Add");
							var values = new Array();
							$.each($("input[name='chkbox[]']:checked").closest(
									"td").siblings("td"), function() {
								values.push($(this).text());
							});

							alert("values are ---" + values.join(", "));

						});

			});
</script>
</head>
<body>
<%
LdapDaoImpl ldapDaoImpl=new LdapDaoImpl(LdapConnectionManager.getInstance().getConnection());
List<SocialProfileData> users= ldapDaoImpl.listAllNonApprovers();
%>
	<div class="page-header">
		<div class="container text-center">
			<h1>Add Users To Approver Group</h1>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<div class="panel panel-primary">
					<div class="panel-body" style="text-align: center; color: #443D5F">
						<br />
						<!-- <div class="dropdown">
							<select class="left" id="searchFilter" style="height: 28px;">
								<option>Search Filter</option>
								<option value="Name">Name</option>
								<option value="ID">ID</option>
							</select> <input type="text" id="searchValue" placeholder="Search Value"
								class="middle">
							<button type="button" id="searchButton"
								class="btn btn-primary right">Search</button>
							<br /> <br />
						</div> -->
						<table class="table table-bordered" id="table">
							<thead>
								<tr>
									<th></th>
									<th>Name</th>
									<th>ID</th>

								</tr>
							</thead>
							<tbody>
							<% for(SocialProfileData data:users){%>
								<tr>
									<td><input type="checkbox" name="chkbox[]" id="checkbox" /></td>
									<td><%=data.getFirstName() %></td>
									<td><%=data.getEmail() %></td>

								</tr>
								<%} %>
							</tbody>
						</table>
						<button type="button" id="addButton" type="submit"
							class="btn btn-primary">Add</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<footer class="container-fluid text-center" style="color: #443D5F">
		<p>Copyright Information</p>
	</footer>
</body>
</html>
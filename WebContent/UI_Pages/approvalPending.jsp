<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.ibm.sam.eai.database.impl.DBConnection"%>
<%@page import="com.ibm.sam.eai.database.impl.ApprovalInfo"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="CSS/bootstrap.min.css">
<script src="JS/jquery-1.12.2.min.js"></script>
<script src="JS/bootstrap.min.js"></script>
<link rel="stylesheet" href="CSS/Custom.css">
<script>
	$(document)
			.ready(
					function() {
						$(
						"input:checkbox[name=chk]:checked")
						.each(
								function() {
									var Id = $(this)
											.attr(
													"id");
									social_Id = Id
									comments_Id = Id
											+ "_comment"
									action_Id = Id
											+ "_action"
											$("textarea[id='"
													+ comments_Id
													+ "']").prop('required',true);
									$("input:radio[name='"
											+ action_Id
											+ "']").prop('required',true);
									
								});
								
						
						
						
						//$('#chk').click(function() {
							 // var index = $('table').index(this);
					//	});
						
						$("#submit")
								.click(
										function() {
											var comments_Id = [];
											var action_Id = []
											var social_Id = []
											var i = 0;
											sizechkbox = $(
													'input:checkbox[name=chk]:checked')
													.size()

											if (sizechkbox == 0)
												alert("Please select the check box to submit the requests")

											$(
													"input:checkbox[name=chk]:checked")
													.each(
															function() {
																var Id = $(this)
																		.attr(
																				"id");
															//	if($("input:radio[name='"+Id+"']").is(":checked")==false)
																	// $("input:radio[name='"
																			//	+ action_Id[j]
																			//	+ "']").prop('required',true);
																	// $("textarea[id='"
																		//	+ comments_Id[j]
																	//	+ "']").prop('required',true);
																	
																social_Id[i] = Id
																comments_Id[i] = Id
																		+ "_comment"
																action_Id[i] = Id
																		+ "_action"
																i = i + 1
															});

											for (j = 0; j < sizechkbox; j++) {
												comments_Id[j] = $(
														"textarea[id='"
																+ comments_Id[j]
																+ "']").val()
												action_Id[j] = $(
														"input:radio[name='"
																+ action_Id[j]
																+ "']:checked")
														.val()
											}

											if (sizechkbox > 0) {
												$.post('requestAction', {
													"socialId" : social_Id,
													"actions" : action_Id,
													"comments" : comments_Id
												}, function() {
													 window.location.reload();
												});

											}
										});
						
					});
</script>
</head>
<body>
	<div class="page-header">
		<div class="container text-center">
			<h1>Approval Page</h1>
		</div>
	</div>
	<div class="container">
		<p>You have below registration request pending for approval.
			Please Approve or Reject</p>
		<table class="table table-striped">
			<thead>
				<tr>
					<th></th>
					<th>Requester</th>
					<th>Action</th>
					<th>Comment</th>
				</tr>
			</thead>
			<tbody>
				<%
					DBConnection dbconn = new DBConnection();
							String serviceProvider = "";

							ArrayList<ApprovalInfo> approvalList = dbconn
									.getPendingApprovalRequests();
							int i = 0;
							for (ApprovalInfo approvalObj : approvalList) {

								String name = approvalObj.getFirstName() + " "
										+ approvalObj.getLastName();
								String requestStatus = "requestStatus_"
										+ approvalObj.getSocial_ID();

								String commentId = approvalObj.getSocialEmail() + "_comment";
								String actionId = approvalObj.getSocialEmail() + "_action";
								if (approvalObj.getSocial_ID().startsWith("az_"))
									serviceProvider = "Amazon";
								else if (approvalObj.getSocial_ID().startsWith("fb_"))
									serviceProvider = "Facebook";
								else if (approvalObj.getSocial_ID().startsWith("ln_"))
									serviceProvider = "LinkedIn";
				%>

				<tr>
					<td><div class="checkbox">
							<input type="checkbox" name="chk"
								id="<%=approvalObj.getSocialEmail()%>">
						</div></td>
					<td><%=name%><br /><%=approvalObj.getSocialEmail()%><br /><%=serviceProvider%></td>
					<td><div class="radio">
							<label><input type="radio" id="<%=actionId%>"
								name="<%=actionId%>" value="approved">Approve</label>
						</div>
						<div class="radio">
							<label><input type="radio" id="<%=actionId%>"
								name="<%=actionId%>" value="rejected">Reject</label>
						</div></td>
					<td><div class="form-group">
							<textarea class="form-control" rows="2" id="<%=commentId%>"></textarea>
						</div></td>
				</tr>
				<%
					i++;
					}
				%>
			</tbody>
		</table>
		<button type="button" id="submit" class="btn btn-primary btn-md">Submit</button>
		<button type="button" class="btn btn-primary btn-md">Remind
			me Later</button>
	</div>
	<footer class="container-fluid text-center" style="color: #443D5F">
	<p>Copyright Information</p>
	</footer>
</body>
</html>

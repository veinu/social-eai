<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Continue Without Registration?</title>
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
		$("#withoutRegistration").change(function() {
			if ($("#withoutRegistration").is(":checked")) {
				alert("looks like you want to continue...");			
				}
			else{
				alert("looks like you don't want to use our services..");
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
	String firstName="Kuldeep";
	%>
	<form action=""
		method="post">
		<div class="page-header">
			<div class="container text-center">
				<h1>Continue Without Registration ?</h1>
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
							Hello, <%=firstName%> your registration request is pending for approval</p>
							<br />
							<div class="checkbox" style="font-size: 18px;">
								<label><input id="withoutRegistration" type="checkbox"
									name="withoutRegistration"
									style="height: 16px; width: 16px; border: 2px solid #999; color: #fff; background-color: #33b;">Do
									you want to continue without registration?
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


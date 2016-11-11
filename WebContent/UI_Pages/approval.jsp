<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Approval</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="CSS/Custom.css">
<!-- <link rel="stylesheet" href="CSS/bootstrap.min.css">
<script src="JS/jquery-1.12.2.min.js"></script>
<script src="JS/bootstrap.min.js"></script> -->
<script>
	$(document).ready(function() {

		$("#RemindButton").click(function() {
			alert("User has clicked Remind me later");
		});
		
		$("#SubmitButtton").click(function() {
			alert("User has clicked Submit");
		});

	

		var NameVariable = 'Veinu';
		var EmailVariable = 'veinu@hotmail.com';
		var ProviderVariable = 'facebook';
		$('#radio_text span').text(NameVariable + ' ' + EmailVariable + ' ' + ProviderVariable + '       ');

	});
</script>
</head>
<body>
<%

%>
	<div class="page-header">
		<div class="container text-center">
			<h1>Approval Page</h1>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<div class="panel panel-primary">
					<div class="panel-body" style="text-align: center; color: #443D5F">
						<br />
						<p style="font-size: 18px;" id="sometext">
							You have below registration pending for your approval.<br />
							Please approve or reject
						</p>

						<div id="radio_text" class="panel-body" style="color: #443D5F;">
							<p style="font-size: 18px;" id="sometext">
								<span></span> &nbsp; &nbsp; <label
									class="radio-inline"> <input type="radio"
									name="optradio">Approve
								</label> <label class="radio-inline"> <input type="radio"
									name="optradio">Reject
								</label>
							</p>
							<br />
						</div>
						<div>

							<button id="RemindButton" type="submit" class="btn btn-default"
								style="font-weight: bold; color: #443D5F">Remind me
								later</button>
							&nbsp;
							<button id="SubmitButtton" type="submit" class="btn btn-default"
								style="font-weight: bold; color: #443D5F">Submit</button>
						</div>
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

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Demo Page</title>
</head>

<script id="amazon-login-sdk"
	src="https://api-cdn.amazon.com/sdk/login1.js" type="text/javascript"
	async></script>
<script id="linkedin-login-sdk"
	src="https://platform.linkedin.com/in.js" type="text/javascript" async></script>
<script id="facebook-login-sdk"
	src="https://connect.facebook.net/en_US/all.js" type="text/javascript"
	async></script>


<script type="text/javascript">
	function SocialLogout(provider) {
		if (provider != null) {
			var providerUpperCase = provider.toUpperCase();
			if (providerUpperCase == "FACEBOOK") {
				FB.logout();
			} else if (providerUpperCase == "LINKEDIN") {
				IN.User.logout();
			} else if (providerUpperCase == "AMAZON") {
				amazon.Login.logout();
			}
		}
	}

	function AZLogout() {
		amazon.Login.logout();
	}

	function LILogout() {
		IN.User.logout();
	}

	function FBLogout() {
		FB.logout();
	}
</script>

<body>
	<br>
	<br>
	<br>

	<!-- 	<a href="Facebook"
		onclick="javascript:void window.open('https://10.51.231.5:9443/wps/socialeai/sociallogin?socialProviderName=facebook&sourceUrl=https://10.51.231.5:9443/wps/portal/home/&targeturl=/wps/myportal/home/myportal/myaccount?deeptarget=/sma/ESCBP/EscMyBillsAndPayment.aspx&locale=en&operation=login','1425386002082','width=700,height=500,left=300,top=100,toolbar=0,menubar=0,location=1,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">Facebook</a>
	<br>

	<a href="LinkedIn"
		onclick="javascript:void window.open('https://10.51.231.5:9443/wps/socialeai/sociallogin?socialProviderName=LinkedIn&sourceUrl=https://10.51.231.5:9443/wps/portal/home/&targeturl=/wps/myportal/home/myportal/myaccount?deeptarget=/sma/ESCBP/EscMyBillsAndPayment.aspx&locale=en&operation=login','1425386002082','width=700,height=500,left=300,top=100,toolbar=0,menubar=0,location=1,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">LinkedIn</a>
	<br>

	<a href="Amazon"
		onclick="javascript:void window.open('https://10.51.231.5:9443/wps/socialeai/sociallogin?socialProviderName=Amazon&sourceUrl=https://10.51.231.5:9443/wps/portal/home/&targeturl=/wps/myportal/home/myportal/myaccount?deeptarget=/sma/ESCBP/EscMyBillsAndPayment.aspx&locale=en&operation=login','1425386002082','width=700,height=500,left=300,top=100,toolbar=0,menubar=0,location=1,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">Amazon</a>
 -->

	<a href="Facebook"
		onclick="javascript:void window.open('https://10.51.231.5:9443/wps/socialeai/sociallogin?socialProviderName=facebook&locale=en&operation=login','1425386002082','width=700,height=500,left=300,top=100,toolbar=0,menubar=0,location=1,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">Facebook</a>
	<br>

	<a href="LinkedIn"
		onclick="javascript:void window.open('https://10.51.231.5:9443/wps/socialeai/sociallogin?socialProviderName=LinkedIn&locale=en&operation=login','1425386002082','width=700,height=500,left=300,top=100,toolbar=0,menubar=0,location=1,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">LinkedIn</a>
	<br>

	<a href="Amazon"
		onclick="javascript:void window.open('https://10.51.231.5:9443/wps/socialeai/sociallogin?socialProviderName=Amazon&locale=en&operation=login','1425386002082','width=700,height=500,left=300,top=100,toolbar=0,menubar=0,location=1,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">Amazon</a>
	<br>
	<br>
	<br>

	<a href="" onclick="FBLogout();"> Facebook Logout</a>
	<br>

	<a href="" onclick="LILogout();"> LinkedIn Logout</a>
	<br>

	<a href="" onclick="AZLogout();"> Amazon Logout</a>
	<br>






</body>
</html>
package com.customer.portal.common.framework;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectComponent {
	
	public static String getRedirectURL(HttpServletRequest request,
			HttpServletResponse response,
			HashMap<String, String> socialProfileData,
			HashMap<String, String> userLdapData,
			String errorCode,
			String responseCode) {		
		
		return "https://ptl03942.persistent.co.in/tomcat/examples/headers.jsp";
	}
}

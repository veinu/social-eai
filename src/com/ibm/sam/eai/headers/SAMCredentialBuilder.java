package com.ibm.sam.eai.headers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SAMCredentialBuilder {
	public void setEAIHeaders(Object principal, HttpServletResponse resp);
	public void setEAIHeaders(HttpServletResponse resp);
	public Object getPDPrincipal(HttpServletRequest req);
}

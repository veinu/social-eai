package com.ibm.sam.eai.social.model;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenInput {
	public final static String POST = "POST";
	public final static String GET = "GET";
	String httpMethod;
	String code;
	Map<String,String> parameters;
	public AccessTokenInput(){
		parameters = new HashMap<String,String>();
	}	
	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the parameters
	 */
	public Map<String,String> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameter(String name,String value) {
		this.parameters.put(name,value);
	}
	
}

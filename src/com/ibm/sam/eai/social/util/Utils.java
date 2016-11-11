package com.ibm.sam.eai.social.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.ibm.sam.eai.social.model.SocialProfileData;

public class Utils {
	private static final String className = Utils.class.getName();
	static org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
	public static String getAccessTokenFromHTMLResponse(String response) {
		String method ="getAccessTokenFromHTMLResponse";
		EAILogger.debug(className, className + ", " + method + "Input: response=" + response);
		try {	

			org.json.simple.JSONObject jObj = (org.json.simple.JSONObject) parser
					.parse(response);
			return (String) jObj.get("access_token");
		} catch (Exception e) {			
			EAILogger.error(className, className + ", " + method + ", Reading Access Token, " + e.getMessage(),e);
			String res = response;
		      try{
					String at = res.substring(res.indexOf("access_token=")+13,res.indexOf("&expires="));
					//System.out.println("access_token\t"+at);
					return at;
				}catch(Exception ex){
					//System.out.println("exception:::" + ex);					
					EAILogger.error(className, className + ", " + method + ", Reading Access Token, " + e.getMessage(),ex);
				}
				
		}
		return null;
	}
	public static String getJSONObject(SocialProfileData profileData){
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		obj.put("provider", profileData.getProvider());
		obj.put("id", profileData.getId());
		obj.put("email", profileData.getEmail());
		obj.put("firstName", profileData.getFirstName());
		obj.put("lastName", profileData.getLastName());
		obj.put("origJSON",profileData.getOrigJSON());
		return obj.toString();
		
	}
	
	public static String getStackTraceString(Exception e){
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		String s = writer.toString();
		return s;
	}
	
	public static void main(String a[]){
		
		SocialProfileData pData = new SocialProfileData();
		pData.setId("ran");
		pData.setEmail("email");
		pData.setOrigJSON("{\"email\":\"jsmith@gmail.com\",\"firstName\":\"John”, “lastName”,”Smith\",\" id\":\"amzn1.account.AGGC4KPUSGZ3ALKADESCILVG6PWQ\"}");
		System.out.println(getJSONObject(pData));
	}
}

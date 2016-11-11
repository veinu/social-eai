package com.ibm.sam.eai.social.provider.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.simple.parser.ParseException;

import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.provider.SocialLoginProvider;
import com.ibm.sam.eai.social.resources.ErrorCodes;
import com.ibm.sam.eai.social.util.Constants;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.Utils;
public class LinkedinLoginProvider extends SocialLoginProvider {
	private final String className = LinkedinLoginProvider.class.getName();

	
	public LinkedinLoginProvider(){
		setAppId();
		setAppSecret();
		setRedirectUrl();
		setBaseUrl();
		setAccessCodeUrl();
		setAccessTokenUrl();
		setProfileDataUrl();
		EAILogger.info(className, className + ", Configured Linkedin Login Provider = "+ toString());
	}
	public void setProviderType(){
		super.setProviderType("linkedin");
	}
	
	@Override
	public void setAppId() {
		super.setAppId(getProperty(Constants.LINKEDIN_APPID));
	}

	@Override
	public void setAppSecret() {
		super.setAppSecret(getProperty(Constants.LINKEDIN_CLIENTSECRET));
	}

	@Override
	public void setRedirectUrl() {
		super.setRedirectUrl(getProperty(Constants.LINKEDIN_REDIRECTURL));
	}

	@Override
	public void setBaseUrl() {
		super.setBaseUrl(getProperty(Constants.LINKEDIN_URL));

	}

	@Override
	public void setAccessCodeUrl() {
		String method = "setAccessCodeUrl"; 
		//System.out.println(Constants.LINKEDIN_ACCESSCODE_URL+": > "+getProperty(Constants.LINKEDIN_ACCESSCODE_URL)+"<");
		EAILogger.debug(className, className + ", " + method + ", " + Constants.LINKEDIN_ACCESSCODE_URL+": > "+getProperty(Constants.LINKEDIN_ACCESSCODE_URL)+"<");
		if(getProperty(Constants.LINKEDIN_ACCESSCODE_URL)!=null){
			super.setAccessCodeUrl(getProperty(Constants.LINKEDIN_ACCESSCODE_URL));	
		}else{
			super.setAccessCodeUrl("https://www.linkedin.com/uas/oauth2/authorization?response_type=code&state=DCEeFWf45A53sdfKef424&scope=r_emailaddress");
		}

	}

	@Override
	public void setAccessTokenUrl() {
		super.setAccessTokenUrl(getProperty(Constants.LINKEDIN_ACCESSTOKEN_URL));

	}

	@Override
	public void setProfileDataUrl() {
		super.setProfileDataUrl(getProperty(Constants.LINKEDIN_PROFILEDATA_URL));

	}


	@Override
	public String toString() {
		return "LinkedinLoginProvider [getAppId()=" + getAppId()
				+ ", getAppSecret()=" + getAppSecret() + ", getRedirectUrl()="
				+ getRedirectUrl() + ", getBaseUrl()=" + getBaseUrl()
				+ ", getAccessCodeUrl()=" + getAccessCodeUrl()
				+ ", getAccessTokenUrl()=" + getAccessTokenUrl()
				+ ", getProfileDataUrl()=" + getProfileDataUrl() + "]";
	}


	@Override
	public SocialProfileData getSocialProfileData(String userData) {
		String method ="getSocialProfileData";
		EAILogger.debug(className, className + ", " + method + ", Input: userData=" + userData);
		
		org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
		org.json.simple.JSONObject obj;
		SocialProfileData userProfileData = new SocialProfileData();
		try {
			obj = (org.json.simple.JSONObject)parser.parse(userData);
		    String mail = (String)obj.get("emailAddress");
		    String id = (String)obj.get("id");
		    String first_name = (String)obj.get("firstName");
		    String last_name = (String)obj.get("lastName");
			
			userProfileData.setEmail(mail);
			userProfileData.setFirstName(first_name);
			userProfileData.setId(id);
			userProfileData.setLastName(last_name);
			userProfileData.setProvider("linkedin");
			userProfileData.setOrigJSON(userData);
			EAILogger.debug(className, className + ", " + method + ", userProfileData = " + userProfileData.toString());
			return userProfileData;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}catch (Exception e1) {
			// TODO Auto-generated catch block
			EAILogger.error(className, className + ", " + method + ", " + e1.getMessage(),e1);
			e1.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e1));
		}
		return userProfileData;
	}
	@Override
	public void setAccessTokenHTTPMethod() {
		super.setHTTPMethod("AccessToken",HTTP_METHOD_GET);
		
	}
	@Override
	public String getAccessToken(String authCode) throws SocialEAIException{
		String method ="getAccessToken";
		EAILogger.debug(className, className + ", " + method);
		IOException e =null;
		for(int i=0;i<3;i++){
			try{
					EAILogger.info(className, className + ", " + method + ", Getting AccessToken:"+getAccessTokenUrl(authCode));
					HashMap<String,String> requestProperties = new HashMap<String,String>();
					requestProperties.put("Accept-Language", "en-US,en;q=0.5");
					String postParameters = super.getAccessTokenParameters("grant_type=authorization_code&client_id=&client_secret=&redirect_uri=&code=",authCode);
					String out = getHttpResponse(getAccessTokenUrl(),HTTP_METHOD_POST,postParameters,requestProperties);
					return Utils.getAccessTokenFromHTMLResponse(out);
				}catch( IOException ex){
					EAILogger.error(className, className + ", " + method + ", Count: "+i + "Caught Ex"+ex + ex.getMessage() + " and Cause:"+ex.getCause(),ex);
					EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
					e =ex;
					try {
						Thread.sleep(500);
					} catch (InterruptedException ignore) {
						ignore.printStackTrace();
						EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ignore));
					}
				}
			}
			
			return null;
	}
	@Override
	public String getUserData(String accessToken) throws SocialEAIException {
		String method ="getUserData";
		EAILogger.debug(className, className + ", " + method + ", Input: accessToken=" + accessToken);
		if(accessToken==null){
			throw new SocialEAIException(ErrorCodes.SOCEAI105E, ErrorCodes.SOCEAI105E_DESC);
		}
		IOException e =null;
		for(int i=0;i<3;i++){
			try{
				HashMap<String,String> requestProperties = new HashMap<String,String>();
				requestProperties.put("Authorization", "Bearer "+accessToken);
				String url = getProfileDataUrl(URLEncoder.encode(accessToken, "UTF-8"));
				return getHttpResponse(url,requestProperties);
				
			}catch(IOException ex){
				EAILogger.error(className, className + ", " + method + ", Count: "+i + "Caught Ex"+ex + ex.getMessage() + " and Cause:"+ex.getCause(),ex);
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
				e =ex;
				try {
					Thread.sleep(500);
				} catch (InterruptedException ignore) {
					ignore.printStackTrace();
					EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ignore));
				}
			}
		}
		//String msg [] = {e.getMessage()};
		//throw new SocialEAIException(ErrorCodes.SOCEAI104E,msg);
		throw new SocialEAIException(ErrorCodes.SOCEAI104E, ErrorCodes.SOCEAI104E_DESC);

	}



}

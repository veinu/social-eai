package com.ibm.sam.eai.social.provider.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.simple.parser.ParseException;

import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.model.SocialURLData;
import com.ibm.sam.eai.social.provider.SocialLoginProvider;
import com.ibm.sam.eai.social.resources.ErrorCodes;
import com.ibm.sam.eai.social.util.Constants;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.Utils;

public class FacebookLoginProvider extends SocialLoginProvider {


	private static final String className = FacebookLoginProvider.class.getName();
	String clientKey;
	String clientSecret;
	String redirectUrl;
	


	public FacebookLoginProvider(){
		setAppId();
		setAppSecret();
		setRedirectUrl();
		setBaseUrl();
		setAccessCodeUrl();
		setAccessTokenUrl();
		setProfileDataUrl();
		EAILogger.info(className, className + ",Configured Facebook Login Provider "+ toString());
	}
	
	@Override
	public String toString() {
		return "FacebookLoginProvider [clientKey=" + clientKey
				+ ", clientSecret=" + clientSecret + ", redirectUrl="
				+ redirectUrl + ", getAppId()=" + getAppId()
				+ ", getAppSecret()=" + getAppSecret() + ", getRedirectUrl()="
				+ getRedirectUrl() + ", getBaseUrl()=" + getBaseUrl()
				+ ", getAccessCodeUrl()=" + getAccessCodeUrl()
				+ ", getAccessTokenUrl()=" + getAccessTokenUrl()
				+ ", getProfileDataUrl()=" + getProfileDataUrl() + "]";
	}
	public void setProviderType(){
		super.setProviderType("facebook");
	}
	public void setSocialURL(SocialURLData socialURL) {
		
		SocialURLData socialURLData = new SocialURLData(clientKey,clientSecret);
		try {
			socialURLData.setBaseUrl(getProperty(Constants.FACEBOOK_URL));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		try {
			socialURLData.setProfileUrl(getProperty(Constants.FACEBOOK_URL));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
	
		try {
			socialURLData.setAccessCodeUrl(getProperty(Constants.FACEBOOK_ACCESSCODE_URL));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		try {
			socialURLData.setTarget(redirectUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		
	}

	@Override
	public void setAppId() {
		super.setAppId(getProperty(Constants.FACEBOOK_APPID));
	}

	@Override
	public void setAppSecret() {
		super.setAppSecret(getProperty(Constants.FACEBOOK_CLIENTSECRET));
	}

	@Override
	public void setRedirectUrl() {
		super.setRedirectUrl(getProperty(Constants.FACEBOOK_REDIRECTURL));
	}

	@Override
	public void setBaseUrl() {
		super.setBaseUrl(getProperty(Constants.FACEBOOK_URL));
	}

	@Override
	public void setAccessCodeUrl() {
		super.setAccessCodeUrl(getProperty(Constants.FACEBOOK_ACCESSCODE_URL));
	}

	@Override
	public void setAccessTokenUrl() {
		super.setAccessTokenUrl(getProperty(Constants.FACEBOOK_ACCESSTOKEN_URL));
	}

	@Override
	public void setProfileDataUrl() {
		super.setProfileDataUrl(getProperty(Constants.FACEBOOK_PROFILEDATA_URL));
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
			String name = (String)obj.get("name");
			String mail = (String)obj.get("email");
			String id = (String)obj.get("id");
			String first_name = (String)obj.get("first_name");
			String last_name = (String)obj.get("last_name");
			String  gender = (String)obj.get("gender");
			
			userProfileData.setEmail(mail);
			userProfileData.setFirstName(first_name);
			userProfileData.setId(id);
			userProfileData.setLastName(last_name);
			userProfileData.setProvider("facebook");
			userProfileData.setOrigJSON(userData);
			EAILogger.debug(className, className + ", " + method + ", userProfileData = " + userProfileData.toString());
			return userProfileData;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			EAILogger.error(className, className + ", " + method + ", " + e1.getMessage(),e1);
			e1.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e1));
		}
		return userProfileData;
	}
	@Override
	public void setAccessTokenHTTPMethod() {
		super.setHTTPMethod("AccessToken","GET");
		
	}

	@Override
	public String getUserData(String accessToken) throws SocialEAIException {
		String method ="getUserData";
		EAILogger.debug(className, className + ", " + method);
		if(accessToken==null){
			throw new SocialEAIException(ErrorCodes.SOCEAI105E, ErrorCodes.SOCEAI105E_DESC);
		}
		IOException e =null;
		for(int i=0;i<2;i++){
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

	@Override
	public String getAccessToken(String authCode) throws SocialEAIException {
		String method ="getAccessToken";
		EAILogger.debug(className, className + ", " + method + ", Input: authCode=" + authCode);
		IOException e =null;
		for(int i=0;i<2;i++){
			try{
				EAILogger.info(className, "Getting AccessToken: "+getAccessTokenUrl(authCode));
				String out = getHttpResponse(getAccessTokenUrl(authCode),null);
				return Utils.getAccessTokenFromHTMLResponse(out);
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
		String msg [] = {e.getMessage()};
		throw new SocialEAIException(ErrorCodes.SOCEAI105E,msg);
	}




}

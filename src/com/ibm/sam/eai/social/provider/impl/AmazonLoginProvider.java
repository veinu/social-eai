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

public class AmazonLoginProvider extends SocialLoginProvider {


	
	private static final String className = AmazonLoginProvider.class.getName();

	public AmazonLoginProvider(){
		super();
		setAppId();
		setAppSecret();
		setRedirectUrl();
		setBaseUrl();
		setAccessCodeUrl();
		setAccessTokenUrl();
		setProfileDataUrl();
		EAILogger.info(className, className + ", Configured Amazon Login Provider "+toString());
	}
	
	@Override
	public String toString() {
		return "AmazonLoginProvider [getAppId()=" + getAppId()
				+ ", getAppSecret()=" + getAppSecret() + ", getRedirectUrl()="
				+ getRedirectUrl() + ", getBaseUrl()=" + getBaseUrl()
				+ ", getAccessCodeUrl()=" + getAccessCodeUrl()
				+ ", getAccessTokenUrl()=" + getAccessTokenUrl()
				+ ", getProfileDataUrl()=" + getProfileDataUrl() + "]";
	}

	public void setProviderType(){
		super.setProviderType("amazon");
	}
	
	@Override
	public void setAppId() {
		super.setAppId(getProperty(Constants.AMAZON_APPID));
	}

	@Override
	public void setAppSecret() {
		super.setAppSecret(getProperty(Constants.AMAZON_CLIENTSECRET));
	}

	@Override
	public void setRedirectUrl() {
		super.setRedirectUrl(getProperty(Constants.AMAZON_REDIRECTURL));
	}

	@Override
	public void setBaseUrl() {
		super.setBaseUrl(getProperty(Constants.AMAZON_URL));

	}

	@Override
	public void setAccessCodeUrl() {
		System.out.println("Constants.AMAZON_ACCESSTOKEN_URL: > "+getProperty(Constants.AMAZON_ACCESSTOKEN_URL)+"<");
		if(getProperty(Constants.AMAZON_ACCESSCODE_URL)!=null){
			super.setAccessCodeUrl(getProperty(Constants.AMAZON_ACCESSCODE_URL));	
		}else{
			super.setAccessCodeUrl("https://www.amazon.com/ap/oa?scope=profile&response_type=code");
		}
	}

	@Override
	public void setAccessTokenUrl() {
		super.setAccessTokenUrl(getProperty(Constants.AMAZON_ACCESSTOKEN_URL));	
	}

	@Override
	public void setProfileDataUrl() {
		super.setProfileDataUrl(getProperty(Constants.AMAZON_PROFILEDATA_URL));

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
			String id = (String)obj.get("user_id");
			String first_name = (String)obj.get("first_name");
			String last_name = (String)obj.get("last_name");
			if(first_name==null && last_name==null && name!=null){
				String[] v = name.split(" "); 
				if(v!=null&& v.length>0){
					first_name = v[0];	
					if(v.length>1){					
						last_name = v[1];
					}
				}
				
			}
			
			userProfileData.setEmail(mail);
			userProfileData.setFirstName(first_name);
			userProfileData.setId(id);
			userProfileData.setLastName(last_name);
			userProfileData.setProvider("amazon");
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
		super.setHTTPMethod("AccessToken","POST");
		
	}
	public String getAccessToken(String authCode) throws SocialEAIException{
		String method ="getAccessToken";
		EAILogger.debug(className, className + ", " + method);
		IOException e =null;
		for(int i=0;i<3;i++){
			try{
				//System.out.println("AmazonLoginProvider's getAccessToken : AccessTokenUrl>>"+getAccessTokenUrl());
				EAILogger.info(className, "Getting AccessToken:"+getAccessTokenUrl(authCode));
				HashMap<String,String> requestProperties = new HashMap<String,String>();
				requestProperties.put("Accept-Language", "en-US,en;q=0.5");
				String postParameters = super.getAccessTokenParameters("grant_type=authorization_code&scope=profile&client_id=&client_secret=&redirect_uri=&code=",authCode);
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

		EAILogger.info(className, "Getting ProfileData");
		if(accessToken==null){
			throw new SocialEAIException(ErrorCodes.SOCEAI105E, ErrorCodes.SOCEAI105E_DESC);
		}
		IOException e =null;
		for(int i=0;i<3;i++){
			try{
				String url = getProfileDataUrl(URLEncoder.encode(accessToken, "UTF-8"));
				EAILogger.debug(className, "Getting ProfileData, " + url);
				HashMap<String,String> requestProperties = new HashMap<String,String>();
				requestProperties.put("Authorization", "Bearer "+accessToken);
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

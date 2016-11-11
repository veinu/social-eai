package com.ibm.sam.eai.social.provider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.ProxyAuthenticator;
import com.ibm.sam.eai.social.model.ProxyDetails;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.resources.ErrorCodes;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;

public abstract class SocialLoginProvider {

	private static ProxyDetails proxyDetails;
	static final String className = SocialLoginProvider.class.getName();
	public static final String CLIENTSECRET = "CLIENTSECRET";
	public static final String APPID = "APPID";
	
	public static final String REDIRECTURL = "REDIRECT_URL";
	public static final String BASE_URL = "BASE_URL";
	public static final String ACCESSCODE_URL = "ACCESSCODE_URL";
	public static final String ACCESSTOKEN_URL = "ACCESSTOKEN_URL";
	public static final String PROFILEDATA_URL = "PROFILEDATA_URL";
	
	public static final String USE_DATA_POWER_URL = "USE_DATA_POWER_URL";
	public static final String DATA_POWER_URL = "DATA_POWER_URL";
	public static final String CUSTOM_HEADER = "CustomHeader";	

	public static final String PROXY_URL = "PROXY_URL";
	public static final String PROXY_PORT = "PROXY_PORT";
	public static final String PROXY_USERNAME = "PROXY_USERNAME";
	public static final String PROXY_PASSWORD = "PROXY_PASSWORD";
	private static Object ACCESSTOKEN_POST_METHOD = null;
	private static final Object ACCESSTOKEN_POST_PARAMS = null;
	
	protected static final String HTTP_METHOD_POST = "POST";
	protected static final String HTTP_METHOD_GET = "GET";
	private HashMap<String,String> environment = null;
	private String authCode;
	static PropertiesManager propertiesManager = null;
	private String providerType;
	static{
		try {
			propertiesManager = PropertiesManager.getInstance();
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, ", msg=" + e.getMessage() + ", code=" +e.getCode(), e);
		}
		if(getProperty(PROXY_URL)!=null ){
			proxyDetails = new ProxyDetails();
			proxyDetails.setProxyUrl(getProperty(PROXY_URL));
			if(getProperty(PROXY_USERNAME)!=null){
				proxyDetails.setProxyUserName(getProperty(PROXY_USERNAME));
			}
			if(getProperty(PROXY_PASSWORD)!=null){
				proxyDetails.setProxyPassword(getProperty(PROXY_PASSWORD));
			}
			EAILogger.info(className, "Configured Proxy Details:"+proxyDetails.toString());
			try {
				proxyDetails.setProxyPort(Integer
						.parseInt(getProperty(PROXY_PORT)));
			} catch (RuntimeException rte) {
				//rte.printStackTrace();
				proxyDetails.setProxyPort(8080);
				EAILogger.error(className,"Reading Proxy Detail", rte);
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(rte));
			}
		}

	}
	public SocialLoginProvider() {
		this.environment = new HashMap<String,String>();
	}
	public SocialLoginProvider(HashMap<String,String> environment){
		this.environment = environment;
	}
	public static String getProperty(String propertyName) {
		return propertiesManager.getProperty(propertyName);
	}
	void updateEnviornment(String key,String val){
		this.environment.put(key, val);
	}
	public abstract void setProviderType();
	public abstract void setAppId();
	public abstract void setAppSecret();
	public abstract void setRedirectUrl();
	public abstract void setBaseUrl();
	public abstract void setAccessCodeUrl();
	public abstract void setAccessTokenUrl();
	public abstract void setProfileDataUrl();
	
	public abstract void setAccessTokenHTTPMethod();
	public String getAppId(){
		return this.environment.get(APPID);
	}
	public String getAppSecret(){
		return environment.get(CLIENTSECRET);
	}
	public String getRedirectUrl(){
		return environment.get(REDIRECTURL);
	}
	public String getBaseUrl(){
		return environment.get(BASE_URL);
	}
	public String getAccessCodeUrl(){
		return environment.get(ACCESSCODE_URL);
	}
	public String getAccessTokenUrl(){
		return environment.get(ACCESSTOKEN_URL);
	}
	public String getProfileDataUrl(){
		return environment.get(PROFILEDATA_URL);
	}
	/**
	 * @return the proxyDetails
	 */
	public ProxyDetails getProxyDetails() {
		return proxyDetails;
	}
	public void setAuthCode(String authCode){
		this.authCode = authCode;
	}
	/*
	public String getAccessToken(String authCode)  throws SocialEAIException {
		IOException e =null;
		for(int i=0;i<3;i++){
			try{
				EAILogger.info(className, "Getting AccessToken: "+getAccessTokenUrl(authCode));
				URL obj = new URL(getAccessTokenUrl(authCode));
				HttpsURLConnection con = null;
				EAILogger.debug(className, "ProxyDetails :"+proxyDetails);
				if (proxyDetails != null && proxyDetails.getProxyUrl()!=null ) {
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
							proxyDetails.getProxyUrl(), proxyDetails.getProxyPort()));
					if(proxyDetails.getProxyUserName()!=null && proxyDetails.getProxyPassword()!=null ){
						Authenticator.setDefault(new ProxyAuthenticator(proxyDetails
								.getProxyUserName(), proxyDetails.getProxyPassword()));
					}
					con = (HttpsURLConnection) obj.openConnection(proxy);
				} else {
					con = (HttpsURLConnection) obj.openConnection();
				}
				
				// optional default is GET
				con.setRequestMethod("GET");
		 
				//add request header
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
			
				int responseCode = con.getResponseCode();
			
				System.out.println("Response Code : " + responseCode);
				StringBuffer out = new StringBuffer();
				if (responseCode == HttpsURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(
					        new InputStreamReader(con.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						out.append(inputLine);
					}
					in.close();			
				}

				con.disconnect();
			
				return Utils.getAccessTokenFromHTMLResponse(out.toString());
			}catch(IOException ex){
				ex.printStackTrace();
				System.out.println("Count: "+i + "Caught Ex : "+ ex.getMessage() + " and Cause:"+ex.getCause());
				e =ex;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		String msg [] = {e.getMessage()};
		throw new SocialEAIException(ErrorCodes.SOCEAI105E,msg);

	}
	*/


	protected String getAccessTokenPostParams(String authCode) throws SocialEAIException  {
		String params = environment.get(ACCESSTOKEN_POST_PARAMS);
		return getAccessTokenParameters(params,authCode);
	}
	public abstract String getUserData(String accessToken) throws SocialEAIException;
	public abstract String getAccessToken(String authCode)  throws SocialEAIException;
	/*public String getUserData(String accessToken) throws SocialEAIException {
		if(accessToken==null){
			throw new SocialEAIException(ErrorCodes.SOCEAI105E);
		}
		IOException e =null;
		for(int i=0;i<3;i++){
			try{
				//System.out.println("getUserData...getProfileDataUrl.. "+getProfileDataUrl());
				URL obj = new URL(getProfileDataUrl(URLEncoder.encode(accessToken, "UTF-8")));
				HttpsURLConnection con = null;
				if (proxyDetails != null && proxyDetails.getProxyUrl()!=null ) {
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
							proxyDetails.getProxyUrl(), proxyDetails.getProxyPort()));
					if(proxyDetails.getProxyUserName()!=null && proxyDetails.getProxyPassword()!=null ){
						Authenticator.setDefault(new ProxyAuthenticator(proxyDetails
								.getProxyUserName(), proxyDetails.getProxyPassword()));
					}
					con = (HttpsURLConnection) obj.openConnection(proxy);
				} else {
					con = (HttpsURLConnection) obj.openConnection();
				}
				con.setRequestMethod("GET");
				con.setRequestProperty("Authorization","Bearer "+accessToken);
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
			
				//System.out.println("before invoking.....");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer out = new StringBuffer();
		
				while ((inputLine = in.readLine()) != null) {
					out.append(inputLine);
				}
				in.close();
				con.disconnect();
				return out.toString();
			}catch(IOException ex){
				System.out.println("Count: "+i + "Caught Ex"+ex + ex.getMessage() + " and Cause:"+ex.getCause());
				e =ex;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		String msg [] = {e.getMessage()};
		throw new SocialEAIException(ErrorCodes.SOCEAI104E,msg);

	}
	*/
	public abstract SocialProfileData getSocialProfileData(String userData);
	
	protected void setAccessTokenUrl(String property) {
		updateEnviornment(ACCESSTOKEN_URL,property);
	}
	public void setAccessCodeUrl(String property) {
		updateEnviornment(ACCESSCODE_URL,property);
		
	}
	public void setProfileDataUrl(String property) {
		updateEnviornment(PROFILEDATA_URL,property);
		
	}
	public void setRedirectUrl(String property) {
		updateEnviornment(REDIRECTURL,property);
		
	}
	public void setAppSecret(String property) {
		updateEnviornment(CLIENTSECRET,property);
		
	}
	public void setAppId(String property) {
		updateEnviornment(APPID,property);
	
	}
	public void setBaseUrl(String property) {
		updateEnviornment(BASE_URL,property);
	}
	public String getAccessTokenUrl(String authCode) throws SocialEAIException{
		String rawUrl = getAccessTokenUrl();
		return getAccessTokenParameters(rawUrl,authCode);
	}
	protected String getAccessTokenParameters(String baseValue,String authCode) throws SocialEAIException{
		String method ="getAccessTokenParameters";
		EAILogger.debug(className, className + ", " + method + ", Input: baseValue=" + baseValue + ", authCode=" + authCode);
		String redirectUrl = getRedirectUrl();
		if(baseValue==null || authCode==null){
			String msg [] = {baseValue,authCode};
			throw new SocialEAIException(ErrorCodes.SOCEAI104E,msg);
		}
		String newValue = baseValue.replace("client_id=&", "client_id="+this.getAppId()+"&");
		newValue = newValue.replace("redirect_uri=&", "redirect_uri="+redirectUrl+"&");
		newValue = newValue.replace("client_secret=&", "client_secret="+this.getAppSecret()+"&");
		newValue = newValue.replace("code=", "code="+authCode);
		EAILogger.debug(className, className + ", " + method + ", newValue = " + newValue);
		return newValue;
		
	}
	public String getProfileDataUrl(String accessTokenCode){
		String rawUrl = getProfileDataUrl();
		String newUrl = rawUrl.replace("access_token=", "access_token="+accessTokenCode);
		return newUrl;
	}
	public void setHTTPMethod(String string, String string2) {
		if(string!=null && string.equalsIgnoreCase("accesstoken") && string2 !=null){
			ACCESSTOKEN_POST_METHOD = string2;
		}else{
			ACCESSTOKEN_POST_METHOD =HTTP_METHOD_GET;
		}
			
		
	}
	protected void setProviderType(String string) {
		this.providerType = string;
	}
	protected String getHttpResponse(String url,HashMap<String, String> requestProperties ) throws SocialEAIException,
	MalformedURLException, IOException, ProtocolException {
		return getHttpResponse(url,HTTP_METHOD_GET,null,requestProperties);
	}
	
	protected String getHttpResponse(String url,String methodName, String postParameters, HashMap<String, String> requestProperties ) throws SocialEAIException,
	MalformedURLException, IOException, ProtocolException {
	
		String method ="getHttpResponse";
		EAILogger.debug(className, className + ", " + method + ", Input: ,url = "+url+ ", methodName = "+methodName + ", postParameters = "
				+postParameters+ ", requestProperties = "+requestProperties);
		
		String useDPURL = PropertiesManager.getInstance().getProperty(USE_DATA_POWER_URL);	
		String DPURL = PropertiesManager.getInstance().getProperty(DATA_POWER_URL);
		EAILogger.debug(className, className + ", " + method + ",useDPURL = "+useDPURL);
		EAILogger.debug(className, className + ", " + method + ",DPURL = "+DPURL);
		
		URL obj = null;
		if(useDPURL!=null && useDPURL.equalsIgnoreCase("true")){
			obj = new URL(DPURL);
		}else{
			obj = new URL(url);
		}
		
		EAILogger.debug(className, className + ", " + method + ",ProxyDetails = "+proxyDetails);	
		
		try{
		
		HttpsURLConnection httpscon = null;
		HttpURLConnection httpcon = null;
		if (proxyDetails != null && proxyDetails.getProxyUrl()!=null ) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					proxyDetails.getProxyUrl(), proxyDetails.getProxyPort()));
			if(proxyDetails.getProxyUserName()!=null && proxyDetails.getProxyPassword()!=null ){
				Authenticator.setDefault(new ProxyAuthenticator(proxyDetails
						.getProxyUserName(), proxyDetails.getProxyPassword()));
			}
			if(obj.openConnection(proxy) instanceof HttpsURLConnection){
				httpscon = (HttpsURLConnection) obj.openConnection(proxy);
			}else{
				httpcon = (HttpURLConnection) obj.openConnection(proxy);
			}
			
		} else {			
			if(obj.openConnection() instanceof HttpsURLConnection){
				httpscon = (HttpsURLConnection) obj.openConnection();
			}else{
				httpcon = (HttpURLConnection) obj.openConnection();
			}
		}
		
		if(httpscon!=null){
			if(useDPURL!=null && useDPURL.equalsIgnoreCase("true")){
				httpscon.setRequestProperty(CUSTOM_HEADER, url);
			}
			
			if(requestProperties!=null){
				Set<String> keys = requestProperties.keySet();
				for(String key : keys){
					httpscon.setRequestProperty(key, requestProperties.get(key));
				}
			}
			//add request header
			httpscon.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			if(methodName==null || methodName.equalsIgnoreCase(HTTP_METHOD_GET)){
				// optional default is GET
				httpscon.setRequestMethod(HTTP_METHOD_GET);
				EAILogger.debug(className, className + ", " + method + ", httpscon = " + httpscon);
			}else{
				httpscon.setRequestMethod(HTTP_METHOD_POST);
				httpscon.setDoOutput(true);
				EAILogger.debug(className, className + ", " + method + ", httpscon = " + httpscon);
				DataOutputStream wr = new DataOutputStream(httpscon.getOutputStream());
				wr.writeBytes(postParameters);
				wr.flush();
				wr.close();
			}

			EAILogger.debug(className, className + ", " + method + ", Response Code = " + httpscon.getResponseCode());
			StringBuffer out = new StringBuffer();
			if ( HttpsURLConnection.HTTP_OK == httpscon.getResponseCode()) {
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(httpscon.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					out.append(inputLine);
				}
				in.close();			
			}
			EAILogger.debug(className, className + ", " + method + ", output = " + out.toString());
			httpscon.disconnect();
			return out.toString();
		}else if(httpcon!=null){
			if(useDPURL!=null && useDPURL.equalsIgnoreCase("true")){
				httpcon.setRequestProperty(CUSTOM_HEADER, url);
			}
			if(requestProperties!=null){
				Set<String> keys = requestProperties.keySet();
				for(String key : keys){
					httpcon.setRequestProperty(key, requestProperties.get(key));
				}
			}
			//add request header
			httpcon.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			if(methodName==null || methodName.equalsIgnoreCase(HTTP_METHOD_GET)){
				// optional default is GET
				httpcon.setRequestMethod(HTTP_METHOD_GET);
				EAILogger.debug(className, className + ", " + method + ", httpcon = " + httpcon);
			}else{
				httpcon.setRequestMethod(HTTP_METHOD_POST);
				httpcon.setDoOutput(true);
				EAILogger.debug(className, className + ", " + method + ", httpcon = " + httpcon);
				DataOutputStream wr = new DataOutputStream(httpcon.getOutputStream());
				wr.writeBytes(postParameters);
				wr.flush();
				wr.close();
			}

			EAILogger.debug(className, className + ", " + method + ", Response Code = " + httpcon.getResponseCode());
			StringBuffer out = new StringBuffer();
			if ( HttpsURLConnection.HTTP_OK == httpcon.getResponseCode()) {
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(httpcon.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					out.append(inputLine);
				}
				in.close();			
			}
			EAILogger.debug(className, className + ", " + method + ", output = " + out.toString());
			httpcon.disconnect();
			return out.toString();
		}else{
			return null;
		}
		}catch (Exception e) {
			e.printStackTrace();			
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			return null;
		}
		
	}
	
	protected String getHttpResponse_OLD(String url,String methodName, String postParameters, HashMap<String, String> requestProperties ) throws SocialEAIException,
	MalformedURLException, IOException, ProtocolException {
	
		String method ="getHttpResponse";
		
		URL obj = new URL(url);
		EAILogger.debug(className, className + ", " + method + ",ProxyDetails = "+proxyDetails);
		EAILogger.debug(className, className + ", " + method + ", Input: ,url = "+url+ ", methodName = "+methodName + ", postParameters = "
				+postParameters+ ", requestProperties = "+requestProperties);
		
		HttpsURLConnection con = null;
		if (proxyDetails != null && proxyDetails.getProxyUrl()!=null ) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					proxyDetails.getProxyUrl(), proxyDetails.getProxyPort()));
			if(proxyDetails.getProxyUserName()!=null && proxyDetails.getProxyPassword()!=null ){
				Authenticator.setDefault(new ProxyAuthenticator(proxyDetails
						.getProxyUserName(), proxyDetails.getProxyPassword()));
			}
			con = (HttpsURLConnection) obj.openConnection(proxy);
		} else {
			con = (HttpsURLConnection) obj.openConnection();
		}
		if(requestProperties!=null){
			Set<String> keys = requestProperties.keySet();
			for(String key : keys){
				con.setRequestProperty(key, requestProperties.get(key));
			}
		}
		//add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		if(methodName==null || methodName.equalsIgnoreCase(HTTP_METHOD_GET)){
			// optional default is GET
			con.setRequestMethod(HTTP_METHOD_GET);
		}else{
			con.setRequestMethod(HTTP_METHOD_POST);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParameters);
			wr.flush();
			wr.close();
		}

		EAILogger.debug(className, className + ", " + method + ", Response Code = " + con.getResponseCode());
		StringBuffer out = new StringBuffer();
		if ( HttpsURLConnection.HTTP_OK == con.getResponseCode()) {
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				out.append(inputLine);
			}
			in.close();			
		}
		EAILogger.debug(className, className + ", " + method + ", output = " + out.toString());
		con.disconnect();
		return out.toString();
	}
}

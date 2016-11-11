package com.ibm.sam.eai.social.provider;

import java.io.IOException;
import java.util.HashMap;

import com.ibm.sam.eai.social.provider.impl.AmazonLoginProvider;
import com.ibm.sam.eai.social.provider.impl.FacebookLoginProvider;
import com.ibm.sam.eai.social.provider.impl.LinkedinLoginProvider;
import com.ibm.sam.eai.social.util.PropertiesManager;

public class SocialLoginProviderFactory {

	static SocialLoginProviderFactory instance = null;
	static String classname = SocialLoginProviderFactory.class.getName();
	private SocialLoginProviderFactory(){
		
	}
	public static SocialLoginProviderFactory getInstance(){
		if(instance==null){
			synchronized(classname){
				instance = new SocialLoginProviderFactory();
			}
		}
		return instance;
	}
	public SocialLoginProvider getFacebookLoginProvider(){
		SocialLoginProvider socialLoginProvider = new FacebookLoginProvider();
		return socialLoginProvider;
	}
	public SocialLoginProvider getAmazonLoginProvider(){
		SocialLoginProvider socialLoginProvider = new AmazonLoginProvider();
		return socialLoginProvider;
	}
	public SocialLoginProvider getLinkedinLoginProvider(){
		SocialLoginProvider socialLoginProvider = new LinkedinLoginProvider();
		return socialLoginProvider;
	}
	public static void main(String a[]){
		/*SocialLoginProvider provider = getInstance().getAmazonLoginProvider();
		
		//SocialLoginProvider provider = getInstance().getFacebookLoginProvider();
		System.out.println("AppId>"+provider.getAppId());
		System.out.println("Secret>"+provider.getAppSecret());
		System.out.println("BaseUrl>"+provider.getBaseUrl());
		System.out.println("AccessTokenUrl>"+provider.getAccessTokenUrl());
		System.out.println("AccessCodeUrl>"+provider.getAccessCodeUrl());
		System.out.println("ProfileDataUrl>"+provider.getProfileDataUrl());
		
		System.out.println("AccessTokenUrl>"+provider.getAccessTokenUrl("RandomValu"));
		System.out.println("ProfileUrl>"+provider.getProfileDataUrl("XXC338123accessTokenCode"));
		
		
		try {
			System.out.println("AccessToken>"+provider.getAccessToken("XXC338123accessTokenCode"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		String res = "access_token=CAAJf7RpN1sUBANMojAxNARakvNwc6chtoKaWvDBNlp52uL98DtGatutGQd02VhHg1qJli6HvTvb39zZCXFZCQ2apS2XiWOsdIm6R46YRAUZAnov8l0bPeyxQsLOFcs1SHh4rgQrkb92cTe2fCHZASu5FrolRjOI81ymuZAh5MIviXZBroJEZCEz9d4ij7eO9OiCYqjpZC8J805tZBe942w90x&expires=5177511";
	      try{
	    	  
				String at = res.substring(res.indexOf("access_token=")+13,res.indexOf("&expires="));
				System.out.println("access_token\t"+at);
				
			}catch(Exception ex){
				System.out.println("exception:::" + ex);
			}
		
	}
}

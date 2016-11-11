package com.ibm.sam.eai.social.model;

public class ProfileDataInput extends AccessTokenInput{
	String profileDataURLStr;
	String accessToken;
	/**
	 * @return the profileDataURLStr
	 */
	public String getProfileDataURLStr() {
		return profileDataURLStr;
	}
	/**
	 * @param profileDataURLStr the profileDataURLStr to set
	 */
	public void setProfileDataURLStr(String profileDataURLStr) {
		this.profileDataURLStr = profileDataURLStr;
	}
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}

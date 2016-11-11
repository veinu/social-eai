package com.ibm.sam.eai.social.model;

import java.net.MalformedURLException;
import java.net.URL;

public class SocialURLData {
	private URL targetURL;
	private URL baseURL;
	private URL accessCodeURL;
	private URL accessTokenURL;
	private URL profileURL;

	private String target;
	private String base;
	private String accessToken;
	private String accessCode;
	private String profile;

	private String clientKey;
	private String clientSecret;

	public SocialURLData(String clientKey, String clientSecret) {
		this.clientKey = clientKey;
		this.clientSecret = clientSecret;

	}

	public SocialURLData(String clientKey, String clientSecret, String baseUrl,
			String accessCodeUrl, String accessTokenUrl, String profileDataUrl)
			throws MalformedURLException {
		this.clientKey = clientKey;
		this.clientSecret = clientSecret;
		setBaseUrl(baseUrl);
		setAccessCodeUrl(accessCodeUrl);
		setAccessTokenUrl(accessTokenUrl);
		setProfileUrl(profileDataUrl);
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 * @throws MalformedURLException
	 */
	public void setTarget(String target) throws MalformedURLException {
		this.target = target;
		this.targetURL = new URL(this.target);
	}

	/**
	 * @return the base
	 */
	public String getBase() {
		return base;
	}

	/**
	 * @param base
	 *            the base to set
	 * @throws MalformedURLException
	 */
	public void setBaseUrl(String base) throws MalformedURLException {
		this.base = base;
		this.baseURL = new URL(this.base);
	}

	/**
	 * @return the accessCode
	 */
	public String getAccessCode() {
		return accessCode;
	}

	/**
	 * @param accessCode
	 *            the accessCode to set
	 * @throws MalformedURLException
	 */
	public void setAccessCodeUrl(String accessCode)
			throws MalformedURLException {
		this.accessCode = accessCode;
		this.accessCodeURL = new URL(this.accessCode);
	}

	/**
	 * @param tokenCode
	 *            the tokenCode to set
	 */
	public void setAccessTokenUrl(String tokenCode)
			throws MalformedURLException {
		this.accessToken = tokenCode;
		this.accessTokenURL = new URL(this.accessToken);
	}

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * @param profile
	 *            the profile to set
	 * @throws MalformedURLException
	 */
	public void setProfileUrl(String profile) throws MalformedURLException {
		this.profile = profile;
		this.profileURL = new URL(this.profile);
	}

	public URL getTargetURL() {
		return targetURL;
	}

	public URL getBaseURL() {
		return baseURL;
	}

	public URL getAccessTokenURL() {
		return accessTokenURL;
	}

	public URL getAccessCodeURL() {
		return accessCodeURL;
	}

	public URL getProfileURL() {
		return profileURL;
	}

	/**
	 * @return the clientKey
	 */
	public String getClientKey() {
		return clientKey;
	}

	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

}

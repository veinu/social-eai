package com.ibm.sam.eai.social.model;

public class ProxyDetails {
	public String getProxyUrl() {
		return proxyUrl;
	}
	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}
	public int getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	public String getProxyUserName() {
		return proxyUserName;
	}
	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}
	public String getProxyPassword() {
		return proxyPassword;
	}
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	String proxyUrl;
	int proxyPort;
	String proxyUserName;
	String proxyPassword;
	@Override
	public String toString() {
		return "ProxyDetails [proxyUrl=" + proxyUrl + ", proxyPort="
				+ proxyPort + ", proxyUserName=" + proxyUserName + "]";
	}

	
	
}

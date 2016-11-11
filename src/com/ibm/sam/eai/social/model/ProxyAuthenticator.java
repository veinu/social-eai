package com.ibm.sam.eai.social.model;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator extends Authenticator {
		String m_username = null;
		String m_password = null;
		
		public ProxyAuthenticator(String uName, String pwd){
			m_username = uName;
			m_password = pwd;
			
		}
		protected PasswordAuthentication getPasswordAuthentication() {
			String prompt = getRequestingPrompt();
			String hostname = getRequestingHost();
			InetAddress ipaddr = getRequestingSite();
			int port = getRequestingPort();
			return new PasswordAuthentication(m_username, m_password.toCharArray());
		}
}

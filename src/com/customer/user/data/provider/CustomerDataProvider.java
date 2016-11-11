package com.customer.user.data.provider;

import java.util.ArrayList;
import java.util.List;

import com.customer.user.data.model.User;
import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.servlets.SocialLoginServlet;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;

public abstract class CustomerDataProvider {
	String m_mailFilter;
	String m_base = "";
	String m_returnAttrList ;
	ArrayList<String> m_returnAttributes;
	
	private static final String className = CustomerDataProvider.class.getName();
	
	public CustomerDataProvider() throws SocialEAIException{
		
		try {
			m_returnAttrList = PropertiesManager.getInstance().getProperty(com.ibm.sam.eai.ldap.impl.LdapDaoImpl.LDAP_RETURN_ATTRIBUTES);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			throw e;
		}
		try {
			m_base = PropertiesManager.getInstance().getProperty(com.ibm.sam.eai.ldap.impl.LdapDaoImpl.SEACH_BASE);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			m_base = "";
			throw e;
		}
		try {
			m_mailFilter = PropertiesManager.getInstance().getProperty(com.ibm.sam.eai.ldap.impl.LdapDaoImpl.MAIL_FILTER);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			throw e;
		}
		if(m_returnAttrList!=null){
			String[] tmp = m_returnAttrList.split(",");
			m_returnAttributes = new ArrayList<String>();
			for(int i=0;i<tmp.length;i++){
				m_returnAttributes.add(tmp[i]);
			}
		}

		if(m_mailFilter==null){
			m_mailFilter = "(mail=%mail)";
		}
	}
	protected String getSearchBase(){
		return this.m_base;
	}
	protected ArrayList<String> getReturnAttributes(){
		return this.m_returnAttributes;
	}
	protected String getMailFilter(){
		return this.m_mailFilter;
	}
	public abstract User getUser(String email) throws SocialEAIException;
	public abstract User getUser(SocialProfileData profileData) throws SocialEAIException;
	public abstract User getUser_SocialIdExist(SocialProfileData profileData) throws SocialEAIException;
	public abstract User getNewlyCreatedUser(String userid) throws SocialEAIException;
	public abstract User getUser_MatchedPrimaryEmailOfAnyExistingUser(SocialProfileData profileData, boolean isAssociatedOnly) throws SocialEAIException;
	public abstract boolean updateLastAccessTime(String email) throws SocialEAIException;
	public abstract List<String> getUserGroupList(String email) throws SocialEAIException;
}

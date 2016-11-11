package com.customer.user.data.provider.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.customer.user.data.model.User;
import com.customer.user.data.provider.CustomerDataProvider;
import com.ibm.sam.eai.ldap.impl.LdapDaoImpl;
import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.provider.SocialLoginProvider;
import com.ibm.sam.eai.social.util.Constants;
import com.ibm.sam.eai.social.util.DateUtil;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;

public class CustomerDataProviderImpl extends  CustomerDataProvider {

	private static final String className = CustomerDataProviderImpl.class.getName();
	
	public static final String SAM_LDAP_ATTRIBUTE_USER_STATUS = "sam.ldap.attribute.user-status";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID = "sam.ldap.attribute.user-social-id";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL = "sam.ldap.attribute.user-social-email";
	public static final String SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL = "sam.ldap.attribute.user-primary-email";	


	public CustomerDataProviderImpl()  throws SocialEAIException{
		super();
	}

	@Override
	public User getUser(String email) throws SocialEAIException {
		String method ="getUser";
		
		User customerUser = new User();
		try{
			com.ibm.sam.eai.ldap.impl.LdapDaoImpl ldapDao = new com.ibm.sam.eai.ldap.impl.LdapDaoImpl(
					com.ibm.sam.eai.ldap.impl.LdapConnectionManager
							.getInstance().getConnection());
			EAILogger.debug(className, className + ", " + method + ", Input: email = "+email);
			String mailFilter = getMailFilter();
			mailFilter = mailFilter.replaceAll("%mail", email);
			
			EAILogger.debug(className, className + ", " + method + ", mailFilter = "+mailFilter);
			EAILogger.debug(className, className + ", " + method + ", SearchBase = "+getSearchBase());
			EAILogger.debug(className, className + ", " + method + ", ReturnAttributes = " +getReturnAttributes());
			ArrayList<com.ibm.sam.eai.social.model.LdapData> ldapObjs = ldapDao
					.getLdapObjectByFilter(getSearchBase(), mailFilter,
							getReturnAttributes());
			com.ibm.sam.eai.social.model.LdapData ldapData = null;
			if (ldapObjs.size() > 0) {
				ldapData = ldapObjs.get(0);
			}
			
			HashMap<String, List<String>> attributeValues = null;
			
			if(ldapData!=null && ldapData.getAttributeValues()!=null){
				attributeValues = ldapData
						.getAttributeValues();
					customerUser.setAttributeValues(attributeValues);
			}
			
			//Add business logic here...	
			
			PropertiesManager pm = PropertiesManager.getInstance();	

			//Checking for user status active or inactive
			boolean isUserActive = false;			
			if(attributeValues!=null && attributeValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS))!=null 
					&& attributeValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS)).size()>0){				
				if(attributeValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS)).get(0).equalsIgnoreCase("TRUE")){
					isUserActive = true;					
				}
			}
			EAILogger.info(className, className + ", " + method + ", isUserActive = " + isUserActive);
			customerUser.setActive(isUserActive);			
			
		}catch(SocialEAIException ex){
			//TODO Handle 
		}catch(Exception ex2){
			ex2.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex2));
			EAILogger.error(className, className + ", " + method + ", " + ex2.getMessage(),ex2);
		}
		return customerUser;
	}

	@Override
	public User getUser(SocialProfileData profileData) throws SocialEAIException{

		String method ="getUser";
			
			EAILogger.debug(className, className + ", " + method + ", Input: SocialProfileData = "+profileData);
			User customerUser = getUser(profileData.getEmail());
			customerUser.setSocialProfileData(profileData);
			
			HashMap<String, List<String>> attrValues = customerUser.getAttributeValues();
			
			try {
				PropertiesManager pm = PropertiesManager.getInstance();		
				
				//Checking for social id exist in LDAP 
				boolean socialIdExists = false;
				if(attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))!=null 
						&& attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID)).size()>0
						&& profileData.getId() !=null){
					List <String> socialIds = attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID));
					for(int i=0;i<socialIds.size();i++){
						String socialID =  socialIds.get(i);
						if( profileData.getId().equalsIgnoreCase(socialID.substring(2, socialID.length()))){
							socialIdExists = true;
							break;
						}
					}						
				}
				
				EAILogger.info(className, className + ", " + method + ", socialIdExists = "+socialIdExists);
				customerUser.setSocialIdExists(socialIdExists);
				
				//Checking for social email id primary
				boolean socialEmailIdPrimary = false;
				if(attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL))!=null 
						&& attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL)).size()>0
						&& profileData.getEmail() !=null
						&& profileData.getEmail().equalsIgnoreCase(attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL)).get(0))){
					socialEmailIdPrimary = true;				
				}
				EAILogger.info(className, className + ", " + method + ", socialEmailIdPrimary = "+socialEmailIdPrimary);
				customerUser.setSocialEmailIdPrimary(socialEmailIdPrimary);
				
				//Checking for primary email Linked  to social id
				boolean primaryEmailLinked2SocialId = false;
				if(socialEmailIdPrimary || socialIdExists){
					primaryEmailLinked2SocialId = true;
				}else{
					//If any other business decision.
					//Implementation will depends on decision.
				}

				EAILogger.info(className, className + ", " + method + ", socialIdExists = "+primaryEmailLinked2SocialId);
				customerUser.setPrimaryEmailLinked2SocialId(primaryEmailLinked2SocialId);
				
			} catch (SocialEAIException e) {
					
			} catch (Exception ex) {
				ex.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
				EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(), ex);			
			} 		
			
			return customerUser;		
	}

	@Override
	public boolean updateLastAccessTime(String email)
			throws SocialEAIException {
		
		String method ="updateLastAccessTime";
		
		EAILogger.debug(className, className + ", " + method);
		
		boolean updateStatus = false;
		
		String objectName = "";		
		//objectName = "uid=mewilcox, ou=People, o=airius.com";
		
		String currentTimeZuluGMT = DateUtil.getCurrentTimeZuluGMT();
		EAILogger.debug(className, "currentTimeZuluGMT :"+currentTimeZuluGMT);
		
		try{
			com.ibm.sam.eai.ldap.impl.LdapDaoImpl ldapDao = new com.ibm.sam.eai.ldap.impl.LdapDaoImpl(
					com.ibm.sam.eai.ldap.impl.LdapConnectionManager
							.getInstance().getConnection());
			//TODO		
			updateStatus = ldapDao.updateLastAccessTime(objectName, currentTimeZuluGMT);			
		
		}catch(SocialEAIException ex){
				
		}catch (Exception ex) {
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(), ex);			
		} 
		
		EAILogger.debug(className, className + ", " + method + ", updateStatus = " + updateStatus);
		return updateStatus;
		
	}

	@Override
	public User getUser_MatchedPrimaryEmailOfAnyExistingUser(SocialProfileData profileData, boolean isAssociatedOnly) throws SocialEAIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser_SocialIdExist(SocialProfileData profileData) throws SocialEAIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getNewlyCreatedUser(String userid) throws SocialEAIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getUserGroupList(String email) throws SocialEAIException {
		// TODO Auto-generated method stub
		return null;
	}
}

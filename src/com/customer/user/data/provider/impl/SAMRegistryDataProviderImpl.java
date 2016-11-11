package com.customer.user.data.provider.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.customer.user.data.model.User;
import com.customer.user.data.provider.CustomerDataProvider;
import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.util.DateUtil;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;
import com.tivoli.pd.rgy.RgyException;
import com.tivoli.pd.rgy.RgyGroup;
import com.tivoli.pd.rgy.RgyIterator;
import com.tivoli.pd.rgy.RgyRegistry;
import com.tivoli.pd.rgy.RgyUser;
import com.tivoli.pd.rgy.ldap.LdapRgyRegistryFactory;


public class SAMRegistryDataProviderImpl extends CustomerDataProvider {
	
	private static final String className = SAMRegistryDataProviderImpl.class.getName();
	public static final String SAM_LDAP_ATTRIBUTE_ALL = "sam.ldap.attribute.all";
	public static final String SAM_LDAP_ATTRIBUTE_USER_STATUS = "sam.ldap.attribute.user-status";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID = "sam.ldap.attribute.user-social-id";
	public static final String SAM_LDAP_ATTRIBUTE_USER_UID = "sam.ldap.attribute.user-uid";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL = "sam.ldap.attribute.user-social-email";
	public static final String SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL = "sam.ldap.attribute.user-primary-email";	
	public static final String SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS = "sam.ldap.attribute.user-last-access-timpstanp";
	public final static String GROUP_MEMBER_ATTRIBUTE = "sam.ldap.attribute.group-member";	
	public final static String GROUP_FILTER = "sam.ldap.attribute.group-filter";
	public final static String SAM_API_CONFIG_FILE =  "SAM_API_CONFIG_FILE";
	private static final String AMAZON_SHORT = "az_";
	private static final String LINKEDIN_SHORT = "li_";
	private static final String FACEBOOK_SHORT = "fb_";
	private static final String AMAZON = "amazon";
	private static final String LINKEDIN = "linkedin";
	private static final String FACEBOOK = "facebook";
	
	static RgyRegistry m_registry = null;
	static SAMRegistryDataProviderImpl m_instance;

	public SAMRegistryDataProviderImpl() throws SocialEAIException{
		m_registry = connectRegistry();
		System.out.println("m_registry :"+m_registry);
		EAILogger.debug(className, className + ", m_registry = " + m_registry);
	}
	static RgyRegistry connectRegistry(){
		if(m_instance==null){
	    	//Connect to Registry
	    	URL propertiesUrl = null;
			String samAPIConfigFile = "";
			try {
				samAPIConfigFile = PropertiesManager.getInstance().getProperty(SAM_API_CONFIG_FILE);	
				EAILogger.debug(className, className + ", samAPIConfigFile = " + samAPIConfigFile);
			} catch (SocialEAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", Error getting property " + SAM_API_CONFIG_FILE);
			}
			try {
			    //propertiesUrl = new URL("file", "", "D:\\IBM\\WebSphere8.5\\AppServer\\java\\jre\\lib\\security\\sam.conf");
				propertiesUrl = new URL("file", "", samAPIConfigFile);
				return LdapRgyRegistryFactory.getRgyRegistryInstance(propertiesUrl, null);
			}catch (MalformedURLException e3) {
				System.out.println("Error in reading TAM Config file - MalformedURL " + e3.getMessage() + e3.getCause());
			}catch (RgyException e2) {
				System.out.println("FAILED :  Unable to obtain instance of LdapRegistry " + e2.getMessage() + e2.getCause());
			} 
		}
    	return null;
    }
	
	public User getUser_SocialIdExist(SocialProfileData profileData){

		String method ="getUser_SocialIdExist";		
		String socialId = "";
		if(profileData!=null && profileData.getId()!=null && !profileData.getId().equalsIgnoreCase("")){
			EAILogger.debug(className, className + ", " + method + ", Input: profileData="+ profileData);			
			socialId = profileData.getId();	
			EAILogger.debug(className, className + ", " + method + ", Input: socialId="+ socialId);
			if(profileData.getProvider()!=null && profileData.getProvider().equalsIgnoreCase(AMAZON)){
				socialId = AMAZON_SHORT + socialId;
			}else if(profileData.getProvider()!=null && profileData.getProvider().equalsIgnoreCase(LINKEDIN)){
				socialId = LINKEDIN_SHORT + socialId;
			}else if(profileData.getProvider()!=null && profileData.getProvider().equalsIgnoreCase(FACEBOOK)){
				socialId = FACEBOOK_SHORT + socialId;
			}
			EAILogger.debug(className, className + ", " + method + ", Input: socialId="+ socialId);
		}else{
			return null;
		}
		
		User customerUser = null;
		HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
		
		RgyUser user;
		try {	

			String samlLDAPAttributeSocialId = "";
			String samlLDAPAllAttributes = "";
			try {					
				samlLDAPAttributeSocialId = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID);	
				samlLDAPAllAttributes = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_ALL);
				EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributeSocialId = " + samlLDAPAttributeSocialId);
				EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
				
			} catch (SocialEAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID + " " + SAM_LDAP_ATTRIBUTE_ALL );
			}
			
			//Added by Satish G. Date 27 Jan 2016
			if(m_registry==null||m_instance==null){
				m_registry = connectRegistry();
			}
			EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
			EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
			
			RgyIterator iterator = m_registry.listNativeUsers(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID), socialId, 1, 1);
			if(iterator!=null){			
				if (iterator.hasNext()) {
					customerUser = new User();
					String nativeId = iterator.next();
					String uid = "";
					user = m_registry.getNativeUser(null, nativeId);
					EAILogger.info(className, className + ", " + method + ", user = " + user);			
					
					if(user!=null){
						
						String[] names = samlLDAPAllAttributes.split(";");
						for(int i=0;i<names.length;i++){
							String name = names[i];	
							List valueList = new ArrayList<String>();
							Object[] objList = user.getAttributeValues(name);
							if(objList!=null && objList.length>0){
								for(int j=0;j<objList.length;j++){
									valueList.add((String)objList[j]);
									EAILogger.debug(className, className + ", " + method + ", " + name+"="+objList[j]);	
									if(name!=null&& name.equalsIgnoreCase(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_UID))){
										uid = (String)objList[j];
									}
								}
							}
							attributeValues.put(name, valueList);								
						}				
					
						//Getting user groups (only static)
//						String grpFilter = PropertiesManager.getInstance().getProperty(GROUP_FILTER);
//						EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//						if(grpFilter!=null){
//							grpFilter =	grpFilter.replace("<UID>", uid);
//							EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//							
//							RgyIterator iterator2 = m_registry.listNativeGroups(PropertiesManager.getInstance().getProperty(GROUP_MEMBER_ATTRIBUTE), grpFilter, 0, 1);
//							List groupList = new ArrayList<String>();
//							while (iterator2.hasNext()) {
//								String group = iterator2.next();						
//								EAILogger.info(className, className + ", " + method + ", group = " + group);	
//								groupList.add(group);
//							}						
//							attributeValues.put("GROUPS", groupList);
//						}
						
						List<String> groupList = getGroups(user);

						if(groupList!=null && groupList.size()>0){
							attributeValues.put("GROUPS", groupList);
						}						
						
						if(attributeValues.size()>0){
							customerUser.setAttributeValues(attributeValues);
						}
					}					
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
					
					customerUser.setSocialId(profileData.getId());
					
					customerUser.setSocialProfileData(profileData);
					
					HashMap<String, List<String>> attrValues = customerUser.getAttributeValues();					
					
						
						//Checking for social id exist in LDAP 
						//This is not used.
						boolean socialIdExists = false;
						if(attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))!=null 
								&& attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID)).size()>0
								&& profileData.getId() !=null){
							List <String> socialIds = attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID));
							for(int i=0;i<socialIds.size();i++){
								String socialID =  socialIds.get(i);
								if( profileData.getId().equalsIgnoreCase(socialID.substring(3, socialID.length()))){
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
						if(socialEmailIdPrimary && socialIdExists){
							primaryEmailLinked2SocialId = true;
						}else{
							//IF any other decision.
							//Implementation depends on decision.
						}

						EAILogger.info(className, className + ", " + method + ", socialIdExists = "+primaryEmailLinked2SocialId);
						customerUser.setPrimaryEmailLinked2SocialId(primaryEmailLinked2SocialId);
				}
				

			}else{
				return null;
			}		
//			user = m_registry.getUser(null, email);
//
//			if(user!=null){
//				String[] names = samlLDAPAllAttributes.split(";");
//				for(int i=0;i<names.length;i++){
//					String name = names[i];
//					System.out.println(name+"="+user.getOneAttributeValue(name));
//				}
//			}
			
		} catch (RgyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
		}
	
		return customerUser;
	}
	
	public User getUser_MatchedPrimaryEmailOfAnyExistingUser(SocialProfileData profileData, boolean isAssociatedOnly){
		
		String method ="getUser_MatchedPrimaryEmailOfAnyExistingUser";
		String email = "";
		String socialId = "";
		boolean associated = false;
		EAILogger.debug(className, className + ", " + method + ", Input: isAssociated="+ isAssociatedOnly);
		if(profileData!=null && profileData.getEmail()!=null && !profileData.getEmail().equalsIgnoreCase("")){
			EAILogger.debug(className, className + ", " + method + ", Input: profileData="+ profileData);
			email = profileData.getEmail();
			socialId = profileData.getId();
			EAILogger.debug(className, className + ", " + method + ", Input: email="+ email);
			EAILogger.debug(className, className + ", " + method + ", Input: socialId="+ socialId);
		}else{
			return null;
		}

		
		User customerUser = null;
		HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
		
		RgyUser user;
		try {	

			String samlLDAPAttributePrimaryEmail = "";
			String samlLDAPAllAttributes = "";
			try {					
				samlLDAPAttributePrimaryEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);	
				samlLDAPAllAttributes = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_ALL);
				EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributePrimaryEmail = " + samlLDAPAttributePrimaryEmail);
				EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
				
			} catch (SocialEAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL + " " + SAM_LDAP_ATTRIBUTE_ALL );
			}
			
			//Added by Satish G. Date 27 Jan 2016
			if(m_registry==null||m_instance==null){
				m_registry = connectRegistry();
			}
			EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
			EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
			
			RgyIterator iterator = m_registry.listNativeUsers(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL), email, 1, 1);
			if(iterator!=null){			
				if (iterator.hasNext()) {
					customerUser = new User();
					String nativeId = iterator.next();
					String uid = "";
					user = m_registry.getNativeUser(null, nativeId);
					EAILogger.info(className, className + ", " + method + ", user = " + user);			
					
					if(user!=null){
						
						String[] names = samlLDAPAllAttributes.split(";");
						for(int i=0;i<names.length;i++){
							String name = names[i];	
							List valueList = new ArrayList<String>();
							Object[] objList = user.getAttributeValues(name);
							if(name.equalsIgnoreCase(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))
										&& objList!=null && objList.length>0 && !((String)objList[0]).equalsIgnoreCase("")){								
								associated = true;								
							}
							
							if(objList!=null && objList.length>0){
								for(int j=0;j<objList.length;j++){
									valueList.add((String)objList[j]);
									EAILogger.debug(className, className + ", " + method + ", " + name+"="+objList[j]);
									if(name!=null&& name.equalsIgnoreCase(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_UID))){
										uid = (String)objList[j];
									}
								}
							}
							attributeValues.put(name, valueList);						
						}	
						
						EAILogger.debug(className, className + ", " + method + ", isAssociatedOnly = " + isAssociatedOnly);
						EAILogger.debug(className, className + ", " + method + ", associated = " + associated);
						
						if(!isAssociatedOnly && associated){
							EAILogger.debug(className, className + ", " + method + ", Need not associated user only, This user has associated scoial id, returning null.");
							return null;
						}
						if(isAssociatedOnly && !associated){
							EAILogger.debug(className, className + ", " + method + ", Need associated user only, This user has no associated scoial id, returning null.");
							return null;
						}
					
						//Getting user groups (only static)
//						String grpFilter = PropertiesManager.getInstance().getProperty(GROUP_FILTER);
//						EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//						if(grpFilter!=null){
//							grpFilter =	grpFilter.replace("<UID>", uid);
//							EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//							
//							RgyIterator iterator2 = m_registry.listNativeGroups(PropertiesManager.getInstance().getProperty(GROUP_MEMBER_ATTRIBUTE), grpFilter, 0, 1);
//							List groupList = new ArrayList<String>();
//							while (iterator2.hasNext()) {
//								String group = iterator2.next();						
//								EAILogger.info(className, className + ", " + method + ", group = " + group);	
//								groupList.add(group);
//							}						
//							attributeValues.put("GROUPS", groupList);
//						}	
						
						List<String> groupList = getGroups(user);
						if(groupList!=null && groupList.size()>0){
							attributeValues.put("GROUPS", groupList);
						}
						
						if(attributeValues.size()>0){
							customerUser.setAttributeValues(attributeValues);
						}
					}
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
					
					customerUser.setSocialId(profileData.getId());
					
					customerUser.setSocialProfileData(profileData);
					
					HashMap<String, List<String>> attrValues = customerUser.getAttributeValues();					
					
						
						//Checking for social id exist in LDAP
						//This is not used.
						boolean socialIdExists = false;
						if(attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))!=null 
								&& attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID)).size()>0
								&& profileData.getId() !=null){
							List <String> socialIds = attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID));
							for(int i=0;i<socialIds.size();i++){
								String socialID =  socialIds.get(i);
								if( profileData.getId().equalsIgnoreCase(socialID.substring(3, socialID.length()))){
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
						if(socialEmailIdPrimary && socialIdExists){
							primaryEmailLinked2SocialId = true;
						}else{
							//IF any other decision.
							//Implementation depends on decision.
						}

						EAILogger.info(className, className + ", " + method + ", socialIdExists = "+primaryEmailLinked2SocialId);
						customerUser.setPrimaryEmailLinked2SocialId(primaryEmailLinked2SocialId);
				}				

			}else{
				return null;
			}		
//			user = m_registry.getUser(null, email);
//
//			if(user!=null){
//				String[] names = samlLDAPAllAttributes.split(";");
//				for(int i=0;i<names.length;i++){
//					String name = names[i];
//					System.out.println(name+"="+user.getOneAttributeValue(name));
//				}
//			}
			
		} catch (RgyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
		}
	
		return customerUser;
	}

	public User getNewlyCreatedUser(String userid){
		
		String method ="getNewlyCreatedUser";			
		EAILogger.debug(className, className + ", " + method + ", Input: userid="+ userid);	
		
		User customerUser = null;
		
		if(userid!=null){			
			HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
			
			RgyUser user;
			try {	

				String samlLDAPAttributeuid = "";
				String samlLDAPAllAttributes = "";
				try {					
					samlLDAPAttributeuid = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_UID);	
					samlLDAPAllAttributes = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_ALL);
					EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributeuid = " + samlLDAPAttributeuid);
					EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
					
				} catch (SocialEAIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
					EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_USER_UID + " " + SAM_LDAP_ATTRIBUTE_ALL );
				}
				
				//Added by Satish G. Date 27 Jan 2016
				if(m_registry==null||m_instance==null){
					m_registry = connectRegistry();
				}
				EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
				EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
				
				RgyIterator iterator = m_registry.listNativeUsers(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_UID), userid, 1, 1);
				if(iterator!=null){			
					if (iterator.hasNext()) {
						customerUser = new User();
						String nativeId = iterator.next();
						user = m_registry.getNativeUser(null, nativeId);
						EAILogger.info(className, className + ", " + method + ", user = " + user);			
						
						if(user!=null){
							
							String[] names = samlLDAPAllAttributes.split(";");
							for(int i=0;i<names.length;i++){
								String name = names[i];	
								List valueList = new ArrayList<String>();
								Object[] objList = user.getAttributeValues(name);
								if(objList!=null && objList.length>0){
									for(int j=0;j<objList.length;j++){
										valueList.add((String)objList[j]);
										EAILogger.debug(className, className + ", " + method + ", " + name+"="+objList[j]);	
									}
								}
								attributeValues.put(name, valueList);						
							}				
						
							//Getting user groups (only static)
//							String grpFilter = PropertiesManager.getInstance().getProperty(GROUP_FILTER);
//							EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//							if(grpFilter!=null){
//								grpFilter =	grpFilter.replace("<UID>", userid);
//								EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//								
//								RgyIterator iterator2 = m_registry.listNativeGroups(PropertiesManager.getInstance().getProperty(GROUP_MEMBER_ATTRIBUTE), grpFilter, 0, 1);
//								List groupList = new ArrayList<String>();
//								while (iterator2.hasNext()) {
//									String group = iterator2.next();						
//									EAILogger.info(className, className + ", " + method + ", group = " + group);	
//									groupList.add(group);
//								}						
//								attributeValues.put("GROUPS", groupList);
//							}	
							
							List<String> groupList = getGroups(user);
							if(groupList!=null && groupList.size()>0){
								attributeValues.put("GROUPS", groupList);
							}
							
							if(attributeValues.size()>0){
								customerUser.setAttributeValues(attributeValues);
							}
						}					
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
						
					}
				}else{
					return null;
				}	

				
			} catch (RgyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			}catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
				EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
			}
		}else{
			return null;
		}		
	
		return customerUser;
	}
	
	//TEMP_DATA temp
	//@Override
	public User getUser_TEMP(String email) throws SocialEAIException {
		String method ="getUser";
		EAILogger.debug(className, className + ", " + method + ", Input: email="+ email);
		
		User customerUser = new User();
		HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
		
		
	
		try {	
			PropertiesManager pm =PropertiesManager.getInstance();		
			
			String samlLDAPAllAttributes = "";
			String samlLDAPAttributePrimaryEmail = "";
			try {
				samlLDAPAllAttributes = pm.getProperty(SAM_LDAP_ATTRIBUTE_ALL);	
				samlLDAPAttributePrimaryEmail = pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
				EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
				EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributePrimaryEmail = " + samlLDAPAttributePrimaryEmail);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_ALL + " or " + SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
			}

			try {
				String[] names = samlLDAPAllAttributes.split(";");
				for(int i=0;i<names.length;i++){
					String name = names[i];	
					List valueList = new ArrayList<String>();
					String value = pm.getProperty(name);
					if(value!=null){
						String[] valueArray = value.split("#");
							if(valueArray!=null && valueArray.length>0){
								for(int j=0;j<valueArray.length;j++){
									valueList.add((String)valueArray[j]);
									EAILogger.debug(className, className + ", " + method + ", " + name+"="+valueArray[j]);	
								}
							}
							attributeValues.put(name, valueList);	
					}										
				}	
				
				String value = pm.getProperty("GROUPS");
				List valueList = new ArrayList<String>();
				if(value!=null){
					String[] valueArray = value.split("#");
						if(valueArray!=null && valueArray.length>0){
							for(int j=0;j<valueArray.length;j++){
								valueList.add((String)valueArray[j]);
								EAILogger.debug(className, className + ", " + method + ", GROUPS="+valueArray[j]);	
							}
						}
						attributeValues.put("GROUPS", valueList);	
				}	
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			}	
			
			
			customerUser.setAttributeValues(attributeValues);			
			
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
			
//			user = m_registry.getUser(null, email);
//
//			if(user!=null){
//				String[] names = samlLDAPAllAttributes.split(";");
//				for(int i=0;i<names.length;i++){
//					String name = names[i];
//					System.out.println(name+"="+user.getOneAttributeValue(name));
//				}
//			}
			
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className + ", " + method + ", " + ex.getMessage(),ex);
		}


		
		// TODO Auto-generated method stub
		return customerUser;
	}

	//TEMP_DATA original
	//name modified 
	//override disabled
	@Override
	public User getUser(String email) throws SocialEAIException {
		String method ="getUser";
		EAILogger.debug(className, className + ", " + method + ", Input: email="+ email);
		
		User customerUser = new User();
		HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
		
		RgyUser user;
		try {
	    	/*RgyIterator rgyIterator = m_registry.listNativeUsers("uid", email, 1, 0);
	    	if (rgyIterator!= null && rgyIterator.hasNext()) {
				userId = rgyIterator.next();
				RgyUser user = m_registry.getNativeUser(null, userId);
				System.out.println("---------------\n"+userId);
			
				String all = "sn;uid;mail;cn;email;userid;eixcsrCreated;eixcustAcctName;eixcustAcctNumber;eixstatusIndicator;eixtcAccepted;email";
				
				String[] names = all.split(";");
				for(int i=0;i<names.length;i++){
					String name = names[i];
					System.out.println(name+"="+user.getOneAttributeValue(name));
				}
				
				return true;
	    	}
	    	*/
			
			String samlLDAPAllAttributes = "";
			String samlLDAPAttributePrimaryEmail = "";
			try {
				samlLDAPAllAttributes = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_ALL);	
				samlLDAPAttributePrimaryEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
				EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
				EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributePrimaryEmail = " + samlLDAPAttributePrimaryEmail);
			} catch (SocialEAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_ALL + " or " + SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
			}
			
			//Added by Satish G. Date 27 Jan 2016
			if(m_registry==null||m_instance==null){
				m_registry = connectRegistry();
			}
			EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
			EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
			
			RgyIterator iterator = m_registry.listNativeUsers(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL), email, 1, 1);
			if(iterator!=null){			
				while (iterator.hasNext()) {
					String nativeId = iterator.next();
					String uid = "";
					user = m_registry.getNativeUser(null, nativeId);
					EAILogger.info(className, className + ", " + method + ", user = " + user);			
					
					if(user!=null){
						
						String[] names = samlLDAPAllAttributes.split(";");
						for(int i=0;i<names.length;i++){
							String name = names[i];	
							List valueList = new ArrayList<String>();
							Object[] objList = user.getAttributeValues(name);
							if(objList!=null && objList.length>0){
								for(int j=0;j<objList.length;j++){
									valueList.add((String)objList[j]);
									EAILogger.debug(className, className + ", " + method + ", " + name+"="+objList[j]);	
									if(name!=null&& name.equalsIgnoreCase(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_UID))){
										uid = (String)objList[j];
									}
								}
							}
							attributeValues.put(name, valueList);						
						}				
					
						//Getting user groups
//						String grpFilter = PropertiesManager.getInstance().getProperty(GROUP_FILTER);
//						EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//						if(grpFilter!=null){
//							grpFilter =	grpFilter.replace("<UID>", uid);
//							EAILogger.debug(className, className + ", " + method + ", grpFilter=" + grpFilter);
//							
//							RgyIterator iterator2 = m_registry.listNativeGroups(PropertiesManager.getInstance().getProperty(GROUP_MEMBER_ATTRIBUTE), grpFilter, 0, 1);
//							List groupList = new ArrayList<String>();
//							while (iterator2.hasNext()) {
//								String group = iterator2.next();						
//								EAILogger.info(className, className + ", " + method + ", group = " + group);	
//								groupList.add(group);
//							}						
//							attributeValues.put("GROUPS", groupList);
//						}	
//						
						List<String> groupList = getGroups(user);
						
						if(groupList!=null && groupList.size()>0){
							attributeValues.put("GROUPS", groupList);
						}
						
						if(attributeValues.size()>0){
							customerUser.setAttributeValues(attributeValues);
						}
					}
				}
			}
			
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
			
//			user = m_registry.getUser(null, email);
//
//			if(user!=null){
//				String[] names = samlLDAPAllAttributes.split(";");
//				for(int i=0;i<names.length;i++){
//					String name = names[i];
//					System.out.println(name+"="+user.getOneAttributeValue(name));
//				}
//			}
			
		} catch (RgyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
		}


		
		// TODO Auto-generated method stub
		return customerUser;
	}

	@Override
	public User getUser(SocialProfileData profileData)
			throws SocialEAIException {
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
					if( profileData.getId().equalsIgnoreCase(socialID.substring(3, socialID.length()))){
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
			if(socialEmailIdPrimary && socialIdExists){
				primaryEmailLinked2SocialId = true;
			}else{
				//IF any other decision.
				//Implementation depends on decision.
			}

			EAILogger.info(className, className + ", " + method + ", primaryEmailLinked2SocialId = "+primaryEmailLinked2SocialId);
			customerUser.setPrimaryEmailLinked2SocialId(primaryEmailLinked2SocialId);
			
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);	
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
		boolean updateLastAccessTimeStatus = false;
		if(email!=null && email.length()>0){
			EAILogger.debug(className, className + ", " + method + ", Input: email="+ email);
			RgyUser user;
			try{
				//Added by Satish G. Date 27 Jan 2016
				if(m_registry==null||m_instance==null){
					m_registry = connectRegistry();
				}
				EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
				EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
				
				RgyIterator iterator = m_registry.listNativeUsers(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL), email, 1, 1);
				if(iterator!=null){
					if (iterator.hasNext()) {
						String nativeId = iterator.next();
						user = m_registry.getNativeUser(null, nativeId);
						EAILogger.info(className, className + ", " + method + ", user = " + user);			
						
						if(user!=null){
							String nowInGMT = DateUtil.getCurrentTimeZuluGMT();
							user.attributeReplace(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS), nowInGMT);
							updateLastAccessTimeStatus = true;
							EAILogger.debug(className, className + ", " + method + ", Updated last access time.");
						}else{
							EAILogger.debug(className, className + ", " + method + ", User not found.");
						}
					}else{
						EAILogger.debug(className, className + ", " + method + ", User not found.");
					}
				}	else{
					EAILogger.debug(className, className + ", " + method + ", User not found.");
				}
			}catch (RgyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			}catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
				EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
			}
		}else{
			EAILogger.debug(className, className + ", " + method + ", email is null or empty, cannot update last access time.");
		}	

		// TODO Auto-generated method stub
		return updateLastAccessTimeStatus;
	}
	
	
	private List<String> getGroups(RgyUser user) throws RgyException{
		
		String method = "getGroups";
		
		//Getting static and dyanmic groups 
		Set<String> groupIds = null; 
		groupIds = user.listNativeGroups(); 
		List<String> groupList = new ArrayList<String>();
		Iterator<String> iter = groupIds.iterator(); 
		while (iter.hasNext()) { 
		    String groupId = (String) iter.next(); 
		    EAILogger.info(className, className + ", " + method + ", groupId = " + groupId);			  
		    int ind2= groupId.indexOf(",");
		    String groupName = groupId.substring("cn=".length(), ind2);	    
		    EAILogger.info(className, className + ", " + method + ", groupId = " + groupId);	
		    EAILogger.info(className, className + ", " + method + ", groupName = " + groupName);	
		    groupList.add(groupName);						    
		} 		
		return groupList;
	}
	
	@Override
	public List<String> getUserGroupList(String email) throws SocialEAIException {

		String method ="getUserGroupList";
		EAILogger.debug(className, className + ", " + method + ", Input: email="+ email);
		List<String> groupList =new ArrayList<String>();		
		RgyUser user;
		try {	
			String samlLDAPAllAttributes = "";
			String samlLDAPAttributePrimaryEmail = "";
			try {
				samlLDAPAllAttributes = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_ALL);	
				samlLDAPAttributePrimaryEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
				EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
				EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributePrimaryEmail = " + samlLDAPAttributePrimaryEmail);
			} catch (SocialEAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_ALL + " or " + SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
			}
			
			if(m_registry==null||m_instance==null){
				m_registry = connectRegistry();
			}
			EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
			EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
			m_registry.listNativeGroups("group", "socialEAIApprover", 1, 1);
			RgyIterator iterator = m_registry.listNativeUsers(PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL), email, 1, 1);
			if(iterator!=null){			
				while (iterator.hasNext()) {
					String nativeId = iterator.next();
					user = m_registry.getNativeUser(null, nativeId);
					EAILogger.info(className, className + ", " + method + ", user = " + user);			
					
					if(user!=null){
						groupList = getGroups(user);
					}
				}
			}
			
		} catch (RgyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
		}
		return groupList;
	}
	
	/*public List<String> getGroupMemberList() throws SocialEAIException {

		String method ="getUserGroupList";
		List<String> groupList =new ArrayList<String>();		
		RgyUser user;
		try {	
			String samlLDAPAllAttributes = "";
			String samlLDAPAttributePrimaryEmail = "";
			try {
				samlLDAPAllAttributes = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_ALL);	
				samlLDAPAttributePrimaryEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
				EAILogger.debug(className, className + ", " + method + ", samlAllAttributes = " + samlLDAPAllAttributes);
				EAILogger.debug(className, className + ", " + method + ", samlLDAPAttributePrimaryEmail = " + samlLDAPAttributePrimaryEmail);
			} catch (SocialEAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
				EAILogger.error(className, className + ", " + method + ", Error getting property " + SAM_LDAP_ATTRIBUTE_ALL + " or " + SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
			}
			
			if(m_registry==null||m_instance==null){
				m_registry = connectRegistry();
			}
			EAILogger.debug(className, className + ", " + method + ", m_registry = " + m_registry);
			EAILogger.debug(className, className + ", " + method + ", m_instance = " + m_instance);
			RgyIterator iterator = m_registry.listNativeGroups("cn", "socialEAIApprover", 1, 1);
			RgyGroup group = null;
			if(iterator!=null){			
				while (iterator.hasNext()) {
					String nativeId = iterator.next();
					//user = m_registry.getNativeUser(null, nativeId);
					group = m_registry.getNativeGroup(null, nativeId);
					System.out.println("group==="+group.toString());
				}
			}
			System.out.println("getting group memebers");
			Set<String> members = group.listMemberIds();
			System.out.println("members====="+members.toString());
			
			
		} catch (RgyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(ex));
			EAILogger.error(className, className + ", " + method + ", " + ex.getMessage(),ex);
		}
		return groupList;
	}*/
	
	public static void main(String sr[]){		
		try {
			SAMRegistryDataProviderImpl a = new SAMRegistryDataProviderImpl();
			System.out.println(a.getUser("sid"));
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}

	}
	
}

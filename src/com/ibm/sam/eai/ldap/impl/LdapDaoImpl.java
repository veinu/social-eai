package com.ibm.sam.eai.ldap.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.customer.user.data.model.User;
import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.LdapData;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.resources.ErrorCodes;
import com.ibm.sam.eai.social.util.DateUtil;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;

public class LdapDaoImpl {
	private static final String className = LdapDaoImpl.class.getName();
	LdapConnection m_ldapConn = null;
	ArrayList<String> m_returnAttributes;
	public final static String LDAP_RETURN_ATTRIBUTES = "LDAP.RETURN_ATTRIBUTES";
	public static final String SEACH_BASE = "LDAP.SEARCH_BASE";
	public static final String MAIL_FILTER = "LDAP.MAIL_FILTER";
	public static final String LDAP_ATTRIBUTE_LASTACCESSTIME = "LDAP.LASTACCESSTIME";
	public static final String SAM_LDAP_OBJECT_CLASS = "sam.ldap.objectclass";
	public static final String SAM_LDAP_ATTRIBUTE_USER_STATUS = "sam.ldap.attribute.user-status";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID = "sam.ldap.attribute.user-social-id";
	public static final String SAM_LDAP_ATTRIBUTE_USER_UID = "sam.ldap.attribute.user-uid";
	public static final String SAM_LDAP_ATTRIBUTE_USER_GROUP = "sam.ldap.attribute.user-group";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL = "sam.ldap.attribute.user-social-email";
	public static final String SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS = "sam.ldap.attribute.user-last-access-timpstanp";
	public static final String SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL = "sam.ldap.attribute.user-primary-email";	
	public static final String SAM_LDAP_SUFFIX = "sam.ldap.suffix";

	public LdapDaoImpl(LdapConnection ldapConn) {
		String method = "LdapDaoImpl";
		EAILogger.debug(className, className + ", " + method);
		m_ldapConn = ldapConn;
		String returnAttrList;
		try {
			returnAttrList = PropertiesManager.getInstance().getProperty(LDAP_RETURN_ATTRIBUTES);
			if (returnAttrList != null) {
				m_returnAttributes = new ArrayList<String>();
				String[] tmp = returnAttrList.split(",");
				for (int i = 0; i < tmp.length; i++) {
					m_returnAttributes.add(tmp[i]);
				}
			}
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
		}
	}

	public ArrayList<LdapData> getLdapObject(String email)
			throws SocialEAIException {
		String filter = "(uid=" + email + ")";
		return getLdapObjectByFilter("", filter, m_returnAttributes);
	}

	public ArrayList<LdapData> getLdapObjectByFilter(
			String base, String filter, ArrayList<String> returnAttributes) throws SocialEAIException {

		String method = "getLdapObjectByFilter";
		EAILogger.debug(className, className + ", " + method + ", Input: base =" + base + ", filter="
				+ filter + ", returnAttributes=" + returnAttributes.toString());

		HashMap<String, ArrayList<String>> returnValues = new HashMap<String, ArrayList<String>>();
		if (base == null) {
			base = "";
		}
		ArrayList<LdapData> ldapDataObjs = new ArrayList<LdapData>();
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnAttr = null;
		if (returnAttributes != null && returnAttributes.size() > 0) {
			returnAttr = new String[returnAttributes.size()];
			for (int i = 0; i < returnAttributes.size(); i++) {
				returnAttr[i] = returnAttributes.get(i);
			}
			controls.setReturningAttributes(returnAttr);
		} else {
			returnAttr = new String[] { "uid", "mail", "cn", "sn" };
		}
		NamingEnumeration<SearchResult> results;
		try {
			results = m_ldapConn.getConnection()
					.search(base, filter, controls);
			while (results.hasMore()) {

				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				String uid = null;
				if (attributes.get("uid") != null && attributes.get("uid").get(0) != null) {
					uid = (String) attributes.get("uid").get(0);
				}
				String mail = null;
				if (attributes.get("mail") != null && attributes.get("mail").get(0) != null) {
					mail = (String) attributes.get("mail").get(0);
				}

				LdapData ldapData = new LdapData(uid, mail, searchResult.getNameInNamespace());

				NamingEnumeration<? extends Attribute> attr_ne = attributes
						.getAll();
				while (attr_ne.hasMore()) {
					Attribute attr = attr_ne.next();

					ArrayList<String> values = new ArrayList<String>();
					for (int i = 0; i < attr.size(); i++) {
						values.add(attr.get(i).toString());
					}
					ldapData.setAttributeValue(attr.getID(), values);
					returnValues.put(attr.getID(), values);
				}
				ldapDataObjs.add(ldapData);

			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}
		EAILogger.debug(className, className + ", " + method + ", ldapDataObjs = " + ldapDataObjs.toString());
		return ldapDataObjs;
	}

	public boolean updateLastAccessTime(String objectName, String lastAccessTime) throws SocialEAIException {

		String method = "updateLastAccessTime";
		EAILogger.debug(className, className + ", " + method + "Input: objectName=" + objectName + ", lastAccessTime=" + lastAccessTime);

		boolean updateStatus = false;

		ModificationItem[] mods = new ModificationItem[1];
		Attribute mod0 = new BasicAttribute(LDAP_ATTRIBUTE_LASTACCESSTIME, lastAccessTime);
		mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
		// mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod1);

		try {
			m_ldapConn.getConnection().modifyAttributes(objectName, mods);
			// m_ldapConn.getConnection().modifyAttributes("uid=mewilcox, ou=People, o=airius.com",
			// mods);
			updateStatus = true;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}
		EAILogger.debug(className, className + ", " + method + ", updateStatus = " + updateStatus);
		return updateStatus;
	}

	public boolean createLDAPUser(User user) {
		String method = "createLDAPUser";
		EAILogger.debug(className, className + ", " + method + "Input: User=" + user.getSocialProfileData().toString());
		boolean userCreationStatus = false;
		LdapContext ctx = m_ldapConn.getConnection();
		Attributes attributes = new BasicAttributes();
		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("inetOrgPerson");
		String customObjectClass = "";
		String socialId = "";
		String socialEmail = "";
		String createTimestamp = "";
		String userStatus = "";
		String suffix = "";
		try {
			customObjectClass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
			socialId = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID);
			socialEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL);
			createTimestamp = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS);
			userStatus = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS);
			PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_UID);
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
		} catch (SocialEAIException e1) {
			e1.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e1));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		objectClass.add(customObjectClass);
		attributes.put(objectClass);
		Attribute sn = new BasicAttribute("sn");
		Attribute cn = new BasicAttribute("cn");
		Attribute socialEAIID = new BasicAttribute(socialId);
		Attribute socialEAIEmail = new BasicAttribute(socialEmail);
		Attribute socialEAIIDCreateTimeStamp = new BasicAttribute(createTimestamp);
		Attribute status = new BasicAttribute(userStatus);

		sn.add(user.getSocialProfileData().getLastName());
		cn.add(user.getSocialProfileData().getFirstName());
		socialEAIEmail.add(user.getSocialProfileData().getEmail());
		String nowInGMT = DateUtil.getCurrentTimeZuluGMT();
		socialEAIIDCreateTimeStamp.add(nowInGMT);
		status.add("FALSE");

		if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("facebook"))
			socialEAIID.add("fb_" + user.getSocialId());
		else if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("amazon"))
			socialEAIID.add("az_" + user.getSocialId());
		else if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("linkedin"))
			socialEAIID.add("ln_" + user.getSocialId());

		attributes.put(sn);
		attributes.put(cn);
		attributes.put(socialEAIID);
		attributes.put(socialEAIEmail);
		attributes.put(socialEAIIDCreateTimeStamp);
		attributes.put(status);
		try {
			ctx.createSubcontext("uid=" + user.getSocialProfileData().getEmail() + "," + suffix, attributes);

			EAILogger.error(className, className + ", " + method + ":New User added to LDAP repository.");
			userCreationStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}

		return userCreationStatus;
	}

	public boolean addLDAPUserAllAttributes(User user) {
		String method = "addLDAPUserAllAttributes";
		EAILogger.debug(className, className + ", " + method + "Update: User=" + user.getSocialProfileData().toString());
		boolean updateStatus = false;

		String customObjectClass = "";
		String socialId = "";
		String socialEmail = "";
		String createTimestamp = "";
		String userStatus = "";
		String suffix = "";
		try {
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
			customObjectClass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
			socialId = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID);
			socialEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL);
			createTimestamp = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS);
			userStatus = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		Attributes updateattr = new BasicAttributes();
		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add(customObjectClass);
		updateattr.put(objectClass);
		if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("facebook"))
			updateattr.put(socialId, "fb_" + user.getSocialId());
		else if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("amazon"))
			updateattr.put(socialId, "az_" + user.getSocialId());
		else if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("linkedin"))
			updateattr.put(socialId, "ln_" + user.getSocialId());
		updateattr.put(socialEmail, user.getSocialProfileData().getEmail());
		String nowInGMT = DateUtil.getCurrentTimeZuluGMT();
		updateattr.put(createTimestamp, nowInGMT);
		updateattr.put(userStatus, "false");
		try {
			m_ldapConn.getConnection().modifyAttributes("uid=" + user.getSocialProfileData().getEmail() + "," + suffix, DirContext.ADD_ATTRIBUTE,
					updateattr);
			updateStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		return updateStatus;
	}
	
	public boolean addLDAPUserSocialAttributes(User user){
		String method = "addLDAPUserSocialAttributes";
		EAILogger.debug(className, className + ", " + method + "Update: User=" + user.getSocialProfileData().toString());
		boolean updateStatus = false;
		String socialId = "";
		String socialEmail = "";
		String createTimestamp = "";
		String suffix = "";
		String group = "";
		try {
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
			socialId = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID);
			socialEmail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL);
			createTimestamp = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS);
			group = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_GROUP);
			} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_SUFFIX);
		}
		Attributes updateattr = new BasicAttributes();
		if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("facebook"))
			updateattr.put(socialId, "fb_" + user.getSocialId());
		else if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("amazon"))
			updateattr.put(socialId, "az_" + user.getSocialId());
		else if (user.getSocialProfileData().getProvider() != null && user.getSocialProfileData().getProvider().equalsIgnoreCase("linkedin"))
			updateattr.put(socialId, "ln_" + user.getSocialId());
		updateattr.put(socialEmail, user.getSocialProfileData().getEmail());
		String nowInGMT = DateUtil.getCurrentTimeZuluGMT();
		updateattr.put(createTimestamp, nowInGMT);
		updateattr.put("memberof", group); //memberof is custom attribute added to LDAP schema
		try {
			m_ldapConn.getConnection().modifyAttributes("uid=" + user.getSocialProfileData().getEmail() + "," + suffix, DirContext.ADD_ATTRIBUTE,
					updateattr);
			updateStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		return updateStatus;
	}
	
	public boolean addLDAPUserBasicAttribtues(User user){
		String method = "addLDAPUserBasicAttribtues";
		EAILogger.debug(className, className + ", " + method + "Update: User=" + user.getSocialProfileData().toString());
		boolean updateStatus = false;
		String customObjectClass = "";
		String userStatus = "";
		String suffix = "";
		String primarymail="";
		try {
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
			customObjectClass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
			userStatus = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS);
			primarymail = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		Attributes updateattr = new BasicAttributes();
		Attribute objectClass = new BasicAttribute("objectclass");
		objectClass.add(customObjectClass);
		updateattr.put(objectClass);
		updateattr.put(userStatus, "false");
		updateattr.put(primarymail, user.getSocialProfileData().getEmail());
		try {
			m_ldapConn.getConnection().modifyAttributes("uid=" + user.getSocialProfileData().getEmail() + "," + suffix, DirContext.ADD_ATTRIBUTE,
					updateattr);
			updateStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		return updateStatus;
	}
	
	public boolean updateLDAPUserStatus(User user, boolean status){
		String method = "updateUserStatus";
		EAILogger.debug(className, className + ", " + method + "Update Status: User=" + user.getSocialProfileData().toString());
		boolean updateStatus = false;
		String userStatus = "";
		String suffix = "";
		try {
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
			userStatus = PropertiesManager.getInstance().getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_SUFFIX);
		}
		Attributes updateattr = new BasicAttributes();
		updateattr.put(userStatus, ""+status);
		try {
			m_ldapConn.getConnection().modifyAttributes("uid=" + user.getSocialProfileData().getEmail() + "," + suffix, DirContext.REPLACE_ATTRIBUTE,
					updateattr);
			updateStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		return updateStatus;
	}
	
	public boolean addUserToGroup(String email, String groupName){
		String method = "addUserToGroup";
		EAILogger.debug(className, className + ", " + method + "Add User To Group: =" + groupName);
		boolean addStatus = false;
		String suffix = "";
		try {
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_SUFFIX);
		}
		Attributes updateattr1 = new BasicAttributes();
		updateattr1.put("member", "uid="+email+","+suffix);
		Attributes updateattr2 = new BasicAttributes();
		updateattr2.put("memberof", groupName);
		try {
			m_ldapConn.getConnection().modifyAttributes("cn=" + groupName + "," + suffix, DirContext.ADD_ATTRIBUTE,
					updateattr1);
			m_ldapConn.getConnection().modifyAttributes("uid=" + email + "," + suffix, DirContext.ADD_ATTRIBUTE,
					updateattr2);
			addStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		return addStatus;
	}
	
	public boolean removeUserFromGroup(String email, String groupName){
		String method = "removeUserFromGroup";
		EAILogger.debug(className, className + ", " + method + "Remove User From Group: =" + groupName);
		boolean removeStatus = false;
		String suffix = "";
		try {
			suffix = PropertiesManager.getInstance().getProperty(SAM_LDAP_SUFFIX);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_SUFFIX);
		}
		Attributes updateattr1 = new BasicAttributes();
		updateattr1.put("member", "uid="+email+","+suffix);
		Attributes updateattr2 = new BasicAttributes();
		updateattr2.put("memberof", groupName);
		try {
			m_ldapConn.getConnection().modifyAttributes("cn=" + groupName + "," + suffix, DirContext.REMOVE_ATTRIBUTE,
					updateattr1);
			m_ldapConn.getConnection().modifyAttributes("uid=" + email + "," + suffix, DirContext.REMOVE_ATTRIBUTE,
					updateattr2);
			removeStatus = true;
		} catch (NamingException e) {
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		return removeStatus;
	}
	
	public List<SocialProfileData> listAllSocialEAIUsers() throws SocialEAIException{
		String method = "listAllSocialEAIUsers";
		EAILogger.debug(className, className + ", " + method + "Listing All Users from LDAP");
		List<SocialProfileData> users=new ArrayList<SocialProfileData>();
		String objectclass = "";
		try {
			objectclass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		
		String filter = "(objectclass="+objectclass+")";
		ArrayList<String> returnAttributes=m_returnAttributes;
		String base="";
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnAttr = null;
		if (returnAttributes != null && returnAttributes.size() > 0) {
			returnAttr = new String[returnAttributes.size()];
			for (int i = 0; i < returnAttributes.size(); i++) {
				returnAttr[i] = returnAttributes.get(i);
			}
			controls.setReturningAttributes(returnAttr);
		} else {
			returnAttr = new String[] { "uid", "mail", "cn", "sn" };
		}
		NamingEnumeration<SearchResult> results;
		try {
			results = m_ldapConn.getConnection()
					.search(base, filter, controls);
			while (results.hasMore()) {
				SocialProfileData user=new SocialProfileData();
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				String uid = null;
				if (attributes.get("uid") != null && attributes.get("uid").get(0) != null) {
					uid = (String) attributes.get("uid").get(0);
				}
				String cn = null;
				if (attributes.get("cn") != null && attributes.get("cn").get(0) != null) {
					cn = (String) attributes.get("cn").get(0);
				}
				String sn = null;
				if (attributes.get("sn") != null && attributes.get("sn").get(0) != null) {
					sn = (String) attributes.get("sn").get(0);
				}
				String mail = null;
				if (attributes.get("mail") != null && attributes.get("mail").get(0) != null) {
					mail = (String) attributes.get("mail").get(0);
				}
				user.setId(uid);
				user.setFirstName(cn);
				user.setLastName(sn);
				user.setEmail(mail);
				users.add(user);
			}

		} catch (NamingException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}	
		return users;
	}
	
	public String getLdapObjectPassword(String email) throws SocialEAIException {
		String filter = "(uid=" + email + ")";
		ArrayList<String> returnAttributes=m_returnAttributes;
		String method = "getLdapObjectPassword";
		String base="";
		EAILogger.debug(className, className + ", " + method + ", Input: base =" + base + ", filter="
				+ filter + ", returnAttributes=" + returnAttributes.toString());

		String userpassword=null;
		//ArrayList<LdapData> ldapDataObjs = new ArrayList<LdapData>();
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnAttr = null;
		if (returnAttributes != null && returnAttributes.size() > 0) {
			returnAttr = new String[returnAttributes.size()];
			for (int i = 0; i < returnAttributes.size(); i++) {
				returnAttr[i] = returnAttributes.get(i);
			}
			controls.setReturningAttributes(returnAttr);
		} else {
			returnAttr = new String[] { "uid", "mail", "cn", "sn", "userpassword" };
		}
		NamingEnumeration<SearchResult> results;
		try {
			results = m_ldapConn.getConnection()
					.search(base, filter, controls);
			while (results.hasMore()) {

				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();

				if (attributes.get("userpassword") != null && attributes.get("userpassword").get(0) != null) {
					userpassword = new String((byte[]) attributes.get("userpassword").get());
				}
			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}
		return userpassword;
	}
	
	public List<SocialProfileData> listAllRegistrationPendingUsers() throws SocialEAIException{
		String method = "listAllRegistrationPendingUsers";
		EAILogger.debug(className, className + ", " + method + "Listing All Users from LDAP Pending for Approval");
		List<SocialProfileData> users=new ArrayList<SocialProfileData>();
		String objectclass = "";
		try {
			objectclass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		
		String filter = "(&(objectclass="+objectclass+")(secAcctValid=false))";
		ArrayList<String> returnAttributes=m_returnAttributes;
		String base="";
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnAttr = null;
		if (returnAttributes != null && returnAttributes.size() > 0) {
			returnAttr = new String[returnAttributes.size()];
			for (int i = 0; i < returnAttributes.size(); i++) {
				returnAttr[i] = returnAttributes.get(i);
			}
			controls.setReturningAttributes(returnAttr);
		} else {
			returnAttr = new String[] { "uid", "mail", "cn", "sn" };
		}
		NamingEnumeration<SearchResult> results;
		try {
			results = m_ldapConn.getConnection()
					.search(base, filter, controls);
			while (results.hasMore()) {
				SocialProfileData user=new SocialProfileData();
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				String uid = null;
				if (attributes.get("uid") != null && attributes.get("uid").get(0) != null) {
					uid = (String) attributes.get("uid").get(0);
				}
				String cn = null;
				if (attributes.get("cn") != null && attributes.get("cn").get(0) != null) {
					cn = (String) attributes.get("cn").get(0);
				}
				String sn = null;
				if (attributes.get("sn") != null && attributes.get("sn").get(0) != null) {
					sn = (String) attributes.get("sn").get(0);
				}
				String mail = null;
				if (attributes.get("mail") != null && attributes.get("mail").get(0) != null) {
					mail = (String) attributes.get("mail").get(0);
				}
				user.setId(uid);
				user.setFirstName(cn);
				user.setLastName(sn);
				user.setEmail(mail);
				users.add(user);
			}

		} catch (NamingException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}	
		return users;
	}
	
	public List<SocialProfileData> listAllApprovers() throws SocialEAIException{
		String method = "listAllApprovers";
		EAILogger.debug(className, className + ", " + method);
		List<SocialProfileData> users=new ArrayList<SocialProfileData>();
		String objectclass = "";
		try {
			objectclass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		// String filter = "(&(objectClass=groupOfNames)(cn=socialEAIApprover))"; //listing group
		String filter = "(&(objectclass="+objectclass+")(memberof=socialEAIApprover))";
		ArrayList<String> returnAttributes=m_returnAttributes;
		String base="";
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnAttr = null;
		if (returnAttributes != null && returnAttributes.size() > 0) {
			returnAttr = new String[returnAttributes.size()];
			for (int i = 0; i < returnAttributes.size(); i++) {
				returnAttr[i] = returnAttributes.get(i);
			}
			controls.setReturningAttributes(returnAttr);
		} else {
			returnAttr = new String[] { "uid", "mail", "cn", "sn" };
		}
		NamingEnumeration<SearchResult> results;
		try {
			results = m_ldapConn.getConnection()
					.search(base, filter, controls);
			while (results.hasMore()) {
				SocialProfileData user = new SocialProfileData();
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
			//	System.out.println("=====attribtues===="+attributes.toString());
				String uid = null;
				if (attributes.get("uid") != null && attributes.get("uid").get(0) != null) {
					uid = (String) attributes.get("uid").get(0);
				}
				String cn = null;
				if (attributes.get("cn") != null && attributes.get("cn").get(0) != null) {
					cn = (String) attributes.get("cn").get(0);
				}
				String sn = null;
				if (attributes.get("sn") != null && attributes.get("sn").get(0) != null) {
					sn = (String) attributes.get("sn").get(0);
				}
				String mail = null;
				if (attributes.get("mail") != null && attributes.get("mail").get(0) != null) {
					mail = (String) attributes.get("mail").get(0);
				}
				user.setId(uid);
				user.setFirstName(cn);
				user.setLastName(sn);
				user.setEmail(mail);
				users.add(user);
			}
		} catch (NamingException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}	
		return users;
	}
	
	public List<SocialProfileData> listAllNonApprovers() throws SocialEAIException{
		String method = "listAllApprovers";
		EAILogger.debug(className, className + ", " + method);
		List<SocialProfileData> users=new ArrayList<SocialProfileData>();
		String objectclass = "";
		try {
			objectclass = PropertiesManager.getInstance().getProperty(SAM_LDAP_OBJECT_CLASS);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + SAM_LDAP_OBJECT_CLASS);
		}
		// String filter = "(&(objectClass=groupOfNames)(cn=socialEAIApprover))"; //listing group
		String filter = "(&(objectclass="+objectclass+")(!(memberof=socialEAIApprover)))";
		ArrayList<String> returnAttributes=m_returnAttributes;
		String base="";
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnAttr = null;
		if (returnAttributes != null && returnAttributes.size() > 0) {
			returnAttr = new String[returnAttributes.size()];
			for (int i = 0; i < returnAttributes.size(); i++) {
				returnAttr[i] = returnAttributes.get(i);
			}
			controls.setReturningAttributes(returnAttr);
		} else {
			returnAttr = new String[] { "uid", "mail", "cn", "sn" };
		}
		NamingEnumeration<SearchResult> results;
		try {
			results = m_ldapConn.getConnection()
					.search(base, filter, controls);
			while (results.hasMore()) {
				SocialProfileData user = new SocialProfileData();
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
			//	System.out.println("=====attribtues===="+attributes.toString());
				String uid = null;
				if (attributes.get("uid") != null && attributes.get("uid").get(0) != null) {
					uid = (String) attributes.get("uid").get(0);
				}
				String cn = null;
				if (attributes.get("cn") != null && attributes.get("cn").get(0) != null) {
					cn = (String) attributes.get("cn").get(0);
				}
				String sn = null;
				if (attributes.get("sn") != null && attributes.get("sn").get(0) != null) {
					sn = (String) attributes.get("sn").get(0);
				}
				String mail = null;
				if (attributes.get("mail") != null && attributes.get("mail").get(0) != null) {
					mail = (String) attributes.get("mail").get(0);
				}
				user.setId(uid);
				user.setFirstName(cn);
				user.setLastName(sn);
				user.setEmail(mail);
				users.add(user);
			}
		} catch (NamingException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
			String msg[] = { e.getMessage() };
			throw new SocialEAIException(ErrorCodes.SOCEAI108E, msg);
		} finally {
			try {
				if (m_ldapConn != null && m_ldapConn.getConnection() != null)
					m_ldapConn.getConnection().close();
			} catch (NamingException e) {
				EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
				e.printStackTrace();
				EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			}
		}	
		return users;
	}
	
}

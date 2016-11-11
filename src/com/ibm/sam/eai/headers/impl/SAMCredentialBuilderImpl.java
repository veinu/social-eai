package com.ibm.sam.eai.headers.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.customer.user.data.model.User;
import com.ibm.sam.eai.headers.SAMCredentialBuilder;
import com.ibm.sam.eai.ldap.impl.LdapConnectionManager;
import com.ibm.sam.eai.ldap.impl.LdapDaoImpl;
import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;
import com.tivoli.pd.jadmin.PDUser;
import com.tivoli.pd.jazn.PDAuthorizationContext;
import com.tivoli.pd.jazn.PDPrincipal;
import com.tivoli.pd.jutil.PDAttrValue;
import com.tivoli.pd.jutil.PDAttrValueList;
import com.tivoli.pd.jutil.PDContext;
import com.tivoli.pd.jutil.PDException;
import com.tivoli.pd.jutil.PDMessages;
import com.tivoli.pd.jutil.PDRgyUserName;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class SAMCredentialBuilderImpl implements SAMCredentialBuilder {

	private static final String className = SAMCredentialBuilderImpl.class
			.getName();

	private static PDAuthorizationContext ctx = null;

	public final static String SAM_API_CONFIG_FILE = "SAM_API_CONFIG_FILE";
	public final static String SAM_EPAC_AUTH_LEVEL = "SAM_EPAC_AUTH_LEVEL";
	public final static String SAM_EPAC_EAI_PAC_HEADER = "SAM_EPAC_EAI_PAC_HEADER";
	public final static String SAM_EPAC_EAI_REDIRECT_URL_HEADER = "SAM_EPAC_EAI_REDIRECT_URL_HEADER ";
	public final static String SAM_POLICY_CONFIG_FILE = "SAM_POLICY_CONFIG_FILE";
	public final static String SAM_LDAP_SUFFIX = "sam.ldap.suffix";
	public final static String SAM_LDAP_ADMIN_USER_NAME = "sam.ldap.admin.user-name";
	public final static String SAM_LDAP_ADMIN_USER_PASSWORD = "sam.ldap.admin.user-password";
	public final static String SAM_LDAP_USER_GROUP = "sam.ldap.attribute.user-group";
	public final static String SAM_LDAP_APPROVER_GROUP = "sam.ldap.attribute.approver-group";

	public SAMCredentialBuilderImpl() {
		if (ctx == null) {
			URL propsUrl = null;
			String samPolicyConfigFile = "";
			String username = "";
			String password = "";
			try {
				samPolicyConfigFile = PropertiesManager.getInstance()
						.getProperty(SAM_POLICY_CONFIG_FILE);
				username = PropertiesManager.getInstance().getProperty(
						SAM_LDAP_ADMIN_USER_NAME);
				password = PropertiesManager.getInstance().getProperty(
						SAM_LDAP_ADMIN_USER_PASSWORD);
				EAILogger.debug(className, className
						+ ", samPolicyConfigFile= " + samPolicyConfigFile
						+ ", Username= " + username);
				propsUrl = new URL("file", "", samPolicyConfigFile);
				EAILogger.debug(className, className + ", propsUrl= "
						+ propsUrl);
				Locale myLocale = new Locale("ENGLISH", "US");
				ctx = new PDAuthorizationContext(myLocale, username,
						password.toCharArray(), propsUrl);
			} catch (SocialEAIException e) {
				e.printStackTrace();
				EAILogger.debug(
						className,
						className + ", EXCEPTION = "
								+ Utils.getStackTraceString(e));
				EAILogger
				.error(className, className + ", " + e.getMessage(), e);
				// throw new ServletException(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(
						className,
						className + ", EXCEPTION = "
								+ Utils.getStackTraceString(e));
				EAILogger
				.error(className, className + ", " + e.getMessage(), e);
			} catch (PDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				EAILogger.debug(
						className,
						className + ", EXCEPTION = "
								+ Utils.getStackTraceString(e));
				EAILogger
				.error(className, className + ", " + e.getMessage(), e);
			}
		}
	}

	public Object getPDPrincipal(HttpServletRequest req) {
		String method = "getPDPrincipal";
		EAILogger.debug(className, className + ", " + method + "Input: req="
				+ req);

		PDPrincipal principal = null;
		String username = "unauthenticated"; // ??? unauthenticated user or any
		// other defined user?
		String authLevel = null;
		try {
			principal = new PDPrincipal(ctx, username);
			authLevel = PropertiesManager.getInstance().getProperty(
					SAM_EPAC_AUTH_LEVEL);
			EAILogger.debug(className, className + ", authLevel= " + authLevel);
			if (authLevel != null) {
				ArrayList list = new ArrayList();
				list.add(new PDAttrValue(ctx, authLevel));
				PDAttrValueList attrs = new PDAttrValueList(ctx, list);
				PDPrincipal temp = principal.addAttribute(ctx,
						"AUTHENTICATION_LEVEL", attrs);
				if (temp != null) {
					principal = temp;
				}
			}
		} catch (PDException e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
		}

		EAILogger.debug(className, className + ", " + method + ", principal= "
				+ principal);

		return principal;
	}

	public void setEAIHeaders(Object principal, HttpServletResponse resp) {
		String method = "setEAIHeaders";
		EAILogger.debug(className, className + ", " + method
				+ "Input: principal=" + principal);
		EAILogger.debug(className, className + ", " + method + "Input: resp="
				+ resp);
		PDPrincipal p = (PDPrincipal) principal;

		String eaiPACHeader = null;
		String eaiRedirectURLHeader = null;

		try {

			eaiPACHeader = PropertiesManager.getInstance().getProperty(
					SAM_EPAC_EAI_PAC_HEADER);
			eaiRedirectURLHeader = PropertiesManager.getInstance().getProperty(
					SAM_EPAC_EAI_REDIRECT_URL_HEADER);

		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
		}

		EAILogger.debug(className, className + ", " + method + "eaiPACHeader="
				+ eaiPACHeader);
		EAILogger.debug(className, className + ", " + method
				+ "eaiRedirectURLHeader=" + eaiRedirectURLHeader);

		if (eaiPACHeader != null) {
			try {
				resp.setHeader(eaiPACHeader, PACToString(p.getPAC(ctx)));
				// resp.setHeader("am-fim-eai-redir-url", "???"); //??? redirect
				// url to go here or this is not required
			} catch (PDException e) {
				e.printStackTrace();
				EAILogger.debug(
						className,
						className + ", EXCEPTION = "
								+ Utils.getStackTraceString(e));
				EAILogger.error(className,
						className + ", " + method + ", " + e.getMessage(), e);
			}
		}

	}

	public void setEAIHeaders(HttpServletResponse resp) {
		// TODO Auto-generated method stub

	}

	private String PACToString(byte[] pac) {

		String method = "PACToString";
		EAILogger.debug(className, className + ", " + method + "Input: pac="
				+ pac);

		String pacString = "";
		StringTokenizer tok = new StringTokenizer(new String(pac), "\r\n");
		while (tok.hasMoreTokens()) {
			pacString += tok.nextToken();
		}

		EAILogger.debug(className, className + ", " + method + "pacString="
				+ pacString);

		return pacString;
	}

	public PDAuthorizationContext getPDAuthorizationContext() {
		return ctx;
	}

	public boolean addTAMUser(User user) {
		String method = "addTAMUser";
		EAILogger.debug(className, className + ", " + method + "Input: User"
				+ user.getSocialProfileData());
		boolean addStatus = false;

		String rgySuffix = "";
		String userGroup = "";
		try {
			rgySuffix = PropertiesManager.getInstance().getProperty(
					SAM_LDAP_SUFFIX);
			userGroup = PropertiesManager.getInstance().getProperty(
					SAM_LDAP_USER_GROUP);
		} catch (SocialEAIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			EAILogger.debug(
					className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e1));
			EAILogger.error(className, className + ", " + e1.getMessage(), e1);
		}
		PDMessages msgs = new PDMessages();
		PDRgyUserName pdRgyUserName = null;
		ArrayList<String> groupList = new ArrayList<String>();

		String firstname = user.getSocialProfileData().getFirstName();
		String lastname = user.getSocialProfileData().getLastName();
		String userPassword = user.getUserPassword();
		String email = user.getSocialProfileData().getEmail();
		boolean ssoUser = true;
		boolean pwdPolicy = true;

		String rgyName = "uid=" + email + "," + rgySuffix;
		pdRgyUserName = new PDRgyUserName(rgyName, firstname, lastname);
		groupList.add(userGroup);
		try {
			PDUser.createUser(ctx, email, pdRgyUserName, null, userPassword.toCharArray(), groupList, ssoUser,
					pwdPolicy, msgs);
			PDUser pdUser = new PDUser(ctx, email, msgs);
			pdUser.setAccountValid(ctx, false, msgs);
			LdapDaoImpl ldapDao=new LdapDaoImpl(LdapConnectionManager.getInstance().getConnection());
			ldapDao.addLDAPUserBasicAttribtues(user);
			addStatus = true;
		} catch (PDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className,className + ", EXCEPTION = "+ Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + e.getMessage(), e);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className,className + ", EXCEPTION = "+ Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + e.getMessage(), e);
		}
		return addStatus;
	}
	
	public boolean updateTAMUserStatus(User user, boolean status) {
		String method = "updateTAMUserStatus";
		EAILogger.debug(className, className + ", " + method + "Update TAM User Status, Input: User"
				+ user.getSocialProfileData());
		boolean updateStatus = false;
		PDMessages msgs = new PDMessages();
		String email = user.getSocialProfileData().getEmail();
		try {
			PDUser pdUser = new PDUser(ctx, email, msgs);
			pdUser.setAccountValid(ctx, status, msgs);
			LdapDaoImpl ldapDao=new LdapDaoImpl(LdapConnectionManager.getInstance().getConnection());
			ldapDao.updateLDAPUserStatus(user, status);
			updateStatus = true;
		} catch (PDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className,className + ", EXCEPTION = "+ Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + e.getMessage(), e);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className,className + ", EXCEPTION = "+ Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + e.getMessage(), e);
		}
		return updateStatus;
	}
}

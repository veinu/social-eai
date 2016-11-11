package com.ibm.sam.eai.social.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.customer.user.data.model.User;
import com.customer.user.data.provider.CustomerDataProviderFactory;
import com.ibm.sam.eai.database.impl.ApprovalInfo;
import com.ibm.sam.eai.database.impl.DBConnection;
import com.ibm.sam.eai.headers.impl.SAMCredentialBuilderImpl;
import com.ibm.sam.eai.ldap.impl.LdapConnectionManager;
import com.ibm.sam.eai.ldap.impl.LdapDaoImpl;
import com.ibm.sam.eai.social.exception.RedirectException;
import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.model.SocialProfileData;
import com.ibm.sam.eai.social.provider.SocialLoginProvider;
import com.ibm.sam.eai.social.provider.SocialLoginProviderFactory;
import com.ibm.sam.eai.social.resources.ErrorCodes;
import com.ibm.sam.eai.social.resources.ResponseCodes;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;

public class SocialLoginServlet extends HttpServlet {
	private static final String AMAZON_SHORT = "az";
	private static final String LINKEDIN_SHORT = "li";
	private static final String FACEBOOK_SHORT = "fb";
	private static final String REDIRECT_URI = "&redirect_uri=";
	private static final String CLIENT_ID = "&client_id=";
	private static final String AMAZON = "amazon";
	private static final String LINKEDIN = "linkedin";
	private static final String FACEBOOK = "facebook";
	private static final String REDIRECT_URL = "redirectURL";
	private static final String ERROR_CODE = "errorCode";
	private static final String ERROR_MSG = "errorMsg";
	private static final String LANDING_JSP = "SocialEAILanding.jsp";
	// final static String TARGET = "target";
	final static String SOCIAL_PROVIDER_NAME = "socialProviderName";
	// final static String PORTAL_SRC_URL = "portalSourceUrl";
	final static String PORTAL_SRC_URL = "sourceUrl";
	final static String PORTAL_TRGT_URL = "targeturl";
	final static String LOCALE = "locale";
	final static String OPERATION = "operation";
	final static String USER = "User";
	final static String ERROR_DIV_TAG_ID = "targetErrorElementId";
	final static String OPERATION_CONNECT = "connect";
	final static String OPERATION_REG_COMPLETED = "registrationcomplete";
	final static String OPERATION_REG_PENDING = "registrationpending";

	final static String FIRST_NAME = "firstName";
	final static String LAST_NAME = "lastName";
	final static String SOCIAL_EMAIL = "socialEmail";
	final static String SOCIAL_ID = "socialId";

	public static final String SAM_LDAP_ATTRIBUTE_ALL = "sam.ldap.attribute.all";
	public static final String SAM_LDAP_ATTRIBUTE_USER_STATUS = "sam.ldap.attribute.user-status";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID = "sam.ldap.attribute.user-social-id";
	public static final String SAM_LDAP_ATTRIBUTE_USER_SOCIAL_EMAIL = "sam.ldap.attribute.user-social-email";
	public static final String SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL = "sam.ldap.attribute.user-primary-email";
	public static final String SAM_LDAP_ATTRIBUTE_USER_LAST_ACCESS = "sam.ldap.attribute.user-last-access-timpstanp";
	public static final String SAM_LDAP_ATTRIBUTE_USER_UID = "sam.ldap.attribute.user-uid";
	public final static String GROUP_MEMBER_ATTRIBUTE = "sam.ldap.attribute.group-member";
	public final static String GROUP_FILTER = "sam.ldap.attribute.group-filter";
	public final static String SAM_API_CONFIG_FILE = "SAM_API_CONFIG_FILE";
	public final static String SAM_EAI_UESR_IS_HEADER = "eai-user-id-header";
	public final static String SAM_EAI_REDIR_URL_HEADER = "eai-redir-url-header";
	public final static String SAM_LDAP_USER_GROUP = "sam.ldap.attribute.user-group";
	public final static String SAM_LDAP_APPROVER_GROUP = "sam.ldap.attribute.approver-group";
	public final static String SAM_LDAP_ADMIN_GROUP = "sam.ldap.attribute.admin-group";
	
	// Commented to avoid error due to deletion of sce jars
	/*
	 * String SCE_PROPERTIES_LOCATION =
	 * (String)SCEProperty.getInstance().getProperty("SCE_PROPERTIES_LOCATION");
	 * String SCE_ERROR_MSG_BUNDLE =
	 * (String)SCEProperty.getInstance().getProperty("SCE_ERROR_MSG_BUNDLE");
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String className = SocialLoginServlet.class.getName();

	@SuppressWarnings("unused")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String method = "doPost";
		EAILogger.debug(className, className + ", " + method);
		EAILogger.debug(className, "*****************************************SocialLoginServlet*******************************************");
		String redirectURL = null;
		// String target = request.getParameter(TARGET);
		String socialProviderName = request.getParameter(SOCIAL_PROVIDER_NAME);
		if(socialProviderName==null){
			socialProviderName=(String) request.getAttribute("socialProviderName");
		}
		String locale = request.getParameter(LOCALE);
		if(locale==null){
			Object obj = request.getAttribute("locale");
			if(obj!=null){
				locale= obj.toString();
			}
		}
		String operation = request.getParameter(OPERATION);
		if(operation==null){
			Object obj = request.getAttribute("operation");
			if(obj!=null){
				operation= obj.toString();
			}
		}
		String errorDivTagID = request.getParameter(ERROR_DIV_TAG_ID);
		if(errorDivTagID==null){
			Object obj = request.getAttribute("targetErrorElementId");
			if(obj!=null){
				errorDivTagID= obj.toString();
			}
		}
		HttpSession session = request.getSession(true);
		
		String firstName = "";
		String lastName = "";
		String socialEmail = "";
		String socialId = "";

		if (operation != null) {
			if (operation.equalsIgnoreCase(OPERATION_REG_PENDING) || operation.equalsIgnoreCase("login")) {
				firstName = request.getParameter(FIRST_NAME);
				if(firstName==null){
					Object obj = request.getAttribute("firstName");
					if(obj!=null){
						firstName= obj.toString();
					}
				}
				lastName = request.getParameter(LAST_NAME);
				if(lastName==null){
					Object obj = request.getAttribute("lastName");
					if(obj!=null){
						lastName= obj.toString();
					}
				}
				socialEmail = request.getParameter(SOCIAL_EMAIL);
				if(socialEmail==null){
					Object obj = request.getAttribute("socialEmail");
					if(obj!=null){
						socialEmail= obj.toString();
					}
				}
				socialId = request.getParameter(SOCIAL_ID);
				if(socialId==null){
					Object obj = request.getAttribute("socialId");
					if(obj!=null){
						socialId= obj.toString();
					}
				}
			}
		}

		// This is when Access Code is returned...
		String code = request.getParameter("code");
		if(code==null){
			Object obj = request.getAttribute("code");
			if(obj!=null){
				code= obj.toString();
			}
		}
		Object obj1 = request.getAttribute("provider");
		String provider = null;
			if (obj1 != null) {
				provider = obj1.toString();
			}

		String userid = request.getParameter(USER);
		if(userid == null){
			Object obj = request.getAttribute("User");
			if(obj!=null){
				userid= obj.toString();
			}
		}
		
		String userPassword = null;
		Object obj2 = session.getAttribute("userpassword");
		if(obj2!=null){
			userPassword= obj2.toString();
		}
		String registrationConsent = null;
		Object obj3 = session.getAttribute("registerme"); 
		if(obj2!=null){
			registrationConsent= obj3.toString();
		}
		String nativePassword = null;
		Object obj4 = session.getAttribute("nativepassword");
		if(obj4!=null){
			nativePassword= obj4.toString();
		}
		String linkingConsent = null;
		Object obj5 = session.getAttribute("linkmyaccount");
		if(obj5!=null){
			linkingConsent= obj5.toString();
		}
		
		// https://wwwut.sce.com/wps/socialeai/sociallogin?operation=registrationcomplete&User=ZBO4KO7PCH4

		EAILogger.debug(className, className + ", " + method + ", socialProviderName= " + socialProviderName);
		EAILogger.debug(className, className + ", " + method + ", locale= " + locale);
		EAILogger.debug(className, className + ", " + method + ", operation= " + operation);
		EAILogger.debug(className, className + ", " + method + ", provider=" + provider);
		EAILogger.debug(className, className + ", " + method + ", code=" + code);
		EAILogger.debug(className, className + ", " + method + ", userid=" + userid);
		EAILogger.debug(className, className + ", " + method + ", errorDivTagID=" + errorDivTagID);
		EAILogger.debug(className, className + ", " + method + ", firstName=" + firstName);
		EAILogger.debug(className, className + ", " + method + ", lastName=" + lastName);
		EAILogger.debug(className, className + ", " + method + ", socialEmail=" + socialEmail);
		EAILogger.debug(className, className + ", " + method + ", socialId=" + socialId);
		EAILogger.debug(className, className + ", " + method + ", registrationConsent=" + registrationConsent);
		EAILogger.debug(className, className + ", " + method + ", linkingConsent=" + linkingConsent);
		
		try {
			SocialLoginProvider loginProvider = null;
			if (operation != null) {
				session.setAttribute(OPERATION, operation);
			}
			if (locale != null) {
				session.setAttribute(LOCALE, locale);
			}
			if (errorDivTagID != null) {
				session.setAttribute(ERROR_DIV_TAG_ID, errorDivTagID);
			}

			String errorCode = null;
			String respCode = null;
			String errormsg = null;

			if (code == null && provider != null) {
				// Case when user login and does not provide consent
				EAILogger.info(className, className + ", " + method + ", User did not provided consent.");
				handleNoUserConsent(request, response, socialProviderName, locale, operation, session);
			} else if (code == null) {
				// Case when user login and provide consent. Now getting auth
				// code.
				// Also case when operation = registrationcomplete or
				// registrationpending
				EAILogger.info(className, className + ", " + method + ", code is null.");
				if (operation != null
						&& (operation.equalsIgnoreCase(OPERATION_REG_COMPLETED) || operation
								.equalsIgnoreCase(OPERATION_REG_PENDING))) {
					EAILogger.info(className, className + ", " + method + ", Registration completed or pending.");
					if (userid == null || userid.equalsIgnoreCase("")
							|| locale == null || locale.equalsIgnoreCase("")) {
						EAILogger.info(className, className + ", " + method + ", userid, portalSrcUrl or locale is null.");
						handleInvalidInputsRegCompleted(request, response,
								userid, locale, session);
					} else {
						if (operation.equalsIgnoreCase(OPERATION_REG_PENDING)) {
							handlePendingUser(request, response, userid,
									firstName, lastName, socialEmail, socialId,
									socialProviderName, session);
						} else if (operation
								.equalsIgnoreCase(OPERATION_REG_COMPLETED)) {
							handleNewlyRegisteredUser(request, response,
									userid, session);
						}
					}
				} else if (socialProviderName == null
						|| (socialProviderName != null && socialProviderName
						.trim().equalsIgnoreCase(""))
						|| locale == null
						|| (locale != null && locale.trim()
						.equalsIgnoreCase(""))
						|| operation == null
						|| (operation != null && operation.trim()
						.equalsIgnoreCase(""))) {
					EAILogger.info(className, className + ", " + method + ", Handling invalid input..");
					handleInvalidInputs(request, response, socialProviderName,
							locale, operation, session);
				} else {
					try {
						// Getting auth code.
						EAILogger.info(className, className + ", " + method + ", Getting Auth Code URL..");
						String url = getAuthCodeUrl(request, response, session,
								socialProviderName);
						response.sendRedirect(url);
					} catch (SocialEAIException e) {
						EAILogger.error(className, className + ", " + method + ", " + e.getMessage(), e);
						errorCode = e.getCode();
						EAILogger.debug(className, className + ", " + method + ", errorCode = " + errorCode);
						session.setAttribute(ERROR_CODE, errorCode);
						errormsg = getErrorMesssage(errorCode, null, null);
						EAILogger.debug(className, className + ", " + method + ", errormsg = " + errormsg);
						session.setAttribute(ERROR_MSG, errormsg);
						redirectURL = getTargetURL(request, response, null,
								null, null, errorCode);
					}

					session.setAttribute(REDIRECT_URL, redirectURL);
					EAILogger.debug(className, className + ", " + method + ", redirectURL = " + redirectURL);
					EAILogger.debug(className, className + ", " + method + ", Redirecting to landing page " + LANDING_JSP);
					request.getRequestDispatcher(LANDING_JSP).include(request,
							response);
				}
			} else {
				// Case when user login, provide consent and recieved auth code.
				// Now getting social profile data and processing business logic

				SocialProfileData profileData = null;
				// com.ibm.sam.eai.ldap.impl.LdapDaoImpl ldapDao;
				HashMap<String, List<String>> attrValues = null;
				try {
					profileData = (SocialProfileData) session.getAttribute("socialProfileData");
					if(profileData == null){
					profileData = getSocialProfileData(request, response,
							session, code, provider);
					}
					EAILogger.debug(className, className + ", " + method + ", profileData = " + profileData.toString());
					session.setAttribute("profileData", profileData.toString());

					operation = (session.getAttribute(OPERATION) != null) ? (String) session
							.getAttribute(OPERATION) : "";
							EAILogger.debug(className, className + ", " + method + ", SESSION operation= " + operation);
						
							//adding new TAM User and adding its LDAP attributes
							if(registrationConsent != null && registrationConsent.equals("yes")){
								if(userPassword!=null){
									if((userPassword.equals("")==false)){
										User newuser=new User();
										newuser.setSocialId(profileData.getId());
										newuser.setSocialProfileData(profileData);
										newuser.setUserPassword(userPassword);
										SAMCredentialBuilderImpl samCredentialBuilderImpl=new SAMCredentialBuilderImpl();
										boolean addStatus=samCredentialBuilderImpl.addTAMUser(newuser);
										EAILogger.info(className, className + ", " + method+"New User added to SAM status="+addStatus);
										LdapDaoImpl ldapDaoImpl=new LdapDaoImpl(LdapConnectionManager.getInstance().getConnection());
										boolean updateStatus=ldapDaoImpl.addLDAPUserSocialAttributes(newuser);
										
										Timestamp time_Created = new Timestamp(new Date().getTime());
										//TODO add user details in table for Approval flow 
										ApprovalInfo approvalInfo = new ApprovalInfo(profileData.getId(), profileData.getFirstName(), profileData.getLastName(), profileData.getEmail(), time_Created );
										DBConnection dbConnection= new DBConnection();
										dbConnection.getConnection();
										dbConnection.insertApprovalRequest(approvalInfo);
										dbConnection.closeConnection();
										
										EAILogger.info(className, className + ", " + method+"New User LDAP attribtues added status="+updateStatus);
										addStatus=samCredentialBuilderImpl.updateTAMUserStatus(newuser, false);
										EAILogger.info(className, className + ", " + method+"New User account disabled, pending for verfication="+addStatus);
										session.removeAttribute("userpassword");
										session.removeAttribute("registerme");
									}
								}
							}
							else if(registrationConsent != null && registrationConsent.equals("no")){
								//when user don't want to register
								EAILogger.info(className, className + ", " + method+"User don't want to register");
								
							}
							else if(linkingConsent != null && linkingConsent.equals("yes")){ //linking social id with native id
								if(nativePassword!=null){
									if((nativePassword.equals("")==false)){
										User newuser=new User();
										newuser.setSocialId(profileData.getId());
										newuser.setSocialProfileData(profileData);
										newuser.setUserPassword(nativePassword);
										LdapDaoImpl ldapDaoImpl=new LdapDaoImpl(LdapConnectionManager.getInstance().getConnection());
										String userpwd=ldapDaoImpl.getLdapObjectPassword(profileData.getEmail());
										if(nativePassword.equals(userpwd)){
											boolean updateStatus = ldapDaoImpl.addLDAPUserSocialAttributes(newuser);
											EAILogger.info(className, className + ", " + method+"Social Id linked with Native ID status="+updateStatus);
										}
										session.removeAttribute("nativepassword");	
										session.removeAttribute("linkmyaccount");
									}
								}
							}
							else if(linkingConsent != null && linkingConsent.equals("no")){
								//when user don't want to link his account
								EAILogger.info(className, className + ", " + method+"User don't want to link id");
							}
							
							// TEMP_DATA
							// User customerUser = getCustomerUser(profileData);
							// session.setAttribute("UserData",
							// customerUser.toString());
							// if(customerUser!=null){
							// EAILogger.debug(className, className + ", " + method +
							// ", customerUser = " + customerUser.toString());
							// }else{
							// EAILogger.debug(className, className + ", " + method +
							// ", customerUser is null.");
							// }

							User socialIDExistUser = getUser_SocialIdExist(profileData);
							User matchedPrimayEmailUserNotAssociated = getUser_MatchedPrimaryEmailOfAnyExistingUser(
									profileData, false);
							User matchedPrimayEmailUserAssociated = getUser_MatchedPrimaryEmailOfAnyExistingUser(
									profileData, true);
							List<String> groups = CustomerDataProviderFactory.getInstance().getCustomerDataProvider()
							.getUserGroupList(socialIDExistUser.getSocialProfileData().getEmail());
							EAILogger.debug(className, className + ", " + method + ", socialIDExistUser = " + socialIDExistUser);
							EAILogger.debug(className, className + ", " + method + ", matchedPrimayEmailUserNotAssociated = "
									+ matchedPrimayEmailUserNotAssociated);
							EAILogger.debug(className, className + ", " + method + ", matchedPrimayEmailUserAssociated = "
									+ matchedPrimayEmailUserAssociated);
							EAILogger.debug(className, className + ", " + method + ", Group Membership = " + groups.toString());
							String approverGroup = "";
							String adminGroup="";
							approverGroup = PropertiesManager.getInstance().getProperty(SAM_LDAP_APPROVER_GROUP);
							adminGroup = PropertiesManager.getInstance().getProperty(SAM_LDAP_ADMIN_GROUP);
							
							//TODO Forwarding User to Approval or Admin Home page
							if(groups.contains(approverGroup)){
								session.setAttribute("isApprover", "true");
								request.getRequestDispatcher("/UI_Pages/approvalPending.jsp").forward(request, response);
								if(groups.contains(adminGroup)){
									session.setAttribute("isAdmin", "true");
									request.getRequestDispatcher("/UI_Pages/manageApprover.jsp").forward(request, response);
								}
							}
							
							if (socialIDExistUser == null
									&& matchedPrimayEmailUserAssociated != null) {
								EAILogger.info(className,className+ ", "+ method
										+ ", SocialId doesn't Exists, Social email matches with primary email of some existing user, which is associated with some other Social Id.");
								errorCode = ErrorCodes.SOCEAI100E;
								EAILogger.debug(className, className + ", " + method
										+ ", errorCode = " + errorCode);
								errormsg = getErrorMesssage(errorCode, profileData,
										matchedPrimayEmailUserAssociated);
								EAILogger.debug(className, className + ", " + method
										+ ", errormsg = " + errormsg);
								session.setAttribute(ERROR_MSG, errormsg);
								session.setAttribute(ERROR_CODE, errorCode);
								//user is registered using other social service provider
								
								// No call to redirect componenet
								redirectURL = "";
							} else if (socialIDExistUser == null
									&& matchedPrimayEmailUserNotAssociated == null) {
								EAILogger.info(className, className + ", " + method
										+ ", SocialId doesn't Exists, Social email doesn't match primary email of any existing user.");
								respCode = ResponseCodes.SOCEAI107I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method + ", profileDataJson= " + profileDataJson);
								
								 //for unregistered user, ask if they want to register
								EAILogger.info(className, className + ", " + method + ", Calling register.jsp");
								session.setAttribute("socialProfileData", profileData);
								request.getRequestDispatcher("/UI_Pages/register.jsp").forward(request, response);
								
								//redirectURL = getTargetURL(request, response, profileData, null, ResponseCodes.SOCEAI107I, null);

							} else if (socialIDExistUser == null
									&& matchedPrimayEmailUserNotAssociated != null) {
								// User can get registered and login should be granted
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId doesn't Exists, Social email matches with primary email of some existing user, which is not associated with some other Social Id.");
								respCode = ResponseCodes.SOCEAI108I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method + ", profileDataJson= " + profileDataJson);
								
								//for registered user ask to link their account
								EAILogger.info(className, className + ", " + method + ", Calling linkid.jsp");
								session.setAttribute("socialProfileData", profileData);
								request.getRequestDispatcher("/UI_Pages/linkid.jsp").forward(request, response);
								
								//redirectURL = getTargetURL(request, response, profileData, matchedPrimayEmailUserNotAssociated.getAttributeValues(), ResponseCodes.SOCEAI108I, null);

							} else if (socialIDExistUser != null && operation != null
									&& operation.equalsIgnoreCase(OPERATION_CONNECT)) {
								// The Social ID has already been registered or
								// connected to another User ID. Please log out and log
								// in woth your Social ID.
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId Exists, Operation is Connect. The Social ID has already been registered or connected to another  User ID. Please log out and log in woth your Social ID.");
								errorCode = ErrorCodes.SOCEAI118E;
								EAILogger.debug(className, className + ", " + method + ", errorCode = " + errorCode);
								errormsg = getErrorMesssage(errorCode, profileData,
										null);
								EAILogger.debug(className, className + ", " + method + ", errormsg = " + errormsg);
								session.setAttribute(ERROR_MSG, errormsg);
								session.setAttribute(ERROR_CODE, errorCode);
								// No call to redirect componenet
								redirectURL = "";
							} else if (socialIDExistUser != null
									&& socialIDExistUser.isSocialIdExists()
									&& socialIDExistUser.isActive()
									&& socialIDExistUser
									.isPrimaryEmailLinked2SocialId()
									&& socialIDExistUser.isSocialEmailIdPrimary()) {
								// All is good, user is registered user and login should be granted
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId Exists, User account is active, primary email is linked to social id and Social email is primaryEmail.");
								respCode = ResponseCodes.SOCEAI101I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method + ", profileDataJson= " + profileDataJson);
								//User it registered and account is linked, grant TAM login
								
								redirectURL = getTargetURL(request, response,
										profileData,
										socialIDExistUser.getAttributeValues(),
										ResponseCodes.SOCEAI101I, null);

								EAILogger.info(className, className + ", " + method + ", Granting SAM login..");
								updateLastAccessTime(profileData.getEmail());
								grantSAMLogin(request, response, socialIDExistUser,
										redirectURL);

							} else if (socialIDExistUser != null
									&& socialIDExistUser.isSocialIdExists()
									&& socialIDExistUser.isActive()
									&& socialIDExistUser
									.isPrimaryEmailLinked2SocialId()
									&& !socialIDExistUser.isSocialEmailIdPrimary()) {
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId Exists, User account is active, primary email is linked to social id and Social email is NOT primaryEmail.");
								respCode = ResponseCodes.SOCEAI102I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method
										+ ", profileDataJson= " + profileDataJson);
								redirectURL = getTargetURL(request, response,
										profileData,
										socialIDExistUser.getAttributeValues(),
										ResponseCodes.SOCEAI102I, null);
							} else if (socialIDExistUser != null
									&& socialIDExistUser.isSocialIdExists()
									&& socialIDExistUser.isActive()
									&& !socialIDExistUser
									.isPrimaryEmailLinked2SocialId()
									&& !socialIDExistUser.isSocialEmailIdPrimary()) {
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId Exists, User account is active, primary email is NOT linked to social id and Social email is NOT primaryEmail.");
								respCode = ResponseCodes.SOCEAI103I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method
										+ ", profileDataJson= " + profileDataJson);
								redirectURL = getTargetURL(request, response,
										profileData,
										socialIDExistUser.getAttributeValues(),
										ResponseCodes.SOCEAI103I, null);
							} else if (socialIDExistUser != null
									&& socialIDExistUser.isSocialIdExists()
									&& !socialIDExistUser.isActive()
									&& !socialIDExistUser
									.isPrimaryEmailLinked2SocialId()
									&& !socialIDExistUser.isSocialEmailIdPrimary()) {
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId Exists, User account is NOT active, primary email is NOT linked to social id and Social email is NOT primaryEmail.");
								respCode = ResponseCodes.SOCEAI104I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method
										+ ", profileDataJson= " + profileDataJson);
								redirectURL = getTargetURL(request, response,
										profileData,
										socialIDExistUser.getAttributeValues(),
										ResponseCodes.SOCEAI104I, null);
							} else if (socialIDExistUser != null
									&& socialIDExistUser.isSocialIdExists()
									&& !socialIDExistUser.isActive()
									&& socialIDExistUser
									.isPrimaryEmailLinked2SocialId()
									&& !socialIDExistUser.isSocialEmailIdPrimary()) {
								EAILogger.info(className, className	+ ", "	+ method
										+ ", SocialId Exists, User account is NOT active, primary email is linked to social id and Social email is NOT primaryEmail.");
								respCode = ResponseCodes.SOCEAI105I;
								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method
										+ ", profileDataJson= " + profileDataJson);
								redirectURL = getTargetURL(request, response,
										profileData,
										socialIDExistUser.getAttributeValues(),
										ResponseCodes.SOCEAI105I, null);
							} else if (socialIDExistUser != null
										&& socialIDExistUser.isSocialIdExists()
										&& !socialIDExistUser.isActive()
										&& socialIDExistUser
										.isPrimaryEmailLinked2SocialId()
										&& socialIDExistUser.isSocialEmailIdPrimary()) {
								EAILogger.info(className, className	+ ", "	+ method
											+ ", SocialId Exists, User account is NOT active, primary email is linked to social id and Social email is primaryEmail.");
									respCode = ResponseCodes.SOCEAI106I;
									String profileDataJson = getCustomePofileDataJson(
											profileData, provider, respCode);
									profileData.setCustomJSON(profileDataJson);
									EAILogger.info(className, className + ", " + method
											+ ", profileDataJson= " + profileDataJson);
									//TODO user account is pending for approval, ask to continue as a guest user
									
									redirectURL = getTargetURL(request, response,
											profileData,
											socialIDExistUser.getAttributeValues(),
											ResponseCodes.SOCEAI106I, null);
								}
							else {
								EAILogger.info(className, className	+ ", "	+ method
										+ ", Did not satisfied any business condition.");
								errorCode = ErrorCodes.SOCEAI116E;
								EAILogger.debug(className, className + ", " + method
										+ ", errorCode = " + errorCode);
								errormsg = getErrorMesssage(errorCode, profileData,
										null);
								EAILogger.debug(className, className + ", " + method
										+ ", errormsg = " + errormsg);
								session.setAttribute(ERROR_MSG, errormsg);
								session.setAttribute(ERROR_CODE, errorCode);

								String profileDataJson = getCustomePofileDataJson(
										profileData, provider, respCode);
								profileData.setCustomJSON(profileDataJson);
								EAILogger.info(className, className + ", " + method
										+ ", profileDataJson= " + profileDataJson);
								redirectURL = getTargetURL(request, response,
										profileData, null, null, errorCode);
							}

							EAILogger.debug(className, className + ", " + method
									+ ", redirectURL= " + redirectURL);


				} catch (SocialEAIException e) {
					e.printStackTrace();
					EAILogger.debug(className, className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
					EAILogger.error(className, className + ", " + method + ", "
							+ e.getMessage(), e);
					errorCode = e.getCode();
					EAILogger.debug(className, className + ", " + method
							+ ", errorCode = " + errorCode);
					session.setAttribute(ERROR_CODE, errorCode);
					errormsg = getErrorMesssage(errorCode, profileData, null);
					EAILogger.debug(className, className + ", " + method
							+ ", errormsg = " + errormsg);
					session.setAttribute(ERROR_MSG, errormsg);
					redirectURL = getTargetURL(request, response, profileData,
							attrValues, null, errorCode);
				} catch (Exception e) {
					e.printStackTrace();
					EAILogger.debug(className, className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
					EAILogger.error(className, className + ", " + method + ", "
							+ e.getMessage(), e);
					session.setAttribute(ERROR_CODE, ErrorCodes.SOCEAI116E);
					redirectURL = getTargetURL(request, response, profileData,
							attrValues, null, ErrorCodes.SOCEAI116E);
				}

				session.setAttribute(REDIRECT_URL, redirectURL);
				EAILogger.debug(className, className + ", " + method
						+ ", Redirecting to landing page " + LANDING_JSP);
				request.getRequestDispatcher(LANDING_JSP).include(request,
						response);
			}
		} catch (RedirectException e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
			EAILogger.error(className, className + ", " + method
					+ ", redirectURL = " + e.getRedirectUrl(), e);
			redirectURL = e.getRedirectUrl();
			session.setAttribute(REDIRECT_URL, redirectURL);
			request.getRequestDispatcher(LANDING_JSP)
			.include(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);

			// redirectURL = getTargetURL(request, response, null,
			// null,null,ErrorCodes.SOCEAI116E);
			// session.setAttribute(REDIRECT_URL, redirectURL);
			request.getRequestDispatcher(LANDING_JSP)
			.include(request, response);
		}
	}

	private void grantSAMLogin(HttpServletRequest request,
			HttpServletResponse response, User customerUser, String redirectURL)
					throws SocialEAIException {
		String method = "grantSAMLogin";

		if (customerUser != null) {
			EAILogger.debug(className, className + ", " + method
					+ "Input: customerUser=" + customerUser);
		} else {
			EAILogger.debug(className, className + ", " + method
					+ "Input: customerUser is null");
		}
		EAILogger.debug(className, className + ", " + method
				+ "Input: redirectURL=" + redirectURL);

		// Set the EPAC HEADERs.
		// SAMCredentialBuilderFactory samCredBuilderfactory =
		// SAMCredentialBuilderFactory.getInstance();
		// SAMCredentialBuilder credBuilder =
		// samCredBuilderfactory.getSAMCredentialBuilder();
		// Object principal = credBuilder.getPDPrincipal(request);
		// credBuilder.setEAIHeaders(response);
		// credBuilder.setEAIHeaders(principal, response);

		String eai_user_id_header = "";
		String eai_redir_url_header = "";
		String uidAttr = "";

		eai_user_id_header = PropertiesManager.getInstance().getProperty(
				SAM_EAI_UESR_IS_HEADER);
		eai_redir_url_header = PropertiesManager.getInstance().getProperty(
				SAM_EAI_REDIR_URL_HEADER);
		uidAttr = PropertiesManager.getInstance().getProperty(
				SAM_LDAP_ATTRIBUTE_USER_UID);

		EAILogger.debug(className, className + ", " + method
				+ ", eai_user_id_header = " + eai_user_id_header);
		EAILogger.debug(className, className + ", " + method
				+ ", eai_redir_url_header = " + eai_redir_url_header);
		EAILogger.debug(className, className + ", " + method + ", uidAttr = "
				+ uidAttr);

		String uid = "";
		if (customerUser.getAttributeValues() != null
				&& customerUser.getAttributeValues().get(uidAttr) != null
				&& customerUser.getAttributeValues().get(uidAttr).get(0) != null) {
			uid = customerUser.getAttributeValues().get(uidAttr).get(0);
		}
		EAILogger
		.debug(className, className + ", " + method + ", uid = " + uid);

		HttpSession session = request.getSession(true);
		session.setAttribute(REDIRECT_URL, redirectURL);

		EAILogger.debug(className, className + ", " + method
				+ ", Setting  eai_user_id_header to " + uid);
		response.setHeader(eai_user_id_header, uid);

		String reqURL = request.getRequestURL().toString();
		int lastInd = reqURL.lastIndexOf("/");
		String newURL = reqURL.substring(0, lastInd) + "/" + LANDING_JSP;

		EAILogger.debug(className, className + ", " + method + ", reqURL = "
				+ reqURL);
		EAILogger.debug(className, className + ", " + method + ", newURL = "
				+ newURL);

		// if(redirectURL != null && redirectURL.trim().length() > 0) {
		EAILogger.debug(className, className + ", " + method
				+ ", Setting  eai_redir_url_header to " + newURL);
		response.setHeader(eai_redir_url_header, newURL);
		// }

		response.setContentType("text/html");
		try {
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
		}

		EAILogger.debug(className, className + ", " + method
				+ ", Done grantSAMLogin.");
	}

	private String getCustomePofileDataJson(SocialProfileData profileData,
			String provider, String respCode) {
		String method = "getCustomePofileDataJson";
		String getCustomePofileDataJason = "";
		if (profileData != null) {
			EAILogger.debug(className, className + ", " + method
					+ "Input: profileData=" + profileData.toString());
		} else {
			EAILogger.debug(className, className + ", " + method
					+ "Input: profileData is null.");
		}
		EAILogger.debug(className, className + ", " + method
				+ "Input: provider=" + provider);
		EAILogger.debug(className, className + ", " + method
				+ "Input: respCode=" + respCode);

		getCustomePofileDataJason = "{\"email\":\"" + profileData.getEmail()
				+ "\",\"firstName\":\"" + profileData.getFirstName()
				+ "\",\"lastName\":\"" + profileData.getLastName()
				+ "\",\"id\":\"" + profileData.getId() + "\",\"provider\":\""
				+ provider + "\", \"responseCode\":\"" + respCode + "\"}";

		return getCustomePofileDataJason;
	}

	// TEMP_DATA temp
	/*private User getCustomerUser_TEMP(SocialProfileData profileData)
			throws SocialEAIException {
		String method = "getCustomerUser";
		if (profileData != null) {
			EAILogger.debug(className, className + ", " + method
					+ "Input: profileData=" + profileData.toString());
		} else {
			EAILogger.debug(className, className + ", " + method
					+ "Input: profileData is null.");
		}

		return getUser(profileData);
	}*/

	// TEMP_DATA Original
	/*private User getCustomerUser(SocialProfileData profileData)
			throws SocialEAIException {
		String method = "getCustomerUser";
		if (profileData != null) {
			EAILogger.debug(className, className + ", " + method
					+ "Input: profileData=" + profileData.toString());
		} else {
			EAILogger.debug(className, className + ", " + method
					+ "Input: profileData is null.");
		}

		return CustomerDataProviderFactory.getInstance()
				.getCustomerDataProvider().getUser(profileData);
	}*/

	private boolean updateLastAccessTime(String email)
			throws SocialEAIException {
		String method = "updateLastAccessTime";
		EAILogger.debug(className, className + ", " + method + "Input: email="
				+ email);

		return CustomerDataProviderFactory.getInstance()
				.getCustomerDataProvider().updateLastAccessTime(email);
	}

	// TEMP_DATA temp
	/*private SocialProfileData getSocialProfileData_TEMP(
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, String code, String provider)
					throws RedirectException, SocialEAIException {

		String method = "getSocialProfileData";
		EAILogger.debug(
				className,
				className + ", " + method + ", Input: code=" + code
				+ ", provider=" + provider + ", session="
				+ session.toString());

		String redirectURL = null;
		String jsonString = null;
		SocialLoginProvider loginProvider = null;
		String accessToken = null;
		String errorMsg = null;

		// try {
		String sp_provider = PropertiesManager.getInstance().getProperty(
				"sp_provider");
		String sp_email = PropertiesManager.getInstance().getProperty(
				"sp_email");
		String sp_firstName = PropertiesManager.getInstance().getProperty(
				"sp_firstName");
		String sp_lastName = PropertiesManager.getInstance().getProperty(
				"sp_lastName");
		String sp_id = PropertiesManager.getInstance().getProperty("sp_id");
		String sp_origJSON = PropertiesManager.getInstance().getProperty(
				"sp_origJSON");

		SocialProfileData socialProfileData = new SocialProfileData();

		socialProfileData.setEmail(sp_email);
		socialProfileData.setFirstName(sp_firstName);
		socialProfileData.setId(sp_id);
		socialProfileData.setProvider(sp_provider);
		socialProfileData.setLastName(sp_lastName);
		socialProfileData.setOrigJSON(sp_origJSON);

		EAILogger.info(className, className + ", " + method
				+ ", socialProfileData = " + socialProfileData.toString());
		return socialProfileData;
		// } catch (SocialEAIException e) {
		// //e.printStackTrace();
		// EAILogger.error(className, className + ", " + method + ", " +
		// e.getMessage(),e);
		// errorMsg = e.getMessage();
		// }

		// String[] msg = { errorMsg };
		// redirectURL = getTargetURL(request, response, null, null,
		// null,ErrorCodes.SOCEAI104E);
		//
		// //System.out.println(className+".getSocialProfileData: redirectURL::."+redirectURL);
		// EAILogger.debug(className, className + ", " + method +
		// ", redirectURL= " + redirectURL);
		// session.setAttribute(REDIRECT_URL, redirectURL);
		// throw new RedirectException(redirectURL);

	}*/

	// TEMP_DATA original
	// name modified
	private SocialProfileData getSocialProfileData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String code,
			String provider) throws RedirectException, SocialEAIException {

		String method = "getSocialProfileData";
		EAILogger.debug(
				className,
				className + ", " + method + ", Input: code=" + code
				+ ", provider=" + provider + ", session="
				+ session.toString());

		//String redirectURL = null;
		String jsonString = null;
		SocialLoginProvider loginProvider = null;
		String accessToken = null;
		//String errorMsg = null;

		// try {
		if (provider.equalsIgnoreCase(FACEBOOK)) {
			loginProvider = SocialLoginProviderFactory.getInstance()
					.getFacebookLoginProvider();
		} else if (provider.equalsIgnoreCase(LINKEDIN)) {
			loginProvider = SocialLoginProviderFactory.getInstance()
					.getLinkedinLoginProvider();
		} else if (provider.equalsIgnoreCase(AMAZON)) {
			loginProvider = SocialLoginProviderFactory.getInstance()
					.getAmazonLoginProvider();
		}

		//PropertiesManager pm = PropertiesManager.getInstance();

		// TEMP - START
		// String callForAccessToken = pm.getProperty("callForAccessToken");
		// EAILogger.debug(className, className + ", " + method +
		// ", callForAccessToken = " + callForAccessToken);
		// if(callForAccessToken!=null&&callForAccessToken.equalsIgnoreCase("true")){
		// accessToken = loginProvider.getAccessToken(code);
		// }
		// TEMP - END

		accessToken = loginProvider.getAccessToken(code);
		EAILogger.debug(className, className + ", " + method
				+ ", accessToken = " + accessToken);

		// TEMP - START
		// String callForSocialProfileData =
		// pm.getProperty("callForSocialProfileData");
		// EAILogger.debug(className, className + ", " + method +
		// ", callForSocialProfileData = " + callForSocialProfileData);
		// if(callForSocialProfileData!=null&&callForSocialProfileData.equalsIgnoreCase("true")){
		// jsonString = loginProvider.getUserData(accessToken);
		// }
		// TEMP - END

		jsonString = loginProvider.getUserData(accessToken);
		EAILogger.debug(className, className + ", " + method
				+ ", jsonString = " + jsonString);

		SocialProfileData socialProfileData = loginProvider
				.getSocialProfileData(jsonString);
		EAILogger.info(className, className + ", " + method
				+ ", socialProfileData = " + socialProfileData.toString());
		return socialProfileData;
		// } catch (SocialEAIException e) {
		// //e.printStackTrace();
		// EAILogger.error(className, className + ", " + method + ", " +
		// e.getMessage(),e);
		// errorMsg = e.getMessage();
		// }

		// String[] msg = { errorMsg };
		// redirectURL = getTargetURL(request, response, null, null,
		// null,ErrorCodes.SOCEAI104E);
		//
		// //System.out.println(className+".getSocialProfileData: redirectURL::."+redirectURL);
		// EAILogger.debug(className, className + ", " + method +
		// ", redirectURL= " + redirectURL);
		// session.setAttribute(REDIRECT_URL, redirectURL);
		// throw new RedirectException(redirectURL);

	}

	private String getAuthCodeUrl(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String target)
					throws RedirectException, SocialEAIException {

		String method = "getAuthCodeUrl";
		EAILogger.debug(className,
				className + ", " + method + ", Input: target=" + target
				+ ", session=" + session.toString());

		SocialLoginProvider sLProvider = null;
		String url = null;
		session.removeAttribute("profileData");
		session.removeAttribute("userData");

		if (target.equalsIgnoreCase(FACEBOOK)
				|| target.equalsIgnoreCase(FACEBOOK_SHORT)) {
			sLProvider = SocialLoginProviderFactory.getInstance()
					.getFacebookLoginProvider();
			url = sLProvider.getAccessCodeUrl() + CLIENT_ID
					+ sLProvider.getAppId() + REDIRECT_URI
					+ sLProvider.getRedirectUrl();
		} else if (target.equalsIgnoreCase(LINKEDIN)
				|| target.equalsIgnoreCase(LINKEDIN_SHORT)) {
			sLProvider = SocialLoginProviderFactory.getInstance()
					.getLinkedinLoginProvider();
			url = sLProvider.getAccessCodeUrl() + CLIENT_ID
					+ sLProvider.getAppId() + REDIRECT_URI
					+ sLProvider.getRedirectUrl();
		} else if (target.equalsIgnoreCase(AMAZON)
				|| target.equalsIgnoreCase(AMAZON_SHORT)) {
			sLProvider = SocialLoginProviderFactory.getInstance()
					.getAmazonLoginProvider();
			url = sLProvider.getAccessCodeUrl() + CLIENT_ID
					+ sLProvider.getAppId() + REDIRECT_URI
					+ sLProvider.getRedirectUrl();
		}
		if (sLProvider == null || sLProvider.getAccessCodeUrl() == null
				|| sLProvider.getAppId() == null
				|| sLProvider.getRedirectUrl() == null) {
			throw new SocialEAIException(ErrorCodes.SOCEAI112E,
					ErrorCodes.SOCEAI112E_DESC);
			// String[] msg = { target, sLProvider.getAccessCodeUrl(),
			// sLProvider.getAppId(), sLProvider.getRedirectUrl() };
			// String redirectURL = getTargetURL(request, response, null, null,
			// null, ErrorCodes.SOCEAI112E);
			//
			// session.setAttribute(REDIRECT_URL, redirectURL);
			// EAILogger.debug(className, className + ", " + method +
			// ", redirectURL= " + redirectURL);
			// // EAILogger.debug(className,redirectURL);
			// throw new RedirectException(redirectURL);
		}
		EAILogger.debug(className, className + ", " + method + ", url= " + url);
		return url;
	}

	private void handleInvalidInputs(HttpServletRequest request,
			HttpServletResponse response, String target, String locale,
			String operation, HttpSession session) throws ServletException,
			IOException {

		String method = "handleInvalidInputs";
		EAILogger.debug(className,
				className + ", " + method + ", Input: target=" + target
				+ ", locale=" + locale + ", operation=" + operation
				+ ", session=" + session.toString());

		String redirectURL;
		// EAILogger.debug(className,ErrorCodes.SOCEAI111E);
	//	String[] msg = { target, locale, operation };
		redirectURL = getTargetURL(request, response, null, null, null,
				ErrorCodes.SOCEAI111E);

		EAILogger.debug(className, className + ", " + method
				+ ", redirectURL= " + redirectURL);

		session.setAttribute(ERROR_CODE, ErrorCodes.SOCEAI111E);
		session.setAttribute(REDIRECT_URL, redirectURL);
		// EAILogger.debug(className,redirectURL);
		// return;
		request.getRequestDispatcher(LANDING_JSP).forward(request, response);
	}

	private void handleInvalidInputsRegCompleted(HttpServletRequest request,
			HttpServletResponse response, String userid, String locale,
			HttpSession session) throws ServletException, IOException {

		String method = "handleInvalidInputsRegCompleted";
		EAILogger.debug(className, className + ", " + method
				+ ", Input: userid=" + userid + ", locale=" + locale
				+ ", session=" + session.toString());

		String redirectURL = null;
		;

		redirectURL = getTargetURL(request, response, null, null, null,
				ErrorCodes.SOCEAI114E);

		EAILogger.debug(className, className + ", " + method
				+ ", redirectURL= " + redirectURL);
		session.setAttribute(ERROR_CODE, ErrorCodes.SOCEAI114E);
		session.setAttribute(REDIRECT_URL, redirectURL);

		// EAILogger.debug(className,redirectURL);
		// return;
		request.getRequestDispatcher(LANDING_JSP).forward(request, response);
	}

	private void handleNewlyRegisteredUser(HttpServletRequest request,
			HttpServletResponse response, String userid, HttpSession session)
					throws ServletException, IOException {

		User newlyCreatedUser = null;
		String method = "handleNewlyRegisteredUser";
		EAILogger.debug(className,
				className + ", " + method + ", Input: userid=" + userid
				+ " , session=" + session.toString());
		try {
			newlyCreatedUser = getNewlyCreatedUser(userid);
			EAILogger.debug(className, className + ", " + method
					+ ", newlyCreatedUser = " + newlyCreatedUser);
			String redirectURL = null;
			if (newlyCreatedUser != null) {
				redirectURL = getTargetURL(request, response, null,
						newlyCreatedUser.getAttributeValues(),
						ResponseCodes.SOCEAI110I, null);

				PropertiesManager pm = PropertiesManager.getInstance();
				if (newlyCreatedUser.getAttributeValues() != null) {
					List<String> emails = newlyCreatedUser
							.getAttributeValues()
							.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL));
					if (emails != null && emails.size() != 0) {
						updateLastAccessTime(emails.get(0));
					} else {
						EAILogger
						.info(className,
								className
								+ ", "
								+ method
								+ ", emails null or empty, Cannot update updateLastAccessTime");
					}
				} else {
					EAILogger
					.info(className,
							className
							+ ", "
							+ method
							+ ", emails null or empty, Cannot update updateLastAccessTime");
				}
				EAILogger.info(className, className + ", " + method
						+ ", Granting SAM login..");
				grantSAMLogin(request, response, newlyCreatedUser, redirectURL);
			} else {
				redirectURL = getTargetURL(request, response, null,
						newlyCreatedUser.getAttributeValues(), null,
						ErrorCodes.SOCEAI115E);
			}

			EAILogger.debug(className, className + ", " + method
					+ ", redirectURL= " + redirectURL);
			session.setAttribute(REDIRECT_URL, redirectURL);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
			String redirectURL = getTargetURL(request, response, null, null,
					null, e.getCode());
			EAILogger.debug(className, className + ", " + method
					+ ", redirectURL= " + redirectURL);
			EAILogger.debug(className, className + ", " + method
					+ ", e.getCode= " + e.getCode());
			session.setAttribute(ERROR_CODE, e.getCode());
			session.setAttribute(REDIRECT_URL, redirectURL);
		}

		// EAILogger.debug(className,redirectURL);
		// return;
		// request.getRequestDispatcher(LANDING_JSP).forward(request, response);
	}

	private void handlePendingUser(HttpServletRequest request,
			HttpServletResponse response, String userid, String firstName,
			String lastName, String socialEmail, String socialId,
			String socialProviderName, HttpSession session)
					throws ServletException, IOException {

		User newlyCreatedUser = null;
		String method = "handlePendingUser";

		EAILogger.debug(className, className + ", " + method
				+ ", Input: userid=" + userid + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", socialEmail=" + socialEmail
				+ ", socialId=" + socialId + ", socialProviderName="
				+ socialProviderName + ", session=" + session.toString());

		try {
			newlyCreatedUser = getNewlyCreatedUser(userid);
			EAILogger.debug(className, className + ", " + method
					+ ", newlyCreatedUser = " + newlyCreatedUser);
			String redirectURL = null;
			if (newlyCreatedUser != null) {
				SocialProfileData profileData = new SocialProfileData();
				profileData.setFirstName(firstName);
				profileData.setLastName(lastName);
				profileData.setEmail(socialEmail);
				profileData.setId(socialId);
				profileData.setProvider(socialProviderName);
				String profileDataJason = getCustomePofileDataJson(
						profileData, socialProviderName,
						ResponseCodes.SOCEAI110I);
				profileData.setCustomJSON(profileDataJason);

				redirectURL = getTargetURL(request, response, profileData,
						newlyCreatedUser.getAttributeValues(),
						ResponseCodes.SOCEAI110I, null);
				EAILogger.info(className, className + ", " + method
						+ ", Granting SAM login..");
				updateLastAccessTime(profileData.getEmail());
				grantSAMLogin(request, response, newlyCreatedUser, redirectURL);
			} else {
				redirectURL = getTargetURL(request, response, null,
						newlyCreatedUser.getAttributeValues(), null,
						ErrorCodes.SOCEAI115E);
			}

			EAILogger.debug(className, className + ", " + method
					+ ", redirectURL= " + redirectURL);
			session.setAttribute(REDIRECT_URL, redirectURL);
		} catch (SocialEAIException e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
			String redirectURL = getTargetURL(request, response, null, null,
					null, e.getCode());
			EAILogger.debug(className, className + ", " + method
					+ ", redirectURL= " + redirectURL);
			EAILogger.debug(className, className + ", " + method
					+ ", e.getCode= " + e.getCode());
			session.setAttribute(ERROR_CODE, e.getCode());
			session.setAttribute(REDIRECT_URL, redirectURL);
		}

		// EAILogger.debug(className,redirectURL);
		// return;
		// request.getRequestDispatcher(LANDING_JSP).forward(request, response);
	}

	private void handleNoUserConsent(HttpServletRequest request,
			HttpServletResponse response, String target, String locale,
			String operation, HttpSession session) throws ServletException,
			IOException {

		String method = "handleNoUserConsent";
		EAILogger.debug(className,
				className + ", " + method + ", Input: target=" + target
				+ ", locale=" + locale + ", operation=" + operation
				+ ", session=" + session.toString());

		String redirectURL;
		redirectURL = getTargetURL(request, response, null, null, null,
				ErrorCodes.SOCEAI117E);

		EAILogger.debug(className, className + ", " + method
				+ ", redirectURL= " + redirectURL);

		session.setAttribute(ERROR_CODE, ErrorCodes.SOCEAI117E);
		session.setAttribute(REDIRECT_URL, redirectURL);
		// EAILogger.debug(className,redirectURL);
		// return;
		request.getRequestDispatcher(LANDING_JSP).forward(request, response);
	}

	private String getTargetURL(HttpServletRequest request,
			HttpServletResponse response, SocialProfileData profileData,
			HashMap<String, List<String>> attrValues, String responseCode,
			String errorCode) {

		String method = "getTargetURL";

		if (profileData != null) {
			EAILogger.debug(className, className + ", " + method
					+ ", Input: profileData=" + profileData.toString());
		}
		if (attrValues != null) {
			EAILogger.debug(className, className + ", " + method
					+ ", Input: attrValues=" + attrValues.toString());
		}
		EAILogger
		.debug(className, className + ", " + method + ", responseCode="
				+ responseCode + ", errorCode=" + errorCode);	 

		HttpSession session = request.getSession(true);
		Object portalUrl_from_Session_obj = session
				.getAttribute(PORTAL_SRC_URL);
		Object portalTrgtUrl_from_Session_obj = session
				.getAttribute(PORTAL_TRGT_URL);
		Object locale_from_Session_obj = session.getAttribute(LOCALE);
		Object operation_from_Session_obj = session.getAttribute(OPERATION);
		EAILogger.debug(className, className + ", " + method
				+ ", operation_from_Session_obj=" + operation_from_Session_obj);
		// int operation = -1;
		String operation = null;
		String portalSrcUrl = null;
		String portalTrgtUrl = null;
		String locale = null;
		if (portalUrl_from_Session_obj != null) {
			portalSrcUrl = (String) portalUrl_from_Session_obj;
		}
		if (portalTrgtUrl_from_Session_obj != null) {
			portalTrgtUrl = (String) portalTrgtUrl_from_Session_obj;
		}
		if (locale_from_Session_obj != null) {
			locale = (String) locale_from_Session_obj;
		}
		if (operation_from_Session_obj != null) {
			try {
				// operation = Integer
				// .parseInt((String) operation_from_Session_obj);
				operation = (String) operation_from_Session_obj;
			} catch (Exception e) {
				e.printStackTrace();
				EAILogger.debug(
						className,
						className + ", EXCEPTION = "
								+ Utils.getStackTraceString(e));
				EAILogger.error(className,
						className + ", " + method + ", " + e.getMessage(), e);
			}
		}

		EAILogger.debug(className, className + ", " + method + ", operation="
				+ operation);

		// Commented to avoid error due to deletion of sce jars
		/*RedirectHelperImpl redirectHelper = new RedirectHelperImpl();
		RedirectWrapper redirectUrl = null; 
		try{
			RedirectInputWrapper inputWrapper = new RedirectInputWrapper();
			inputWrapper.setOperation(operation);
			inputWrapper.setSourceUrl(portalSrcUrl);
			inputWrapper.setTargetUrl(portalTrgtUrl);
			inputWrapper.setLocale(locale);

			DirectoryProfile directoryProfile = new DirectoryProfile(); 
			if(attrValues != null && attrValues.size() > 0) {
				directoryProfile.setUserAttributes(attrValues);
				inputWrapper.setDirectoryProfile(directoryProfile); }
			if (profileData	!= null) { 
				SocialProfile socialProfile = new SocialProfile();
				//socialProfile.setOriginalJSON(profileData.getOrigJSON());
				//socialProfile.setSocialJSON(profileData.getOrigJSON());
				socialProfile.setSocialJSON(profileData.getCustomJSON());
				//socialProfile.setProvider(profileData.getProvider());
				socialProfile.setEmailId(profileData.getEmail());
				socialProfile.setFirstName(profileData.getFirstName());
				socialProfile.setLastName(profileData.getLastName());
				socialProfile.setSocialId(profileData.getId());
				inputWrapper.setSocialProfile(socialProfile);
			} 
			if (errorCode != null) { 
				inputWrapper.setErrorCode(errorCode); 
			}
			if (responseCode != null) {
				inputWrapper.setResponseCode(responseCode); 
			} 
			redirectUrl = redirectHelper.getRedirectURL(inputWrapper); 
		}
		catch (Exception e) {
			e.printStackTrace(); EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e); } 
		EAILogger.debug(className, className + ", " + method + ", redirectUrl= " + redirectUrl);
		if(redirectUrl!=null){
			EAILogger.debug(className, className + ", " + method + ", redirectUrl.getRedirectURL= " + redirectUrl.getRedirectURL());
			return redirectUrl.getRedirectURL(); 
		}
		else{
			return "";
		}*/
	 return "";
	}

	public User getUser(String email) throws SocialEAIException {
		String method = "getUser";
		EAILogger.debug(className, className + ", " + method
				+ ", Input: email=" + email);

		User customerUser = new User();
		HashMap<String, List<String>> attributeValues = new HashMap<String, List<String>>();

		PropertiesManager pm = PropertiesManager.getInstance();

		String samlLDAPAllAttributes = "";
		String samlLDAPAttributePrimaryEmail = "";
		samlLDAPAllAttributes = pm.getProperty(SAM_LDAP_ATTRIBUTE_ALL);
		samlLDAPAttributePrimaryEmail = pm
				.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL);
		EAILogger.debug(className, className + ", " + method
				+ ", samlAllAttributes = " + samlLDAPAllAttributes);
		EAILogger.debug(className, className + ", " + method
				+ ", samlLDAPAttributePrimaryEmail = "
				+ samlLDAPAttributePrimaryEmail);

		String[] names = samlLDAPAllAttributes.split(";");
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			List<String> valueList = new ArrayList<String>();
			String value = pm.getProperty(name);
			if (value != null) {
				String[] valueArray = value.split("#");
				if (valueArray != null && valueArray.length > 0) {
					for (int j = 0; j < valueArray.length; j++) {
						valueList.add((String) valueArray[j]);
						EAILogger.debug(className, className + ", " + method
								+ ", " + name + "=" + valueArray[j]);
					}
				}
				attributeValues.put(name, valueList);
			}
		}

		String value = pm.getProperty("GROUPS");
		List<String> valueList = new ArrayList<String>();
		if (value != null) {
			String[] valueArray = value.split("#");
			if (valueArray != null && valueArray.length > 0) {
				for (int j = 0; j < valueArray.length; j++) {
					valueList.add((String) valueArray[j]);
					EAILogger.debug(className, className + ", " + method
							+ ", GROUPS=" + valueArray[j]);
				}
			}
			attributeValues.put("GROUPS", valueList);
		}

		customerUser.setAttributeValues(attributeValues);

		// Checking for user status active or inactive
		boolean isUserActive = false;
		if (attributeValues != null
				&& attributeValues.get(pm
						.getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS)) != null
						&& attributeValues.get(
								pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS)).size() > 0) {
			if (attributeValues
					.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_STATUS)).get(0)
					.equalsIgnoreCase("TRUE")) {
				isUserActive = true;
			}
		}
		EAILogger.info(className, className + ", " + method
				+ ", isUserActive = " + isUserActive);
		customerUser.setActive(isUserActive);

		// user = m_registry.getUser(null, email);
		//
		// if(user!=null){
		// String[] names = samlLDAPAllAttributes.split(";");
		// for(int i=0;i<names.length;i++){
		// String name = names[i];
		// System.out.println(name+"="+user.getOneAttributeValue(name));
		// }
		// }
		return customerUser;
	}

	public User getUser(SocialProfileData profileData)
			throws SocialEAIException {
		String method = "getUser";

		EAILogger.debug(className, className + ", " + method
				+ ", Input: SocialProfileData = " + profileData);
		User customerUser = getUser(profileData.getEmail());
		customerUser.setSocialId(profileData.getId());
		customerUser.setSocialProfileData(profileData);

		HashMap<String, List<String>> attrValues = customerUser
				.getAttributeValues();

		PropertiesManager pm = PropertiesManager.getInstance();

		// Checking for social id exist in LDAP
		boolean socialIdExists = false;
		if (attrValues.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID)) != null
				&& attrValues.get(
						pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))
						.size() > 0 && profileData.getId() != null) {
			List<String> socialIds = attrValues.get(pm
					.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID));
			for (int i = 0; i < socialIds.size(); i++) {
				String socialID = socialIds.get(i);
				if (profileData.getId().equalsIgnoreCase(
						socialID.substring(2, socialID.length()))) {
					socialIdExists = true;
					break;
				}
			}
		}

		EAILogger.info(className, className + ", " + method
				+ ", socialIdExists = " + socialIdExists);
		customerUser.setSocialIdExists(socialIdExists);

		// Checking for social email id primary
		boolean socialEmailIdPrimary = false;
		if (attrValues.get(pm
				.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL)) != null
				&& attrValues.get(
						pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL))
						.size() > 0
						&& profileData.getEmail() != null
						&& profileData
						.getEmail()
						.equalsIgnoreCase(
								attrValues
								.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_PRIMARY_EMAIL))
								.get(0))) {
			socialEmailIdPrimary = true;
		}
		EAILogger.info(className, className + ", " + method
				+ ", socialEmailIdPrimary = " + socialEmailIdPrimary);
		customerUser.setSocialEmailIdPrimary(socialEmailIdPrimary);

		// Checking for primary email Linked to social id
		boolean primaryEmailLinked2SocialId = false;
		if (socialEmailIdPrimary && socialIdExists) {
			primaryEmailLinked2SocialId = true;
		} else {
			// IF any other decision.
			// Implementation depends on decision.
		}

		EAILogger.info(className, className + ", " + method
				+ ", socialIdExists = " + primaryEmailLinked2SocialId);
		customerUser
		.setPrimaryEmailLinked2SocialId(primaryEmailLinked2SocialId);

		return customerUser;
	}

	private User getUser_SocialIdExist(SocialProfileData profileData)
			throws SocialEAIException {
		String method = "getUser_SocialIdExist";
		if (profileData != null) {
			EAILogger.debug(className, className + ", " + method
					+ ", Input: profileData=" + profileData);
			return CustomerDataProviderFactory.getInstance()
					.getCustomerDataProvider()
					.getUser_SocialIdExist(profileData);
		} else {
			return null;
		}
	}

	private User getNewlyCreatedUser(String userid) throws SocialEAIException {
		String method = "getNewlyCreatedUser";
		EAILogger.debug(className, className + ", " + method
				+ ", Input: userid=" + userid);
		if (userid != null) {
			return CustomerDataProviderFactory.getInstance()
					.getCustomerDataProvider().getNewlyCreatedUser(userid);
		} else {
			return null;
		}
	}

	private User getUser_MatchedPrimaryEmailOfAnyExistingUser(
			SocialProfileData profileData, boolean isAssociated)
					throws SocialEAIException {
		String method = "getUser_MatchedPrimaryEmailOfAnyExistingUser";
		EAILogger.debug(className, className + ", " + method
				+ ", Input: isAssociated=" + isAssociated);
		if (profileData != null) {
			EAILogger.debug(className, className + ", " + method
					+ ", Input: profileData=" + profileData);
			return CustomerDataProviderFactory
					.getInstance()
					.getCustomerDataProvider()
					.getUser_MatchedPrimaryEmailOfAnyExistingUser(profileData,
							isAssociated);
		} else {
			return null;
		}
	}

	private String getErrorMesssage(String errorcode,
			SocialProfileData profiledata, User exisitngUser) {
		String method = "getErrorMesssage";
		String errorMessage = "";
		EAILogger.debug(className, className + ", " + method
				+ ", Input: errorcode=" + errorcode);
		if (profiledata != null) {
			EAILogger.debug(className, className + ", " + method
					+ ", Input: profiledata=" + profiledata);
			EAILogger.debug(className, className + ", " + method
					+ ", profiledata provider=" + profiledata.getProvider());
		}
		try {
			if (errorcode != null) {

				// Commented to avoid error due to deletion of sce jars
				/*
				 * ResourceBundle commonResourceBundle =
				 * CommonResourceBundle.getBundle(SCE_ERROR_MSG_BUNDLE, new
				 * Locale("en"), SCE_PROPERTIES_LOCATION, true); errorMessage =
				 * commonResourceBundle.getString(errorcode);
				 */
				if (errorcode.equalsIgnoreCase("SOCEAI100E")) {
					PropertiesManager pm = PropertiesManager.getInstance();
					if (exisitngUser != null
							&& exisitngUser.getAttributeValues() != null
							&& exisitngUser
							.getAttributeValues()
							.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID)) != null
							&& exisitngUser
							.getAttributeValues()
							.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))
							.size() > 0) {
						String existingSocialID = (String) exisitngUser
								.getAttributeValues()
								.get(pm.getProperty(SAM_LDAP_ATTRIBUTE_USER_SOCIAL_ID))
								.get(0);
						EAILogger.debug(className, className + ", " + method
								+ ", existingSocialID=" + existingSocialID);
						if (existingSocialID != null) {
							String existingSPShort = existingSocialID
									.substring(0, 2);
							String existingProvider = "";
							if (existingSPShort != null
									&& existingSPShort
									.equalsIgnoreCase(AMAZON_SHORT)) {
								existingProvider = "Amazon";
							} else if (existingSPShort != null
									&& existingSPShort
									.equalsIgnoreCase(FACEBOOK_SHORT)) {
								existingProvider = "Facebook";
							} else if (existingSPShort != null
									&& existingSPShort
									.equalsIgnoreCase(LINKEDIN_SHORT)) {
								existingProvider = "LinkedIn";
							}
							EAILogger.debug(className, className + ", "
									+ method + ", existingProvider="
									+ existingProvider);
							errorMessage = MessageFormat.format(errorMessage,
									existingProvider);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			EAILogger
			.debug(className,
					className + ", EXCEPTION = "
							+ Utils.getStackTraceString(e));
			EAILogger.error(className,
					className + ", " + method + ", " + e.getMessage(), e);
		}
		EAILogger.debug(className, className + ", errorMessage = "
				+ errorMessage);
		return errorMessage;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String method = "doGet";
		EAILogger.debug(className, className + ", " + method);
		doPost(request, response);
	}
}

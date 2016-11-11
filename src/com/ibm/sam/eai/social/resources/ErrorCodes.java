package com.ibm.sam.eai.social.resources;

public class ErrorCodes {

    public static final String SOCEAI100E_DESC = "SOCEAI100E - SocialId doesn't Exists, Social email matches with primary email of some existing user, which is associated with some other Social Id.%0";
    public static final String SOCEAI101E_DESC = "SOCEAI101E - Error occurred while processing the request on social site. Error code %0 and Error Message %1";
    public static final String SOCEAI102E_DESC = "SOCEAI102E - Error while connecting to LDAP";
    public static final String SOCEAI103E_DESC = "SOCEAI103E - Error while authenticating using social site.%0";
    public static final String SOCEAI104E_DESC = "SOCEAI104E - Error (%0) occurred, while retrieving profile data.";
    public static final String SOCEAI105E_DESC = "SOCEAI105E - Error while getting access token. ";
    public static final String SOCEAI106E_DESC = "SOCEAI106E - Configuration error. %0";
    public static final String SOCEAI107E_DESC = "SOCEAI107E - System Error.  Error code %0 and Error Message %1";
    public static final String SOCEAI108E_DESC = "SOCEAI108E - Properties file not found %0";
    public static final String SOCEAI109E_DESC = "SOCEAI109E - Properties file not accessible %0";
    public static final String SOCEAI110E_DESC = "SOCEAI110E - LDAP Connection error %0";
    public static final String SOCEAI111E_DESC = "SOCEAI111E - One of required inputs is missing target '%0', portalUrl '%1', locale '%2', operation '%3'";
	public static final String SOCEAI112E_DESC = "SOCEAI112E - For '%0' Social Provider following configuration information is missing. Parameter AccessCodeUrl '%1', AppId '%2', RedirectUrl '%3'";;
	public static final String SOCEAI113E_DESC = "SOCEAI113E - For '%0' Social Provider following configuration information is missing. Parameter BaseUrl '%1', AuthCode '%2'";;
	public static final String SOCEAI114E_DESC = "SOCEAI114E - Registration completed or pending, One of required inputs is missing, userid, sourceUrl or locale.";
	public static final String SOCEAI115E_DESC = "SOCEAI115E - Registration completed or pending, Newly registered user NOT found."; 
	public static final String SOCEAI116E_DESC = "SOCEAI116E - Did not satisfied any business condition."; 
	public static final String SOCEAI117E_DESC = "SOCEAI117E - Social Login profile data access consent not provided by the user.";
	public static final String SOCEAI118E_DESC = "SOCEAI118E - SocialId Exists, operation is connect."; //The Social ID has already been registered or connected to another  User ID. Please log out and log in woth your Social ID.
	
    public static final String SOCEAI100E = "SOCEAI100E"; // SocialId doesn't Exists, Social email matches with primary email of some existing user, which is associated with some other Social Id. //Social ID already associated with other customer.com ID. %0
    public static final String SOCEAI101E = "SOCEAI101E"; // Error occurred while processing the request on social site. Error code %0 and Error Message %1
    public static final String SOCEAI102E = "SOCEAI102E"; // Error while connecting to LDAP
    public static final String SOCEAI103E = "SOCEAI103E"; // Error while authenticating using social site.%0
    public static final String SOCEAI104E = "SOCEAI104E"; // Error (%0) occurred, while retrieving profile data.
    public static final String SOCEAI105E = "SOCEAI105E"; // Error while getting access token. 
    public static final String SOCEAI106E = "SOCEAI106E"; // Configuration error. %0
    public static final String SOCEAI107E = "SOCEAI107E"; // System Error.  Error code %0 and Error Message %1
    public static final String SOCEAI108E = "SOCEAI108E"; // Properties file not found %0
    public static final String SOCEAI109E = "SOCEAI109E"; // Properties file not accessible %0
    public static final String SOCEAI110E = "SOCEAI110E"; // LDAP Connection error %0
    public static final String SOCEAI111E = "SOCEAI111E"; // One of required inputs is missing target '%0', portalUrl '%1', locale '%2', operation '%3'
	public static final String SOCEAI112E = "SOCEAI112E"; // For '%0' Social Provider following configuration information is missing. Parameter AccessCodeUrl '%1', AppId '%2', RedirectUrl '%3'
	public static final String SOCEAI113E = "SOCEAI113E"; // For '%0' Social Provider following configuration information is missing. Parameter BaseUrl '%1', AuthCode '%2'
	public static final String SOCEAI114E = "SOCEAI114E"; // Registration completed or pending, One of required inputs is missing, userid, sourceUrl or locale.
	public static final String SOCEAI115E = "SOCEAI115E"; // Registration completed or pending, Newly registered user NOT found.
	public static final String SOCEAI116E = "SOCEAI116E"; // Did not satisfied any business condition.
	public static final String SOCEAI117E = "SOCEAI117E";// Social Login profile data access consent not provided by the user.
	public static final String SOCEAI118E = "SOCEAI118E";// SocialId Exists, operation is connect.//The Social ID has already been registered or connected to another  User ID. Please log out and log in woth your Social ID.
}

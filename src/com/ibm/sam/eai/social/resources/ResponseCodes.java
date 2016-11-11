package com.ibm.sam.eai.social.resources;

public class ResponseCodes {

	/*
	 * public static final String SOCEAI100I =
	 * "SOCEAI100I - Social ID Match found."; public static final String
	 * SOCEAI101I = "SOCEAI101I - Primary email match found."; public static
	 * final String SOCEAI102I = "SOCEAI102I - Profile data retrieved.";
	 */
	public static final String SOCEAI101I = "SOCEAI101I";// - SocialId Exists, User account is active, primary email is linked to social id and Social email is primaryEmail.
	public static final String SOCEAI102I = "SOCEAI102I";// - SocialId Exists, User account is active, primary email is linked to social id and Social email is NOT primaryEmail. //Cotraditcs: Primary email is liked to social id when SocialId Exists and email is  primaryEmail. 
	public static final String SOCEAI103I = "SOCEAI103I";// - SocialId Exists, User account is active, primary email is NOT linked to social id and Social email is NOT primaryEmail.
	public static final String SOCEAI104I = "SOCEAI104I";// - SocialId Exists, User account is NOT active, primary email is NOT linked to social id and Social email is NOT primaryEmail.
	public static final String SOCEAI105I = "SOCEAI105I";// - SocialId Exists, User account is NOT active, primary email is linked to social id and Social email is NOT primaryEmail. //Cotraditcs: Primary email is liked to social id when SocialId Exists and email is  primaryEmail.
	public static final String SOCEAI106I = "SOCEAI106I";// - SocialId Exists, User account is NOT active, primary email is linked to social id and Social email is primaryEmail.
	public static final String SOCEAI107I = "SOCEAI107I";// - SocialId doesn't Exists, Social email doesn't match primary email of any existing user.
	public static final String SOCEAI108I = "SOCEAI108I";// - SocialId doesn't Exists, Social email matches with primary email of some existing user, which is not associated with some other Social Id.
	//public static final String SOCEAI109I = "SOCEAI109I";// - Social Login profile data access consent not provided by the user.
	public static final String SOCEAI110I = "SOCEAI110I";// - Registration completed or pending, Newly registered user found.	
	//public static final String SOCEAI111I = "SOCEAI111I";// - Registration completed or pending, Newly registered user NOT found.	

}

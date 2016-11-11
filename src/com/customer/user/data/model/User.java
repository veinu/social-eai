package com.customer.user.data.model;

import java.util.HashMap;
import java.util.List;

import com.ibm.sam.eai.social.model.SocialProfileData;

public class User {

	String socialId;
	boolean isActive;
	boolean socialIdExists;
	boolean primaryEmailLinked2SocialId;
	boolean socialEmailIdPrimary;
	HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
	
	SocialProfileData socialProfileData;
	String userPassword;
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getSocialId() {
		return socialId;
	}
	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isSocialIdExists() {
		return socialIdExists;
	}
	public void setSocialIdExists(boolean socialIdExists) {
		this.socialIdExists = socialIdExists;
	}
	public boolean isPrimaryEmailLinked2SocialId() {
		return primaryEmailLinked2SocialId;
	}
	public void setPrimaryEmailLinked2SocialId(boolean primaryEmailLinked2SocialId) {
		this.primaryEmailLinked2SocialId = primaryEmailLinked2SocialId;
	}
	public boolean isSocialEmailIdPrimary() {
		return socialEmailIdPrimary;
	}
	public void setSocialEmailIdPrimary(boolean socialEmailIdPrimary) {
		this.socialEmailIdPrimary = socialEmailIdPrimary;
	}
	public SocialProfileData getSocialProfileData() {
		return socialProfileData;
	}
	public void setSocialProfileData(SocialProfileData socialProfileData) {
		this.socialProfileData = socialProfileData;
	}
	
	@Override
	public String toString() {
		return "User [socialId=" + socialId + ", isActive=" + isActive
				+ ", socialIdExists=" + socialIdExists
				+ ", primaryEmailLinked2SocialId="
				+ primaryEmailLinked2SocialId + ", socialEmailIdPrimary="
				+ socialEmailIdPrimary + "]";
	}
	public HashMap<String, List<String>> getAttributeValues() {
		return attributeValues;
	}
	public void setAttributeValues(
			HashMap<String, List<String>> attributeValues) {
		this.attributeValues = attributeValues;
	}
	
	
	

	
}

package com.ibm.sam.eai.social.model;

public class SocialProfileData {
	String provider;
	String email;
	String firstName;
	String lastName;
	String id;
	String origJSON;
	String customJSON;
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrigJSON() {
		return origJSON;
	}
	public void setOrigJSON(String origJSON) {
		this.origJSON = origJSON;
	}
	public String getCustomJSON() {
		return customJSON;
	}
	public void setCustomJSON(String customJSON) {
		this.customJSON = customJSON;
	}
	
	@Override
	public String toString() {
		return "SocialProfileData [provider=" + provider + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", id=" + id + ", origJSON=" + origJSON + "]";
	}
}

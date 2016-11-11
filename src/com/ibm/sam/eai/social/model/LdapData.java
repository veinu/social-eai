package com.ibm.sam.eai.social.model;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class LdapData {
	
	HashMap<String, List<String>> attributeValues =  new HashMap<String, List<String>>();
	String rawDn;
	String primaryMail;
	String uid;
	
	public LdapData(String uid,String primaryMail,String rawDn){
		this.uid=uid;
		this.primaryMail=primaryMail;
		this.rawDn=rawDn;
	}
	public HashMap<String, List<String>> getAttributeValues() {
		return attributeValues;
	}
	public void setAttributeValues(
			HashMap<String, List<String>> attributeValues) {
		this.attributeValues = attributeValues;
	}
	public String getRawDn() {
		return rawDn;
	}
	public void setRawDn(String rawDn) {
		this.rawDn = rawDn;
	}
	public String getPrimaryMail() {
		return primaryMail;
	}
	public void setPrimaryMail(String primaryMail) {
		this.primaryMail = primaryMail;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setAttributeValue(String key, ArrayList<String> value) {
		this.attributeValues.put(key, value);
	}
	public List<String> getAttributeValue(String key) {
		return this.attributeValues.get(key);
	}
	@Override
	public String toString() {
		return "LdapData [rawDn="
				+ rawDn + ", primaryMail=" + primaryMail + ", uid=" + uid + ",attributeValues=" + attributeValues + "]";
	}
	
}

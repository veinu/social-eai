package com.ibm.sam.eai.database.impl;

import java.sql.Timestamp;
import java.util.Date;

public class ApprovalInfo {

	private String social_ID;
	private String firstName;
	private String lastName;
	private String socialEmail;
	private int approvalStatus;
	private String approvedBy;
	private int num_Remainder_Sent;
	private String approver_Comment;
	private Timestamp time_Created;
	private Timestamp time_Approved;
	
	public ApprovalInfo()
	{
		
	}
	public ApprovalInfo(String social_ID,String firstName,String lastName,String socialEmail,Timestamp time_Created)
	{
		this.social_ID=social_ID;
		this.firstName=firstName;
		this.lastName=lastName;
		this.socialEmail=socialEmail;
		this.time_Created=time_Created;
		this.approvalStatus=0;
		this.approvedBy = "";
		this.num_Remainder_Sent=0;
		this.approver_Comment="";
	}
	
	public String getSocial_ID() {
		return social_ID;
	}
	public void setSocial_ID(String social_ID) {
		this.social_ID = social_ID;
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
	public String getSocialEmail() {
		return socialEmail;
	}
	public void setSocialEmail(String socialEmail) {
		this.socialEmail = socialEmail;
	}
	public int getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(int approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public int getNum_Remainder_Sent() {
		return num_Remainder_Sent;
	}
	public void setNum_Remainder_Sent(int num_Remainder_Sent) {
		this.num_Remainder_Sent = num_Remainder_Sent;
	}
	public String getApprover_Comment() {
		return approver_Comment;
	}
	public void setApprover_Comment(String approver_Comment) {
		this.approver_Comment = approver_Comment;
	}
	public Timestamp getTime_Created() {
		return time_Created;
	}
	public void setTime_Created(Timestamp time_Created) {
		this.time_Created = time_Created;
	}
	public Timestamp getTime_Approved() {
		return time_Approved;
	}
	public void setTime_Approved(Timestamp time_Approved) {
		this.time_Approved = time_Approved;
	}
}

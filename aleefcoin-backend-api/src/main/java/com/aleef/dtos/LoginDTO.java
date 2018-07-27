package com.aleef.dtos;

import java.util.Date;

public class LoginDTO {

	private Integer id;

	private String userName;

	private String password;

	private Integer roleId;

	private String emailId;

	private String mobileNo;

	private String etherWalletAddress;

	private String etherWalletPassword;

	private Date createdDate;

	private Boolean activation;

	private String status;

	private String qrCode;

	private String sessionId;

	private Integer securityKey;

	private String otpCode;

	private String popUpStatus;

	private String mediaId;

	private String emailPopUpStatus;

	private Integer kycStatus;

	private Integer usersCount;

	private Double referralTokens;

	private Double referralLevel1Tokens;

	private Double referralLevel2Tokens;

	private Double referralLevel3Tokens;

	private Double referralLevel4Tokens;

	private String referralLink;

	public Double getReferralLevel1Tokens() {
		return referralLevel1Tokens;
	}

	public void setReferralLevel1Tokens(Double referralLevel1Tokens) {
		this.referralLevel1Tokens = referralLevel1Tokens;
	}

	public Double getReferralLevel2Tokens() {
		return referralLevel2Tokens;
	}

	public void setReferralLevel2Tokens(Double referralLevel2Tokens) {
		this.referralLevel2Tokens = referralLevel2Tokens;
	}

	public Double getReferralLevel3Tokens() {
		return referralLevel3Tokens;
	}

	public void setReferralLevel3Tokens(Double referralLevel3Tokens) {
		this.referralLevel3Tokens = referralLevel3Tokens;
	}

	public Double getReferralLevel4Tokens() {
		return referralLevel4Tokens;
	}

	public void setReferralLevel4Tokens(Double referralLevel4Tokens) {
		this.referralLevel4Tokens = referralLevel4Tokens;
	}

	public String getReferralLink() {
		return referralLink;
	}

	public void setReferralLink(String referralLink) {
		this.referralLink = referralLink;
	}

	public Double getReferralTokens() {
		return referralTokens;
	}

	public void setReferralTokens(Double referralTokens) {
		this.referralTokens = referralTokens;
	}

	public Integer getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(Integer usersCount) {
		this.usersCount = usersCount;
	}

	public Integer getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(Integer kycStatus) {
		this.kycStatus = kycStatus;
	}

	public String getEmailPopUpStatus() {
		return emailPopUpStatus;
	}

	public void setEmailPopUpStatus(String emailPopUpStatus) {
		this.emailPopUpStatus = emailPopUpStatus;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getPopUpStatus() {
		return popUpStatus;
	}

	public void setPopUpStatus(String popUpStatus) {
		this.popUpStatus = popUpStatus;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public Integer getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(Integer securityKey) {
		this.securityKey = securityKey;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEtherWalletAddress() {
		return etherWalletAddress;
	}

	public void setEtherWalletAddress(String etherWalletAddress) {
		this.etherWalletAddress = etherWalletAddress;
	}

	public String getEtherWalletPassword() {
		return etherWalletPassword;
	}

	public void setEtherWalletPassword(String etherWalletPassword) {
		this.etherWalletPassword = etherWalletPassword;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getActivation() {
		return activation;
	}

	public void setActivation(Boolean activation) {
		this.activation = activation;
	}

}

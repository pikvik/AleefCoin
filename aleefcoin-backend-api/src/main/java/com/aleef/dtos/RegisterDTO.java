package com.aleef.dtos;

import java.math.BigDecimal;
import java.util.Date;

public class RegisterDTO {

	private Integer id;

	private String userName;

	private String password;

	private String confirmPassword;

	private Integer roleId;

	private String emailId;

	private String mobileNo;

	private String etherWalletAddress;

	private String etherWalletPassword;

	private Date createdDate;

	private Boolean activation;

	private String otpCode;

	private String sessionId;

	private String oldPassword;

	private String token;

	private String mediaId;

	private BigDecimal etherBalance;

	private String subject;

	private String description;

	private String sponser_id;

	private String confirmEtherWalletPassword;

	private Integer levelOne;

	private BigDecimal levelOneBonus;

	private Integer levelTwo;

	private BigDecimal levelTwoBonus;

	private Integer levelThree;

	private BigDecimal levelThreeBonus;

	private Integer levelFour;

	private BigDecimal levelFourBonus;

	private BigDecimal totalTokens;

	private BigDecimal icoTokens;

	private BigDecimal soldTokens;

	private BigDecimal burnTokens;

	private String appId;

	private String deviceType;

	private Double etherToUsd;

	private String securityKey;

	private String confirmSecurityKey;

	private Double referralTokens;

	private Double referralLevel1Tokens;

	private Double referralLevel2Tokens;

	private Double referralLevel3Tokens;

	private Double referralLevel4Tokens;

	private Double tokenToUsd;

	public Double getTokenToUsd() {
		return tokenToUsd;
	}

	public void setTokenToUsd(Double tokenToUsd) {
		this.tokenToUsd = tokenToUsd;
	}

	public Double getReferralTokens() {
		return referralTokens;
	}

	public void setReferralTokens(Double referralTokens) {
		this.referralTokens = referralTokens;
	}

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

	public String getConfirmSecurityKey() {
		return confirmSecurityKey;
	}

	public void setConfirmSecurityKey(String confirmSecurityKey) {
		this.confirmSecurityKey = confirmSecurityKey;
	}

	public Double getEtherToUsd() {
		return etherToUsd;
	}

	public void setEtherToUsd(Double etherToUsd) {
		this.etherToUsd = etherToUsd;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public BigDecimal getIcoTokens() {
		return icoTokens;
	}

	public void setIcoTokens(BigDecimal icoTokens) {
		this.icoTokens = icoTokens;
	}

	public BigDecimal getSoldTokens() {
		return soldTokens;
	}

	public void setSoldTokens(BigDecimal soldTokens) {
		this.soldTokens = soldTokens;
	}

	public BigDecimal getBurnTokens() {
		return burnTokens;
	}

	public void setBurnTokens(BigDecimal burnTokens) {
		this.burnTokens = burnTokens;
	}

	public BigDecimal getTotalTokens() {
		return totalTokens;
	}

	public void setTotalTokens(BigDecimal bigDecimal) {
		this.totalTokens = bigDecimal;
	}

	public String getConfirmEtherWalletPassword() {
		return confirmEtherWalletPassword;
	}

	public void setConfirmEtherWalletPassword(String confirmEtherWalletPassword) {
		this.confirmEtherWalletPassword = confirmEtherWalletPassword;
	}

	public String getSponser_id() {
		return sponser_id;
	}

	public void setSponser_id(String sponser_id) {
		this.sponser_id = sponser_id;
	}

	public Integer getLevelOne() {
		return levelOne;
	}

	public void setLevelOne(Integer levelOne) {
		this.levelOne = levelOne;
	}

	public Integer getLevelTwo() {
		return levelTwo;
	}

	public void setLevelTwo(Integer levelTwo) {
		this.levelTwo = levelTwo;
	}

	public Integer getLevelThree() {
		return levelThree;
	}

	public void setLevelThree(Integer levelThree) {
		this.levelThree = levelThree;
	}

	public Integer getLevelFour() {
		return levelFour;
	}

	public void setLevelFour(Integer levelFour) {
		this.levelFour = levelFour;
	}

	public BigDecimal getLevelOneBonus() {
		return levelOneBonus;
	}

	public void setLevelOneBonus(BigDecimal levelOneBonus) {
		this.levelOneBonus = levelOneBonus;
	}

	public BigDecimal getLevelTwoBonus() {
		return levelTwoBonus;
	}

	public void setLevelTwoBonus(BigDecimal levelTwoBonus) {
		this.levelTwoBonus = levelTwoBonus;
	}

	public BigDecimal getLevelThreeBonus() {
		return levelThreeBonus;
	}

	public void setLevelThreeBonus(BigDecimal levelThreeBonus) {
		this.levelThreeBonus = levelThreeBonus;
	}

	public BigDecimal getLevelFourBonus() {
		return levelFourBonus;
	}

	public void setLevelFourBonus(BigDecimal levelFourBonus) {
		this.levelFourBonus = levelFourBonus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public BigDecimal getEtherBalance() {
		return etherBalance;
	}

	public void setEtherBalance(BigDecimal etherBalance) {
		this.etherBalance = etherBalance;
	}

	private String popUpStatus;

	public String getPopUpStatus() {
		return popUpStatus;
	}

	public void setPopUpStatus(String popUpStatus) {
		this.popUpStatus = popUpStatus;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
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

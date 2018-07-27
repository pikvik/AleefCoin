package com.aleef.dtos;

import java.util.List;

public class StatusResponseDTO {

	private String status;

	private String message;

	private LoginDTO loginInfo;

	private List<TokenDTO> purchaseListInfo;

	private RegisterDTO linkVerificationInfo;

	private RegisterDTO etherBalanceInfo;

	private TokenDTO tokenBalance;

	private List<TokenDTO> transactionHistoryInfo;

	private List<RegisterDTO> listUsers;

	private String dueTo;

	private List<KycDTO> kycList;

	private KycDTO kycUserInfo;

	private RegisterDTO adminDashboardDetails;

	private List<TokenDTO> requestTokensList;

	private List<PurchaseCoinDTO> icoTokensDetList;

	private List<String>[] allLevelsRefNames;

	private RegisterDTO referralTokens;

	private KycDTO kycInfo;

	public KycDTO getKycInfo() {
		return kycInfo;
	}

	public void setKycInfo(KycDTO kycInfo) {
		this.kycInfo = kycInfo;
	}

	public RegisterDTO getReferralTokens() {
		return referralTokens;
	}

	public void setReferralTokens(RegisterDTO referralTokens) {
		this.referralTokens = referralTokens;
	}

	public List<String>[] getAllLevelsRefNames() {
		return allLevelsRefNames;
	}

	public void setAllLevelsRefNames(List<String>[] allLevelsRefNames) {
		this.allLevelsRefNames = allLevelsRefNames;
	}

	public List<PurchaseCoinDTO> getIcoTokensDetList() {
		return icoTokensDetList;
	}

	public void setIcoTokensDetList(List<PurchaseCoinDTO> icoTokensDetList) {
		this.icoTokensDetList = icoTokensDetList;
	}

	public List<TokenDTO> getRequestTokensList() {
		return requestTokensList;
	}

	public void setRequestTokensList(List<TokenDTO> requestTokensList) {
		this.requestTokensList = requestTokensList;
	}

	public RegisterDTO getAdminDashboardDetails() {
		return adminDashboardDetails;
	}

	public void setAdminDashboardDetails(RegisterDTO adminDashboardDetails) {
		this.adminDashboardDetails = adminDashboardDetails;
	}

	public List<TokenDTO> getPurchaseListInfo() {
		return purchaseListInfo;
	}

	public void setPurchaseListInfo(List<TokenDTO> purchaseListInfo) {
		this.purchaseListInfo = purchaseListInfo;
	}

	public KycDTO getKycUserInfo() {
		return kycUserInfo;
	}

	public void setKycUserInfo(KycDTO kycUserInfo) {
		this.kycUserInfo = kycUserInfo;
	}

	public List<KycDTO> getKycList() {
		return kycList;
	}

	public void setKycList(List<KycDTO> kycList) {
		this.kycList = kycList;
	}

	public String getDueTo() {
		return dueTo;
	}

	public void setDueTo(String dueTo) {
		this.dueTo = dueTo;
	}

	public List<RegisterDTO> getListUsers() {
		return listUsers;
	}

	public void setListUsers(List<RegisterDTO> listUsers) {
		this.listUsers = listUsers;
	}

	public List<TokenDTO> getTransactionHistoryInfo() {
		return transactionHistoryInfo;
	}

	public void setTransactionHistoryInfo(List<TokenDTO> transactionHistoryInfo) {
		this.transactionHistoryInfo = transactionHistoryInfo;
	}

	public TokenDTO getTokenBalance() {
		return tokenBalance;
	}

	public void setTokenBalance(TokenDTO tokenBalance) {
		this.tokenBalance = tokenBalance;
	}

	public RegisterDTO getEtherBalanceInfo() {
		return etherBalanceInfo;
	}

	public void setEtherBalanceInfo(RegisterDTO etherBalanceInfo) {
		this.etherBalanceInfo = etherBalanceInfo;
	}

	public RegisterDTO getLinkVerificationInfo() {
		return linkVerificationInfo;
	}

	public void setLinkVerificationInfo(RegisterDTO linkVerificationInfo) {
		this.linkVerificationInfo = linkVerificationInfo;
	}

	public LoginDTO getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(LoginDTO loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

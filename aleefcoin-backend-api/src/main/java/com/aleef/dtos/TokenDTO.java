package com.aleef.dtos;

import java.util.Date;
import java.math.BigDecimal;

public class TokenDTO {

	private String sessionId;

	private String fromAddress;

	private String toAddress;

	private Integer kycId;

	private Double amount;

	private BigDecimal tokenAmount;

	private Double transactionAmount;

	private String etherWalletPassword;

	private Date createdDate;

	private Date transactionDate;

	private String etherWalletAddress;

	private Double requestTokens;

	private Integer id;

	private String transactionMode;

	private Date purchasedDate;

	private String transferStatus;

	private String emailId;

	private Integer transactionType;

	private String status;

	private BigDecimal etherAmount;

	private BigDecimal freeTokens;

	private BigDecimal refferalTokens;

	private BigDecimal icoBalanceTokens;

	private String userName;

	private String transferId;

	private String typeOfStatus;

	private Integer purchaseStatus;

	private Double tokenToUsd;

	private Integer notificationCount;

	public Integer getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(Integer notificationCount) {
		this.notificationCount = notificationCount;
	}

	public Double getTokenToUsd() {
		return tokenToUsd;
	}

	public void setTokenToUsd(Double tokenToUsd) {
		this.tokenToUsd = tokenToUsd;
	}

	public Integer getPurchaseStatus() {
		return purchaseStatus;
	}

	public void setPurchaseStatus(Integer purchaseStatus) {
		this.purchaseStatus = purchaseStatus;
	}

	public String getTypeOfStatus() {
		return typeOfStatus;
	}

	public void setTypeOfStatus(String typeOfStatus) {
		this.typeOfStatus = typeOfStatus;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getIcoBalanceTokens() {
		return icoBalanceTokens;
	}

	public void setIcoBalanceTokens(BigDecimal icoBalanceTokens) {
		this.icoBalanceTokens = icoBalanceTokens;
	}

	public void setEtherAmount(BigDecimal etherAmount) {
		this.etherAmount = etherAmount;
	}

	public void setFreeTokens(BigDecimal freeTokens) {
		this.freeTokens = freeTokens;
	}

	public void setRefferalTokens(BigDecimal refferalTokens) {
		this.refferalTokens = refferalTokens;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getKycId() {
		return kycId;
	}

	public void setKycId(Integer kycId) {
		this.kycId = kycId;
	}

	public Date getPurchasedDate() {
		return purchasedDate;
	}

	public String getTransferStatus() {
		return transferStatus;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getEtherAmount() {
		return etherAmount;
	}

	public BigDecimal getFreeTokens() {
		return freeTokens;
	}

	public BigDecimal getRefferalTokens() {
		return refferalTokens;
	}

	public Double getRequestTokens() {
		return requestTokens;
	}

	public void setRequestTokens(Double requestTokens) {
		this.requestTokens = requestTokens;
	}

	public String getEtherWalletAddress() {
		return etherWalletAddress;
	}

	public void setEtherWalletAddress(String etherWalletAddress) {
		this.etherWalletAddress = etherWalletAddress;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getTokenAmount() {
		return tokenAmount;
	}

	public void setTokenAmount(BigDecimal tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public String getEtherWalletPassword() {
		return etherWalletPassword;
	}

	public void setEtherWalletPassword(String etherWalletPassword) {
		this.etherWalletPassword = etherWalletPassword;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public Double getAmount() {
		return amount;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}

package com.aleef.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "purchase_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PurchaseInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "purchased_date")
	@NotNull
	private Date purchasedDate;

	@Column(name = "request_tokens")
	@NotNull
	private BigDecimal requestTokens;

	@Column(name = "free_tokens")
	@NotNull
	private BigDecimal freeTokens;

	@Column(name = "transfer_status")
	@NotNull
	private int transferStatus;

	@Column(name = "email_id")
	@NotNull
	private String emailId;

	@Column(name = "etherWalletAddress")
	@NotNull
	private String etherWalletAddress;

	@Column(name = "ether_amount")
	private BigDecimal etherAmount;

	@Column(name = "refferal_Tokens")
	private BigDecimal refferalTokens;

	@Column(name = "user_name")
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPurchasedDate() {
		return purchasedDate;
	}

	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public BigDecimal getRequestTokens() {
		return requestTokens;
	}

	public void setRequestTokens(BigDecimal requestTokens) {
		this.requestTokens = requestTokens;
	}

	public BigDecimal getFreeTokens() {
		return freeTokens;
	}

	public void setFreeTokens(BigDecimal freeTokens) {
		this.freeTokens = freeTokens;
	}

	public int getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(int transferStatus) {
		this.transferStatus = transferStatus;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEtherWalletAddress() {
		return etherWalletAddress;
	}

	public void setEtherWalletAddress(String etherWalletAddress) {
		this.etherWalletAddress = etherWalletAddress;
	}

	public BigDecimal getEtherAmount() {
		return etherAmount;
	}

	public void setEtherAmount(BigDecimal etherAmount) {
		this.etherAmount = etherAmount;
	}

	public BigDecimal getRefferalTokens() {
		return refferalTokens;
	}

	public void setRefferalTokens(BigDecimal refferalTokens) {
		this.refferalTokens = refferalTokens;
	}

}

package com.aleef.models;

import java.io.Serializable;

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
@Table(name = "Request_Tokens")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class RequestTokensInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "user_Id")
	@NotNull
	private Integer userId;

	@Column(name = "emailId")
	@NotNull
	private String emailId;

	@Column(name = "from_Address")
	@NotNull
	private String fromAddress;

	@Column(name = "to_Address")
	@NotNull
	private String toAddress;

	@Column(name = "token_Amount")
	@NotNull
	private Double tokenAmount;

	@Column(name = "status")
	@NotNull
	private String status;

	public Integer getId() {
		return id;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public Double getTokenAmount() {
		return tokenAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public void setTokenAmount(Double tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

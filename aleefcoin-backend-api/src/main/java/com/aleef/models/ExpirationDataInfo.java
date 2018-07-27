package com.aleef.models;

import java.io.Serializable;
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
@Table(name = "ExpirationData_Info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class ExpirationDataInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "emailId")
	@NotNull
	private String emailId;

	@Column(name = "token")
	@NotNull
	private String token;

	@Column(name = "expiredDate")
	@NotNull
	private Date expiredDate;

	@Column(name = "tokenStatus")
	@NotNull
	private boolean tokenStatus;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public boolean isTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(boolean tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

}

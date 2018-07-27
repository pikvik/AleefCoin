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
@Table(name = "PreICO_RegisterInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class PreICORegisterInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "user_Name")
	@NotNull
	private String userName;

	@Column(name = "Role_Id")
	@NotNull
	private Integer roleId;

	@Column(name = "Email_Id")
	@NotNull
	private String emailId;

	@Column(name = "mobileNo")
	@NotNull
	private String mobileNo;

	@Column(name = "activation")
	private Boolean activation;

	@Column(name = "createdDate")
	private Date createdDate;

	public Integer getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public Boolean getActivation() {
		return activation;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public void setActivation(Boolean activation) {
		this.activation = activation;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}

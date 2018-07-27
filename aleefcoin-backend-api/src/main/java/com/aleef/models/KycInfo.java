package com.aleef.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "KYCInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KycInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "creation_time")
	private Date creationTime;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "dob")
	private String dob;

	@Column(name = "gender")
	private String gender;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "home_address")
	private String homeAddress;

	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "city")
	private String city;

	@Column(name = "country")
	private String country;

	@Column(name = "kyc_status")
	private Integer kycStatus;

	@Column(name = "kycdoc1_path")
	private String kycDoc1Path;

	@Column(name = "kycdoc2_path")
	private String kycDoc2Path;

	@Column(name = "kycdoc1_name")
	private String kycDoc1Name;

	@Column(name = "kycdoc2_name")
	private String kycDoc2Name;

	@Column(name = "kyc_upload_status")
	private Integer kycUploadStatus;

	public String getKycDoc1Name() {
		return kycDoc1Name;
	}

	public void setKycDoc1Name(String kycDoc1Name) {
		this.kycDoc1Name = kycDoc1Name;
	}

	public String getKycDoc2Name() {
		return kycDoc2Name;
	}

	public void setKycDoc2Name(String kycDoc2Name) {
		this.kycDoc2Name = kycDoc2Name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getKycUploadStatus() {
		return kycUploadStatus;
	}

	public void setKycUploadStatus(Integer kycUploadStatus) {
		this.kycUploadStatus = kycUploadStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(int kycStatus) {
		this.kycStatus = kycStatus;
	}

	public String getKycDoc1Path() {
		return kycDoc1Path;
	}

	public void setKycDoc1Path(String kycDoc1Path) {
		this.kycDoc1Path = kycDoc1Path;
	}

	public String getKycDoc2Path() {
		return kycDoc2Path;
	}

	public void setKycDoc2Path(String kycDoc2Path) {
		this.kycDoc2Path = kycDoc2Path;
	}

	public void setKycStatus(Integer kycStatus) {
		this.kycStatus = kycStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

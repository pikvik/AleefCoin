package com.aleef.dtos;

public class KycDTO {

	private String fullName;
	private String dob;
	private String gender;
	private String emailId;
	private String address;
	private String city;
	private String mobileNo;
	private String sessionId;
	private Integer id;
	private String kycDoc1Path;
	private String kycDoc2Path;
	private Integer kycStatus;
	private String message;
	private Integer kycUploadStatus;
	private String country;
	private String kycDoc1Name;
	private String kycDoc2Name;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(Integer kycStatus) {
		this.kycStatus = kycStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getKycUploadStatus() {
		return kycUploadStatus;
	}

	public void setKycUploadStatus(Integer kycUploadStatus) {
		this.kycUploadStatus = kycUploadStatus;
	}

}

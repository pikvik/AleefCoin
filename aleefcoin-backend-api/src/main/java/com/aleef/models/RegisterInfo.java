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
@Table(name = "Register_Info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class RegisterInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "user_Name")
	@NotNull
	private String userName;

	@Column(name = "password")
	@NotNull
	private String password;

	@Column(name = "Role_Id")
	@NotNull
	private Integer roleId;

	@Column(name = "Email_Id")
	@NotNull
	private String emailId;

	@Column(name = "mobileNo")
	@NotNull
	private String mobileNo;

	@Column(name = "ether_Wallet_Address")
	@NotNull
	private String etherWalletAddress;

	@Column(name = "wallet_Address")
	@NotNull
	private String walletAddress;

	@Column(name = "Ether_Wallet_Password")
	@NotNull
	private String etherWalletPassword;

	@Column(name = "createdDate")
	private Date createdDate;

	@Column(name = "activation")
	private Boolean activation;

	@Column(name = "media_id")
	private String mediaId;

	@Column(name = "burn_tokens")
	private Double burnTokens;

	@Column(name = "sponser_id")
	private Integer sponser_id;

	@Column(name = "level_one")
	private Integer levelOne;

	@Column(name = "level_one_bonus")
	private BigDecimal levelOneBonus;

	@Column(name = "level_two")
	private Integer levelTwo;

	@Column(name = "level_two_bonus")
	private BigDecimal levelTwoBonus;

	@Column(name = "level_three")
	private Integer levelThree;

	@Column(name = "level_three_bonus")
	private BigDecimal levelThreeBonus;

	@Column(name = "level_four")
	private Integer levelFour;

	@Column(name = "level_four_bonus")
	private BigDecimal levelFourBonus;

	@Column(name = "kycStatus")
	@NotNull
	private Integer kycStatus;

	@Column(name = "app_id")
	@NotNull
	private String appId;

	@Column(name = "device_type")
	@NotNull
	private String deviceType;

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
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

	public Integer getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(Integer kycStatus) {
		this.kycStatus = kycStatus;
	}

	public Integer getSponser_id() {
		return sponser_id;
	}

	public void setSponser_id(Integer sponser_id) {
		this.sponser_id = sponser_id;
	}

	public Integer getLevelOne() {
		return levelOne;
	}

	public void setLevelOne(Integer levelOne) {
		this.levelOne = levelOne;
	}

	public BigDecimal getLevelOneBonus() {
		return levelOneBonus;
	}

	public void setLevelOneBonus(BigDecimal levelOneBonus) {
		this.levelOneBonus = levelOneBonus;
	}

	public Integer getLevelTwo() {
		return levelTwo;
	}

	public void setLevelTwo(Integer levelTwo) {
		this.levelTwo = levelTwo;
	}

	public BigDecimal getLevelTwoBonus() {
		return levelTwoBonus;
	}

	public void setLevelTwoBonus(BigDecimal levelTwoBonus) {
		this.levelTwoBonus = levelTwoBonus;
	}

	public Integer getLevelThree() {
		return levelThree;
	}

	public void setLevelThree(Integer levelThree) {
		this.levelThree = levelThree;
	}

	public BigDecimal getLevelThreeBonus() {
		return levelThreeBonus;
	}

	public void setLevelThreeBonus(BigDecimal levelThreeBonus) {
		this.levelThreeBonus = levelThreeBonus;
	}

	public Integer getLevelFour() {
		return levelFour;
	}

	public void setLevelFour(Integer levelFour) {
		this.levelFour = levelFour;
	}

	public BigDecimal getLevelFourBonus() {
		return levelFourBonus;
	}

	public void setLevelFourBonus(BigDecimal levelFourBonus) {
		this.levelFourBonus = levelFourBonus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Double getBurnTokens() {
		return burnTokens;
	}

	public void setBurnTokens(Double burnTokens) {
		this.burnTokens = burnTokens;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
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

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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "purchase_coin_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PurchaseCoinInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "ico_start_date")
	private Date icoStartDate;

	@Column(name = "ico_end_date")
	private Date icoEndDate;

	@Column(name = "total_distribution_tokens")
	private BigDecimal totalDistributionTokens;

	@Column(name = "free_coin_percentage")
	private Integer freeCoinPercentage;

	@Column(name = "aleef_rate_in_usd")
	private BigDecimal aleefRateInUSD;

	@Column(name = "balance_coins")
	private BigDecimal balanceCoins;

	@Column(name = "purchased_coins")
	private BigDecimal purchasedCoins;

	@Column(name = "free_coins")
	private BigDecimal freeCoins;

	@Column(name = "referral_coins")
	private BigDecimal referralCoins;

	@Column(name = "slabs")
	private String slabs;

	@Column(name = "ico_slabs")
	private String icoLevelsSlabs;

	@Column(name = "ico_end_date_for_mail")
	private String icoEndDateForMail;

	@Column(name = "static_date_for_mail")
	private String staticDateForMail;

	@Column(name = "start_trigger_post_ico")
	private Date startTriggerPostIco;

	@Column(name = "stop_trigger_post_ico")
	private Date stopTriggerPostIco;

	@Column(name = "actual_ico_end_date")
	private Date actualIcoEndDate;

	public String getIcoLevelsSlabs() {
		return icoLevelsSlabs;
	}

	public void setIcoLevelsSlabs(String icoLevelsSlabs) {
		this.icoLevelsSlabs = icoLevelsSlabs;
	}

	public Date getActualIcoEndDate() {
		return actualIcoEndDate;
	}

	public void setActualIcoEndDate(Date actualIcoEndDate) {
		this.actualIcoEndDate = actualIcoEndDate;
	}

	public Date getStartTriggerPostIco() {
		return startTriggerPostIco;
	}

	public void setStartTriggerPostIco(Date startTriggerPostIco) {
		this.startTriggerPostIco = startTriggerPostIco;
	}

	public Date getStopTriggerPostIco() {
		return stopTriggerPostIco;
	}

	public void setStopTriggerPostIco(Date stopTriggerPostIco) {
		this.stopTriggerPostIco = stopTriggerPostIco;
	}

	public BigDecimal getTotalDistributionTokens() {
		return totalDistributionTokens;
	}

	public void setTotalDistributionTokens(BigDecimal totalDistributionTokens) {
		this.totalDistributionTokens = totalDistributionTokens;
	}

	public BigDecimal getBalanceCoins() {
		return balanceCoins;
	}

	public void setBalanceCoins(BigDecimal balanceCoins) {
		this.balanceCoins = balanceCoins;
	}

	public BigDecimal getPurchasedCoins() {
		return purchasedCoins;
	}

	public void setPurchasedCoins(BigDecimal purchasedCoins) {
		this.purchasedCoins = purchasedCoins;
	}

	public BigDecimal getFreeCoins() {
		return freeCoins;
	}

	public void setFreeCoins(BigDecimal freeCoins) {
		this.freeCoins = freeCoins;
	}

	public BigDecimal getReferralCoins() {
		return referralCoins;
	}

	public void setReferralCoins(BigDecimal referralCoins) {
		this.referralCoins = referralCoins;
	}

	public String getStaticDateForMail() {
		return staticDateForMail;
	}

	public void setStaticDateForMail(String staticDateForMail) {
		this.staticDateForMail = staticDateForMail;
	}

	public String getIcoEndDateForMail() {
		return icoEndDateForMail;
	}

	public void setIcoEndDateForMail(String icoEndDateForMail) {
		this.icoEndDateForMail = icoEndDateForMail;
	}

	public String getSlabs() {
		return slabs;
	}

	public void setSlabs(String slabs) {
		this.slabs = slabs;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getIcoStartDate() {
		return icoStartDate;
	}

	public void setIcoStartDate(Date icoStartDate) {
		this.icoStartDate = icoStartDate;
	}

	public Date getIcoEndDate() {
		return icoEndDate;
	}

	public void setIcoEndDate(Date icoEndDate) {
		this.icoEndDate = icoEndDate;
	}

	public Integer getFreeCoinPercentage() {
		return freeCoinPercentage;
	}

	public void setFreeCoinPercentage(Integer freeCoinPercentage) {
		this.freeCoinPercentage = freeCoinPercentage;
	}

	public BigDecimal getAleefRateInUSD() {
		return aleefRateInUSD;
	}

	public void setAleefRateInUSD(BigDecimal aleefRateInUSD) {
		this.aleefRateInUSD = aleefRateInUSD;
	}

}

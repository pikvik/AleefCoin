package com.aleef.dtos;

import java.math.BigDecimal;

public class PurchaseCoinDTO {

	private BigDecimal balanceCoins;

	private BigDecimal purchasedCoins;

	private BigDecimal freeCoins;

	private BigDecimal referralCoins;

	private String slabs;

	private BigDecimal totalDistributionTokens;

	private BigDecimal soldTokens;

	public BigDecimal getSoldTokens() {
		return soldTokens;
	}

	public void setSoldTokens(BigDecimal soldTokens) {
		this.soldTokens = soldTokens;
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

	public String getSlabs() {
		return slabs;
	}

	public void setSlabs(String slabs) {
		this.slabs = slabs;
	}

	public BigDecimal getTotalDistributionTokens() {
		return totalDistributionTokens;
	}

	public void setTotalDistributionTokens(BigDecimal totalDistributionTokens) {
		this.totalDistributionTokens = totalDistributionTokens;
	}

}

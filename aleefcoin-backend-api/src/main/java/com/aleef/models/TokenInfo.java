package com.aleef.models;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name = "Token_Info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class TokenInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "total_Tokens")
	@NotNull
	private BigDecimal totalTokens;

	@Column(name = "total_internal_tokens")
	@NotNull
	private BigDecimal totalInternalTokens;

	@Column(name = "total_ico_tokens")
	@NotNull
	private BigDecimal totalIcoTokens;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getTotalTokens() {
		return totalTokens;
	}

	public void setTotalTokens(BigDecimal totalTokens) {
		this.totalTokens = totalTokens;
	}

	public BigDecimal getTotalInternalTokens() {
		return totalInternalTokens;
	}

	public void setTotalInternalTokens(BigDecimal totalInternalTokens) {
		this.totalInternalTokens = totalInternalTokens;
	}

	public BigDecimal getTotalIcoTokens() {
		return totalIcoTokens;
	}

	public void setTotalIcoTokens(BigDecimal totalIcoTokens) {
		this.totalIcoTokens = totalIcoTokens;
	}

}

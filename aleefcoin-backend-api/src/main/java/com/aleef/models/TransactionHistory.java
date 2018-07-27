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
@Table(name = "transaction_history")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class TransactionHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "transaction_date")
	@NotNull
	private Date transactionDate;

	@Column(name = "from_address")
	@NotNull
	private String fromAddress;

	@Column(name = "to_address")
	@NotNull
	private String toAddress;

	@Column(name = "transfer_amount")
	@NotNull
	private Double transferAmount;

	@Column(name = "transfer_status")
	@NotNull
	private String transferStatus;

	@Column(name = "transaction_mode")
	@NotNull
	private String transactionMode;

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public Double getTransferAmount() {
		return transferAmount;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public void setTransferAmount(Double transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}

	public Integer getId() {
		return id;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

}

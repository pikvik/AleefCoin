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
@Table(name = "QRcode_Info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class QRcodeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "QR_key")
	@NotNull
	private String qrKey;

	@Column(name = "QR_value")
	@NotNull
	private String qrvalue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQrKey() {
		return qrKey;
	}

	public void setQrKey(String qrKey) {
		this.qrKey = qrKey;
	}

	public String getQrvalue() {
		return qrvalue;
	}

	public void setQrvalue(String qrvalue) {
		this.qrvalue = qrvalue;
	}

}

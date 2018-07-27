package com.aleef.models;

import java.io.Serializable;
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
@Table(name = "referral_percentages")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ReferralPercentages implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "level_one_percentages")
	private Integer levelOnePercentage;

	@Column(name = "level_two_percentages")
	private Integer levelTwoPercentages;

	@Column(name = "level_three_percentages")
	private Integer levelThreePercentages;

	@Column(name = "level_four_percentages")
	private Integer levelFourPercentages;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLevelOnePercentage() {
		return levelOnePercentage;
	}

	public void setLevelOnePercentage(Integer levelOnePercentage) {
		this.levelOnePercentage = levelOnePercentage;
	}

	public Integer getLevelTwoPercentages() {
		return levelTwoPercentages;
	}

	public void setLevelTwoPercentages(Integer levelTwoPercentages) {
		this.levelTwoPercentages = levelTwoPercentages;
	}

	public Integer getLevelThreePercentages() {
		return levelThreePercentages;
	}

	public void setLevelThreePercentages(Integer levelThreePercentages) {
		this.levelThreePercentages = levelThreePercentages;
	}

	public Integer getLevelFourPercentages() {
		return levelFourPercentages;
	}

	public void setLevelFourPercentages(Integer levelFourPercentages) {
		this.levelFourPercentages = levelFourPercentages;
	}

}

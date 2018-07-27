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
@Table(name = "level_percentage")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class LevelsPercentage implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "level_one")
	@NotNull
	private Integer levelOne;

	@Column(name = "level_two")
	@NotNull
	private Integer levelTwo;

	@Column(name = "level_three")
	@NotNull
	private Integer levelThree;

	@Column(name = "level_four")
	@NotNull
	private Integer levelFour;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLevelOne() {
		return levelOne;
	}

	public void setLevelOne(Integer levelOne) {
		this.levelOne = levelOne;
	}

	public Integer getLevelTwo() {
		return levelTwo;
	}

	public void setLevelTwo(Integer levelTwo) {
		this.levelTwo = levelTwo;
	}

	public Integer getLevelThree() {
		return levelThree;
	}

	public void setLevelThree(Integer levelThree) {
		this.levelThree = levelThree;
	}

	public Integer getLevelFour() {
		return levelFour;
	}

	public void setLevelFour(Integer levelFour) {
		this.levelFour = levelFour;
	}

}

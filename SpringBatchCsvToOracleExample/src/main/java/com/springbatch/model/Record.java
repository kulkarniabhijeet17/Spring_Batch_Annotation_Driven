package com.springbatch.model;

import java.util.Date;

public class Record {
	private String countryCode;
	private String countryName;
	private String countryIsdCode;
	private Integer regionCode;
	private String userCreated;
	private Date dateCreated;
	private String stateCode;
	private String stateName;

	public Record() {
	}

	public Record(String countryCode, String countryName, String countryIsdCode, Integer regionCode, String userCreated,
			Date dateCreated, String stateCode, String stateName) {
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.countryIsdCode = countryIsdCode;
		this.regionCode = regionCode;
		this.userCreated = userCreated;
		this.dateCreated = dateCreated;
		this.stateCode = stateCode;
		this.stateName = stateName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryIsdCode() {
		return countryIsdCode;
	}

	public void setCountryIsdCode(String countryIsdCode) {
		this.countryIsdCode = countryIsdCode;
	}

	public Integer getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(Integer regionCode) {
		this.regionCode = regionCode;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
}
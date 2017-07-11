package com.ibm.retain.session;

public class User {

	private String retainid;
	private String retainpassword;
	private String intranetid;
	private String intranetpassword;
	private String country;
	private int timezone;
	
	public String getRetainID() {
		return retainid;
	}

	public void setRetainID(String retainid) {
		this.retainid = retainid;
	}
	
	public String getRetainPassword() {
		return retainpassword;
	}

	public void setRetainPassword(String retainpassword) {
		this.retainpassword = retainpassword;
	}
	
	public String getIntranetID() {
		return intranetid;
	}

	public void setIntranetID(String intranetid) {
		this.intranetid = intranetid;
	}
	
	public String getIntranetPassword() {
		return intranetpassword;
	}

	public void setIntranetPassword(String intranetpassword) {
		this.intranetpassword = intranetpassword;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public int getTimeZone() {
		return timezone;
	}

	public void setTimeZone(int timezone) {
		this.timezone = timezone;
	}

}

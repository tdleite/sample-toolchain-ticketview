package com.ibm.retain.utils;

import com.ibm.retain.sdi.RETAINSDIException;
import com.ibm.retain.sdi.dataflags.RetainInfo;
import com.ibm.retain.session.User;

public abstract class Retain {

	static String bill = "retwb";
	static String extBill = "retwb";
	static int instance = RetainInfo.RS4_PROD1;
	static int timezone = 0;

	public static String canLogin(User user) {
		try {
			new RetainInfo(user.getRetainID(), user.getRetainPassword(), bill, extBill, user.getCountry(), instance);
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static RetainInfo getRetainInfo(User user) throws IllegalArgumentException, RETAINSDIException {
		return new RetainInfo(user.getRetainID(), user.getRetainPassword(), bill, extBill, user.getCountry(), instance);
	}

	public static int getTimezone() {
		return timezone;
	}

}

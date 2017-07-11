package com.ibm.retain.session;

import java.util.ArrayList;
import java.util.Date;
import java.security.SecureRandom;
import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ibm.json.java.JSONObject;
import com.ibm.retain.objects.Engineer;
import com.ibm.retain.utils.EcuRep;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.Retain;
import com.ibm.retain.utils.Utils;

public class SessionManager {

	private static ArrayList<Session> sessions;
	private static SecureRandom random = new SecureRandom();

	public static ArrayList<Session> getSessions() {
		return sessions;
	}

	public static void setSessions() {
		sessions = new ArrayList<Session>();
	}

	public static void login(ServletRequest request, ServletResponse response) throws IOException {
		String retainid = request.getParameter("retainid");
		String retainpassword = request.getParameter("retainpassword");
		String intranetid = request.getParameter("intranetid");
		String intranetpassword = request.getParameter("intranetpassword");
		int timezone = 0;
		
		try {
			timezone = Integer.parseInt(request.getParameter("timezone"));
		} catch (Exception e) {
			Utils.sendResponseError(request, response, 400, "Parameter timezone is not valid.");
			return;
		}
				
		if (retainid == null) {
			Utils.sendResponseError(request, response, 400, "Parameter retainid is missing.");
			return;
		}
		
		if (retainpassword == null) {
			Utils.sendResponseError(request, response, 400, "Parameter retainpassword is missing.");
			return;
		}
		
		if (intranetid == null) {
			Utils.sendResponseError(request, response, 400, "Parameter intranetid is missing.");
			return;
		}
		
		if (intranetpassword == null) {
			Utils.sendResponseError(request, response, 400, "Parameter intranetpassword is missing.");
			return;
		}

		Session session = new Session();
		session.setSessionID(generateSessionID());
		session.setLastAccess(new Date());
		User user = new User();
		user.setRetainID(retainid);
		user.setRetainPassword(retainpassword);
		user.setIntranetID(intranetid);
		user.setIntranetPassword(intranetpassword);
		user.setCountry("000");
		user.setTimeZone(timezone);
		session.setUser(user);

		String error = Retain.canLogin(user);
		if (error == null) {
			sessions.add(session);

			JSONObject json = new JSONObject();
			json.put("message", "Login successful.");
			json.put("sessionID", session.getSessionID());

			try {
				ArrayList<Obj> objs = new Engineer().read(null);
				for (Obj o : objs) {
					if (o.getString("retainid").equalsIgnoreCase(retainid)) {
						json.put("userName", o.getString("name"));
						break;
					}
				}
			} catch (Exception e) {
				json.put("userName", "");
			}
			
			if (EcuRep.canLogin(user)) {
				Utils.sendResponseOK(request, response, "application/json", json.toString());
			
			} else {
				Utils.sendResponseError(request, response, 401, "Intranet login Failed");
			}			

		} else	{
			Utils.sendResponseError(request, response, 401, "Retain login Failed: " + error);
		}
	}

	public static String generateSessionID() {
		return new BigInteger(130, random).toString(32);
	}

	public static boolean authenticate(ServletRequest request, ServletResponse response) throws IOException {
		Session session = getSession(request);

		if (session == null) {
			Utils.sendResponseError(request, response, 401, "Invalid Session. Please Login.");
			return false;

		} else {
			session.setLastAccess(new Date());
		}
		return true;
	}

	public static Session getSession(ServletRequest request) {
		
		String sessionID = request.getParameter("sessionID");		
		if (sessionID != null)
			return getSession(sessionID);		
		
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		if (cookies == null) 
			return null;

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("sessionID")) {
				sessionID = cookie.getValue();
				return getSession(sessionID);
			}
		}

		return null;
	}
		
	public static Session getSession(String sessionID) {
		for (Session s : sessions) {
			if (s.getSessionID().equalsIgnoreCase(sessionID)) {
				return s;
			}
		}
		return null;
	}

	public static User getUser(String sessionID) {
		for (Session s : sessions) {
			if (s.getSessionID().equalsIgnoreCase(sessionID)) {
				return s.getUser();
			}
		}
		return null;
	}

	public static void logout(ServletRequest request, ServletResponse response) throws IOException {
		Session session = getSession(request);

		if (session == null) {
			Utils.sendResponseError(request, response, 401, "Invalid Session. Please Login.");
			return;

		} else {
			sessions.remove(session);

			JSONObject json = new JSONObject();
			json.put("message", "Logout successful.");

			Utils.sendResponseOK(request, response, "application/json", json.toString());
			return;
		}
	}

	public static void checkSession(ServletRequest request, ServletResponse response) throws IOException {
		Session session = getSession(request);

		if (session != null) {
			String retainid = SessionManager.getUser(session.getSessionID()).getRetainID();

			JSONObject json = new JSONObject();
			json.put("message", "Session is valid.");
			json.put("retainid", retainid);

			try {
				ArrayList<Obj> objs = new Engineer().read(null);
				for (Obj o : objs) {
					if (o.getString("retainid").equalsIgnoreCase(retainid)) {
						json.put("name", o.getString("name"));
						break;
					}
				}
			} catch (Exception e) {
				json.put("userName", "");
			}

			Utils.sendResponseOK(request, response, "application/json", json.toString());

		} else {
			Utils.sendResponseError(request, response, 401, "Session is not valid. Please login.");
		}
	}

}

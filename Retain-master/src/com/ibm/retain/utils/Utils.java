package com.ibm.retain.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.ibm.json.java.JSONObject;

public class Utils {

	public static void getWebApp(ServletRequest request, ServletResponse response) throws IOException, ServletException {

		byte[] encoded = null;
		try {
			File utils = new File(new Utils().getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			File webContent = utils.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
			
			encoded = Files.readAllBytes(Paths.get(webContent.toPath().toString().replace("%20", " ") + "\\" + "html/index.html"));
			String html = new String(encoded, "UTF-8");

			sendResponseOK(request, response, "text/html", html);
			return;

		} catch (IOException e1) {
			try {
				File utils = new File(new Utils().getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
				File webContent = utils.getParentFile().getParentFile();
				
				encoded = Files.readAllBytes(Paths.get(webContent.toPath().toString().replace("%20", " ") + "\\" + "html/index.html"));
				String html = new String(encoded, "UTF-8");

				sendResponseOK(request, response, "text/html", html);
				return;

			} catch (IOException e2) {
				sendResponseError(request, response, 404, "webApp (index.html) not found.");
				return;
			}
		}
	}

	public static void sendResponseOK(ServletRequest request, ServletResponse response, String contentType, String data) throws IOException {
		((HttpServletResponse) response).setStatus(200);
		response.setContentType(contentType);
		response.getWriter().write(data);
	}

	public static void sendResponseError(ServletRequest request, ServletResponse response, int HTTPcode, String message) throws IOException {
		JSONObject json = new JSONObject();
		json.put("message", message);

		((HttpServletResponse) response).setStatus(HTTPcode);
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}

	public static boolean isNumber(String str) {
	    if (str == null)
	        return false;
	    char[] data = str.toCharArray();
	    if (data.length <= 0)
	        return false;
	    int index = 0;
	    if (data[0] == '-' && data.length > 1)
	        index = 1;
	    for (; index < data.length; index++) {
	        if (data[index] < '0' || data[index] > '9')
	            return false;
	    }
	    return true;
	}

	public static long getDiffDays(Calendar date1, Calendar date2) {

		 long diffTime = date1.getTime().getTime() - date2.getTime().getTime();
		 long diffDays = diffTime / (1000 * 60 * 60 * 24);

		 return Math.abs(diffDays);
	}
	
	public static long getDiffMinutes(Calendar date1, Calendar date2) {

		 long diffTime = date1.getTime().getTime() - date2.getTime().getTime();
		 long diffMinutes = diffTime / (1000 * 60);

		 return Math.abs(diffMinutes);
	}
	
	public static int getTimeZone() {
		Calendar now = Calendar.getInstance();
		TimeZone timeZone = now.getTimeZone();
		return timeZone.getOffset( System.currentTimeMillis() ) / 60 / 1000;
	}
	
	public static Date adjustTimezone(Date date, int timezone) {
		int diff;
		int localTimezone = getTimeZone();
		
		if (timezone == localTimezone) {
			diff = 0;
			
		} else if (timezone > localTimezone) {
			diff = timezone - localTimezone;
		
		} else {
			diff = timezone - localTimezone;
		}
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, diff);

		return c.getTime();
	}
	
	public static String toString(Calendar c) {
		int year = c.get(Calendar.YEAR);
		if (year < 2000)
			year = 2000 + year;
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		String s = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute);
				
		return  s;
	}

}

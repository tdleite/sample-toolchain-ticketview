package com.ibm.retain.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.ibm.json.java.JSONObject;
import com.ibm.retain.session.User;

public abstract class EcuRep {
	
	private static String url = "ecurep.mainz.de.ibm.com";
	private static int port = 443;

	public static HttpResponse request(User user, String path) throws Exception {
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https").
			setHost(getURL()).
			setPort(getPort()).
			setPath(path);
		
		builder.addParameter("format", "tree");
				
		java.net.URI uri = builder.build();
			
		HttpGet get = new HttpGet(uri);
			
		String username = user.getIntranetID();
		String password = user.getIntranetPassword();		  
	    String authentication = new Base64().encodeAsString(new String(username + ":" + password).getBytes());
					
	    get.setHeader("Authorization", "Basic " + authentication);
	    
		HttpClient httpclient = HttpClientBuilder.create().build();
			
		HttpResponse response = httpclient.execute(get);		
			
		return response;
	}
	
	public static String getNotesID(User user, String pmr) throws Exception {
		HttpResponse response = request(user, "/rest/1/ticket/" + pmr + "/notes/id");	
			
		if (response.getStatusLine().getStatusCode() == 200) {
			String content = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
					  .lines().collect(java.util.stream.Collectors.joining("\n"));
			
			JSONObject json = JSONObject.parse(content);
			return json.get("uid").toString();
			
				
		} else {
			throw new Exception("Error with EcuRep server: " + response.getStatusLine().getStatusCode());
		}
	}
	
	public static String getContent(HttpResponse response) throws Exception {
		return new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
				  .lines().collect(java.util.stream.Collectors.joining("\n"));
	}

	public static boolean canLogin(User user) {	
		try {
			HttpResponse response = request(user, "/rest/1/ticket/10368,004,000/notes/id");
			
			if (response.getStatusLine().getStatusCode() == 401) {
				return false;
				
			} else {
				return true;
			}
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String getURL() {
		return url;
	}
	
	public static int getPort() {
		return port;
	}

}
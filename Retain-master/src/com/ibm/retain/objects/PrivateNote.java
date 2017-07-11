package com.ibm.retain.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.retain.session.Cache;
import com.ibm.retain.session.SessionManager;
import com.ibm.retain.session.User;
import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.EcuRep;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.Utils;

public class PrivateNote extends Action {

	@Override
	public String getTableName() {
		return "PRIVATENOTES";
	}
	
	@Override
	public ArrayList<String> getAttributesList() throws ClassNotFoundException, SQLException {
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("pmr");
		attributes.add("body");
		
		return attributes;
	}
	
	@Override
	public String create(Map<String, String> attributes) throws Exception {
		String pmr = attributes.get("pmr");
		String body = attributes.get("body");
		
		if (pmr == null) {
			throw new Exception("Parameter pmr is missing.");
		}
		
		User user = getSession().getUser();
		String notesID = EcuRep.getNotesID(user, pmr);
		
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https").
			setHost(EcuRep.getURL()).
			setPort(EcuRep.getPort()).
			setPath("/rest/1/ticket/" + notesID + "/notes");
				
		java.net.URI uri = builder.build();
			
		HttpPost post = new HttpPost(uri);
			
		String username = user.getIntranetID();
		String password = user.getIntranetPassword();	  
	    String authentication = new Base64().encodeAsString(new String(username + ":" + password).getBytes());
					
		post.setHeader("Authorization", "Basic " + authentication);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("body", body));
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		HttpClient httpclient = HttpClientBuilder.create().build();
			
		HttpResponse response = httpclient.execute(post);	
		
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("Error with EcuRep server: " + response.getStatusLine().getStatusCode());
		}
		
		if (Cache.active) {
			ArrayList<Obj> objs = Cache.get(new PMR().getTableName());
			for (Obj o : objs) {
				if (o.getString("number").equalsIgnoreCase(pmr)) {
					o.setValue("text", null);
					break;
				}
			}
		}
		
		return "Done.";
	}
	
	@Override
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {
		String pmr = attributes.get("pmr");
		
		if (pmr == null) {
			throw new Exception("Parameter pmr is missing.");
		}
		
		User user = getSession().getUser();
		String notesID = EcuRep.getNotesID(user, pmr);
		
		HttpResponse response = EcuRep.request(user, "/rest/1/ticket/" + notesID + "/notes");
		String content = EcuRep.getContent(response);
		
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("Error with EcuRep server: " + response.getStatusLine().getStatusCode());
		} 
				
		ArrayList<Obj> objs = new ArrayList<Obj>();		
		JSONArray responseJSON = JSONArray.parse(content);
		
		for (int i=0; i < responseJSON.size(); i++) {
			Obj obj = new Obj();
			JSONObject privatenote = (JSONObject) responseJSON.get(i);
			
			obj.setValue("createdby", privatenote.get("createdBy"));
			obj.setValue("type", privatenote.get("type"));
			obj.setValue("mimetype", privatenote.get("mimeType"));
			obj.setValue("body", privatenote.get("body"));
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(Long.parseLong(privatenote.get("timestamp").toString()));
			obj.setValue("date", Utils.toString(c));
			obj.setValue("formattedText", "Private Note by " + obj.getString("createdby") + " on " + obj.getString("date"));		
			
			objs.add(obj);
		}
		
		return objs;
	}
	
	@Override
	public ArrayList<Obj> select(Map<String, String> attributes) throws Exception {
		return super.select(attributes);
	}
	
	@Override
	public String update(Map<String, String> attributes) throws Exception {
		return super.update(attributes);
	}
	
	@Override
	public String delete(Map<String, String> attributes) throws Exception {
		return super.delete(attributes);
	}
	
}

package com.ibm.retain.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.retain.session.User;
import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.EcuRep;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.Utils;

public class EcuRepData extends Action {
	@Override
	public String getTableName() {
		return "ECUREPFILES";
	}
	
	@Override
	public ArrayList<String> getAttributesList() throws ClassNotFoundException, SQLException {
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("pmr");
		
		return attributes;
	}
	
	@Override
	public String create(Map<String, String> attributes) throws Exception {
		return super.create(attributes);
	}
	
	@Override
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {
		String pmr = attributes.get("pmr");
		
		if (pmr == null) {
			throw new Exception("Parameter pmr is missing.");
		}
		
		User user = getSession().getUser();
		
		HttpResponse response = EcuRep.request(user, "/rest/1/tickets/" + pmr + "/files");
		String content = EcuRep.getContent(response);
		
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("Error with EcuRep server: " + response.getStatusLine().getStatusCode());
		} 
				
		ArrayList<Obj> objs = new ArrayList<Obj>();		
		JSONObject responseJSON = JSONObject.parse(content);
		JSONArray items = (JSONArray) responseJSON.get("items");
		
		for (int i=0; i < items.size(); i++) {			
			JSONObject data = (JSONObject) items.get(i);
			
			if (data.get("name").toString().equalsIgnoreCase("0-all_data")) {
				JSONArray files = (JSONArray) data.get("children");				
				Obj obj = new Obj();
				obj.setValue("name", data.get("name").toString());
				obj.setValue("children", files);
				obj.setValue("path", data.get("path").toString());
				objs.add(obj);
			
			} else if (data.get("name").toString().equalsIgnoreCase("mail")) {
				JSONArray files = (JSONArray) data.get("children");				
				Obj obj = new Obj();	
				obj.setValue("name", data.get("name").toString());
				obj.setValue("children", files);
				obj.setValue("path", data.get("path").toString());
				objs.add(obj);
			}
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

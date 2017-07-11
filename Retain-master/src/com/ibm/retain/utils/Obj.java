package com.ibm.retain.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class Obj {

	private Map<String, Object> attributes;

	public Obj() {
		attributes = new HashMap<String, Object>();
	}

	public void setValue(String attribute, Object value) {
		attributes.put(attribute, value);
	}
	
	public Object getObject(String attribute) {
		return attributes.get(attribute);
	}

	public String getString(String attribute) {
		return (String) attributes.get(attribute);
	}

	public int getInt(String attribute) {
		return Integer.valueOf(attributes.get(attribute).toString());
	}

	public float getFloat(String attribute) {
		return (Float) attributes.get(attribute);
	}
	
	public long getLong(String attribute) {
		return (Long) attributes.get(attribute);
	}

	public Obj getObj(String attribute) throws Exception {
		return (Obj) attributes.get(attribute);
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		Iterator it = attributes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        Object key = pair.getKey();
	        Object value = pair.getValue();
	        if (value == null)
	        	value = "";

	        if (value.toString().startsWith("{")) {
	        	JSONObject jsonObjAttr;
	        	try {
	        		jsonObjAttr = JSONObject.parse(value.toString());
	        		json.put(key, jsonObjAttr);
	        	} catch (Exception e) {
	        		json.put(key, value);
	        	}

	        } else if (value.toString().startsWith("[")) {
	        	JSONArray jsonArrAttr;
	        	try {
	        		jsonArrAttr = JSONArray.parse(value.toString());
	        		json.put(key, jsonArrAttr);
	        	} catch (Exception e) {
	        		json.put(key, value);
	        	}

	        
	        } else if (value instanceof Obj) {
	        	json.put(key, ((Obj) value).toJSON());
	        	
	        } else {
	        	json.put(key, value);        	
	        }
	    }

		return json;
	}

}

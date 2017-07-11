package com.ibm.retain.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.Obj;

public class View extends Action {

	@Override
	public String getTableName() {
		return "VIEWS";
	}
	
	@Override
	public String getOrderBy() throws Exception {
		return "name";
	}
	
	@Override
	public String create(Map<String, String> attributes) throws Exception {
		String viewid = attributes.get("viewid");
		String retainid = attributes.get("retainid");
		String name = attributes.get("name");
		String json = attributes.get("json");
		String ispublic = attributes.get("ispublic");
		String ismain = attributes.get("ismain");

		if (name == null) {
			throw new Exception("Parameter name is missing.");
		}
		
		if (ismain == null) {
			throw new Exception("Parameter ismain is missing.");
		}
		
		if (!(ismain.equalsIgnoreCase("0") || ismain.equalsIgnoreCase("1"))) {
			throw new Exception("Parameter ismain should be 0 or 1.");
		}

		HashMap<String, String> tempAttributes = new HashMap<String, String>();
		tempAttributes.put("name", name);
		tempAttributes.put("ispublic", "1");
		ArrayList<Obj> objs = read(tempAttributes);
		if (objs.size() > 0 && !objs.get(0).getString("retainid").equalsIgnoreCase(retainid)) {
			throw new Exception("Name " + name + " already in use. Please choose another one.");
		}

		if (json == null) {
			throw new Exception("Parameter json is missing.");
		}

		if (ispublic == null) {
			throw new Exception("Parameter ispublic is missing.");
		}

		if (!(ispublic.equalsIgnoreCase("0") || ispublic.equalsIgnoreCase("1"))) {
			throw new Exception("Parameter ispublic should be 0 or 1.");
		}
		
		if (ismain.equalsIgnoreCase("1")) {
			tempAttributes = new HashMap<String, String>();
			tempAttributes.put("retainid", retainid);
			objs = read(tempAttributes);
			for (Obj o : objs) {
				tempAttributes = new HashMap<String, String>();
				tempAttributes.put("retainid", retainid);
				tempAttributes.put("ismain", "0");
				tempAttributes.put(getPrimaryKeyName(), o.getString(getPrimaryKeyName()));
				super.update(tempAttributes);	
			}
		}

		checkJSON(json);
		
		tempAttributes = new HashMap<String, String>();
		tempAttributes.put("viewid", viewid);
		objs = read(tempAttributes);
		if (viewid != null && objs.size() > 0 && objs.get(0).getString("name").equalsIgnoreCase(name)) {
			return update(attributes);
		}

		return super.create(attributes); 
	}

	@Override
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {
		if (attributes != null) {
			attributes.remove("retainid");
		}
		return super.read(attributes);
	}
	
	@Override
	public ArrayList<Obj> select(Map<String, String> attributes) throws Exception {
		return super.select(attributes);
	}
	
	@Override
	public String update(Map<String, String> attributes) throws Exception {
		Map<String, String> tempAttributes = new HashMap<String, String>();
		String retainid = attributes.get("retainid");
		String ismain = attributes.get("ismain");
		
		if (ismain != null && ismain.equalsIgnoreCase("1")) {
			tempAttributes = new HashMap<String, String>();
			tempAttributes.put("retainid", retainid);
			ArrayList<Obj> objs = read(tempAttributes);
			for (Obj o : objs) {
				tempAttributes = new HashMap<String, String>();
				tempAttributes.put("retainid", retainid);
				tempAttributes.put("ismain", "0");
				tempAttributes.put(getPrimaryKeyName(), o.getString(getPrimaryKeyName()));
				super.update(tempAttributes);	
			}
		}
		
		return super.update(attributes);
	}
	
	@Override
	public String delete(Map<String, String> attributes) throws Exception {
		return super.delete(attributes);
	}

	public void checkJSON(String view) throws Exception {
		JSONObject json = null;
		try {
			json = JSONObject.parse(view);
		} catch (Exception e) {
			throw new Exception("Parameter view is not a valid JSON.");
		}

		Object temp;
		JSONArray groups;
		temp = json.get("groups");
		if (temp instanceof JSONArray) {
			groups = (JSONArray) json.get("groups");
		} else {
			throw new Exception("Parameter groups is not a JSON array.");
		}

		for (int i = 0; i < groups.size(); i++) {
			JSONObject group;
			temp = groups.get(i);
			if (temp instanceof JSONObject) {
				group = (JSONObject) groups.get(i);
			} else {
				throw new Exception("Parameter index " + i + " of groups is not a JSON object.");
			}

			String label;
			temp = group.get("label");
			if (temp instanceof String) {
				label = group.get("label").toString();
			} else {
				throw new Exception("Parameter label of group index " + i + " is not valid.");
			}

			if (label.length() > 20) {
				throw new Exception("Parameter label of group index " + i + " should not exceed 20 characters.");
			}

			JSONArray columns;
			temp = group.get("columns");
			if (temp instanceof JSONArray) {
				columns = (JSONArray) group.get("columns");
			} else {
				throw new Exception("Parameter columns of group index " + i + " is a JSON array.");
			}

			for (int j = 0; j < columns.size(); j++) {
				JSONObject column;
				temp = columns.get(j);
				if (temp instanceof JSONObject) {
					column = (JSONObject) columns.get(j);
				} else {
					throw new Exception("Parameter index " + j + " of group index " + i + " is not a JSON object.");
				}

				String columntype;
				temp = column.get("columntype");
				if (temp instanceof String) {
					columntype = column.get("columntype").toString();
				} else {
					throw new Exception("Parameter columnType of column index " + j + " of group index " + i + " is not valid.");
				}

				if (!(columntype.equalsIgnoreCase("queue") || columntype.equalsIgnoreCase("engineer") || columntype.equalsIgnoreCase("attribute")
						|| columntype.equalsIgnoreCase("status") || columntype.equalsIgnoreCase("client") || columntype.equalsIgnoreCase("custom"))) {
					throw new Exception("Parameter columntype of column index " + j + " of group index " + i + " should be queue, engineer, attribute, status, client or custom.");
				}

				String columnvalue;
				temp = column.get("columnvalue");
				if (temp instanceof String) {
					columnvalue = column.get("columnvalue").toString();
				} else {
					throw new Exception("Parameter columnvalue of column index " + j + " of group index " + i + " is not valid.");
				}

				String columnlabel;
				temp = column.get("columnlabel");
				if (temp instanceof String) {
					columnlabel = column.get("columnlabel").toString();
				} else {
					throw new Exception("Parameter columnlabel of column index " + j + " of group index " + i + " is not valid.");
				}
			}
		}
	}
}

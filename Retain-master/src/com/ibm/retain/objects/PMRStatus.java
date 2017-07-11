package com.ibm.retain.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.Utils;

public class PMRStatus extends Action {

	@Override
	public String getTableName() {
		return "PMRSTATUS";
	}
	
	@Override
	public String getOrderBy() throws Exception {
		return "changedon DESC";
	}
	
	@Override
	public String create(Map<String, String> attributes) throws Exception {
		
		String retainid = getSession().getUser().getRetainID();
		attributes.put("changedby", retainid);
		
		attributes.put("changedon", "");
		attributes.put("timezone", "" + Utils.getTimeZone());
		
		return super.create(attributes);
	}
	
	@Override
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {
		return super.read(attributes);
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

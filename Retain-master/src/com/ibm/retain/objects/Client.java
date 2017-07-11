package com.ibm.retain.objects;

import java.util.ArrayList;
import java.util.Map;

import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.Obj;

public class Client extends Action {

	@Override
	public String getTableName() {
		return "CLIENTS";
	}
	
	@Override
	public String getOrderBy() throws Exception {
		return "name";
	}
	
	@Override
	public String create(Map<String, String> attributes) throws Exception {
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

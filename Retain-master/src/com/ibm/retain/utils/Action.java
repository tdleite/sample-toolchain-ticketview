package com.ibm.retain.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.json.java.JSONObject;
import com.ibm.retain.session.Cache;
import com.ibm.retain.session.Session;
import com.ibm.retain.session.SessionManager;

public abstract class Action {
	
	private Session session = null;

	public void doIt(ServletRequest request, ServletResponse response, String action) throws IOException {
		
		try {
			setSession(SessionManager.getSession(request));
			
			Map<String, String> attributes = getParametersFromRequest(request);
			JSONObject json = new JSONObject();
			
			Method  method = this.getClass().getDeclaredMethod(action, Map.class);
			Object ret = method.invoke(this, attributes);

			if (ret instanceof String) {
				String s = (String) ret;				
				json.put("message", s);
				Utils.sendResponseOK(request, response, "application/json", json.toString());
				
			} else if (ret instanceof ArrayList) {
				ArrayList<Obj> objs = (ArrayList<Obj>) ret;
				StringBuilder jsonArray = new StringBuilder("{ \"objects\": [");
				for (Obj obj : objs) {
					jsonArray.append(obj.toJSON().toString());
					jsonArray.append(",");
				}
				if (jsonArray.toString().endsWith(","))
					jsonArray.deleteCharAt(jsonArray.toString().length()-1);
				jsonArray.append("]}");
				Utils.sendResponseOK(request, response, "application/json", jsonArray.toString());
			}
	
		} catch (Exception e) {
			Utils.sendResponseError(request, response, 400, e.getCause().getMessage());
		}
	}

	public ArrayList<String> getAttributesList() throws ClassNotFoundException, SQLException {
		
		if (Cache.active) {
			ArrayList<Obj> objs = Cache.get(getTableName() + "_attributes");
			ArrayList<String> attributes = new ArrayList<String>();
			if (objs != null) {
				for (Obj o : objs) {
					attributes.add(o.getString("column_name").toLowerCase());
				}
				return attributes;
			}
		}
		
		DAO dao = new DAO();
		dao.connect();

		ResultSet rs = dao.execute("SELECT column_name FROM all_tab_cols WHERE table_name = '" + getTableName() + "'", null);

		ArrayList<Obj> objs = new ArrayList<Obj>();
		ArrayList<String> attributes = new ArrayList<String>();
		if (rs != null) {
			objs = getObjs(rs);
		}
			
		if (Cache.active) {
			Cache.put(getTableName() + "_attributes", objs);
		}

		dao.disconnect();
		return attributes;
	}
	
	public Map<String, String> getParametersFromRequest(ServletRequest request) throws ClassNotFoundException, SQLException {		
		Map<String, String> attributes = new HashMap<String, String>();
		for (String attribute : getAttributesList()) {
			String value = request.getParameter(attribute);
			if (value != null) {
				attributes.put(attribute, value);
			}
		}
		if (getAttributesList().contains("retainid")) {
			attributes.put("retainid", getSession().getUser().getRetainID());
		}
		return attributes;
	}
	
	public String create(Map<String, String> attributes) throws Exception {
		
		checkIfAttributesExist(attributes);
		checkIfHasAllAttributes(attributes);
		
		StringBuilder s = new StringBuilder("INSERT INTO " + getTableName() + "(");
		StringBuilder s2 = new StringBuilder();
		ArrayList<String> values = new ArrayList<String>();
		
		for (String attribute : attributes.keySet()) {
			s.append(attribute + ",");
			s2.append("?,");
			values.add(attributes.get(attribute));
		}
		
		s.deleteCharAt(s.length()-1);
		s2.deleteCharAt(s2.length()-1);
		
		s.append(") VALUES (");
		s.append(s2.toString());
		s.append(")");
		
		DAO dao = new DAO();
		dao.connect();
		
		dao.execute(s.toString(), values);

		dao.commit();
		dao.disconnect();

		Cache.clear(getTableName());

		return "Done.";
	}
	
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {
		
		checkIfAttributesExist(attributes);
		
		if (Cache.active) {
			ArrayList<Obj> objs = Cache.get(getTableName());
			if (objs == null) {
				objs = select(null);
				Cache.put(getTableName(), objs);
			}
			
			objs = filter(objs, attributes);			
			return objs;

		} else {
			return select(attributes);
		}
	}
	
	public String getWhereClause(Map<String, String> attributes) {
		if (attributes == null || attributes.size() == 0) {
			return " ";
			
		} else {
			String w = "WHERE ";			
			for (String attr : attributes.keySet()) {
				if (!w.equalsIgnoreCase("WHERE "))
					w += "AND ";
				w += attr + " = '" + attributes.get(attr) + "' ";				
			}
			return w;
		}
	}
	
	public ArrayList<Obj> select(Map<String, String> attributes) throws Exception {
		
		DAO dao = new DAO();
		dao.connect();
		ResultSet rs = dao.execute("SELECT * FROM " + getTableName() + " " + getWhereClause(attributes) + " ORDER BY " + getOrderBy(), null);

		ArrayList<Obj> objs = getObjs(rs);
		
		dao.disconnect();
		return objs;
	}
	
	public ArrayList<Obj> getObjs(ResultSet rs) throws SQLException {
		
		ResultSetMetaData rsmd = rs.getMetaData();
		ArrayList<Obj> objs = new ArrayList<Obj>();
		if (rs != null) {
			while (rs.next()) {
				Obj obj = new Obj();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (rsmd.getColumnType(i) == Types.TIMESTAMP) {
						Calendar c = Calendar.getInstance();
						c.set(Calendar.YEAR, rs.getDate(i).getYear() + 1900);
						c.set(Calendar.MONTH, rs.getDate(i).getMonth());
						c.set(Calendar.DATE, rs.getDate(i).getDate());
						c.set(Calendar.HOUR_OF_DAY, rs.getTime(i).getHours());
						c.set(Calendar.MINUTE, rs.getTime(i).getMinutes());
						Date d = Utils.adjustTimezone(c.getTime(),  rs.getInt("timezone"));						
						obj.setValue(rsmd.getColumnLabel(i).toLowerCase(), Utils.toString(c));
						
					} else {
						obj.setValue(rsmd.getColumnLabel(i).toLowerCase(), rs.getString(i));
					}		
				}
				objs.add(obj);
			}
		}
		return objs;
	}
	
	public String update(Map<String, String> attributes) throws Exception {
		
		checkIfAttributesExist(attributes);
		
		String primaryKey = getPrimaryKeyName().toLowerCase();
		
		if (attributes.get(primaryKey) == null) {
			throw new Exception("Parameter " + primaryKey + " is missing");
		}
		
		StringBuilder s = new StringBuilder("UPDATE " + getTableName() + " SET");
		
		ArrayList<String> values = new ArrayList<String>();		

		for (String attribute : attributes.keySet()) {
			if (attribute.equalsIgnoreCase(primaryKey))
				continue;
				
			s.append(" " + attribute + " = ?,");
			values.add(attributes.get(attribute));
		}
						
		s.deleteCharAt(s.length()-1);
		
		s.append(" WHERE " + primaryKey + " = ?");
		
		values.add(attributes.get(primaryKey));
		
		DAO dao = new DAO();
		dao.connect();
		
		dao.execute(s.toString(), values);

		dao.commit();
		dao.disconnect();

		Cache.clear(getTableName());

		return "Object updated.";		
	}
	
	public ArrayList<Obj> filter(ArrayList<Obj> objs, Map<String, String> attributes) {
		
		if (attributes == null || attributes.size() == 0) {
			return objs;
		}
		
		ArrayList<Obj> filteredObjs = new ArrayList<Obj>();
		boolean filter = true;
		for (Obj obj : objs) {
			filter = true;
			for (String attr : attributes.keySet()) {
				if (!obj.getString(attr).equalsIgnoreCase(attributes.get(attr))) {
					filter = false;
				}					
			}
			if (filter) {
				filteredObjs.add(obj);
			}
		}
		
		return filteredObjs;
	}
	
	public void checkIfAttributesExist(Map<String, String> attributesToCheck) throws Exception {
		
		if (attributesToCheck == null)
			return;
		
		ArrayList<String> attributes = getAttributesList();
		boolean ok = false;
		for (String attributeToCheck : attributesToCheck.keySet()) {
			ok = false;
			for (String attribute : attributes) {
				if (attributeToCheck.equalsIgnoreCase(attribute)) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				throw new Exception("Parameter " + attributeToCheck + " is invalid");
			}
		}		
	}
	
	public void checkIfHasAllAttributes(Map<String, String> attributesToCheck) throws Exception {
		
		ArrayList<String> attributes = getAttributesList();
		boolean ok = false;
		
		for (String attribute : attributes) {
			ok = false;
			
			if (attribute.equalsIgnoreCase(getPrimaryKeyName())) {
				continue;
			}
			
			for (String attributeToCheck : attributesToCheck.keySet()) {
				if (attributeToCheck.equalsIgnoreCase(attribute)) {
					ok = true;
					break;
				}
			}
			
			if (!ok) {
				throw new Exception("Parameter " + attribute + " is missing");
			}
		}
	}
	
	public String getPrimaryKeyName() throws ClassNotFoundException, SQLException {
		
		if (Cache.active) {
			ArrayList<Obj> objs = Cache.get(getTableName() + "_primarykey");
			if (objs != null) {
				for (Obj o : objs) {
					return o.getString("column_name").toLowerCase();
				}
			}
		}
		
		DAO dao = new DAO();
		dao.connect();
		
		String s = "SELECT column_name FROM all_cons_columns WHERE constraint_name = (";
		s += "SELECT constraint_name FROM user_constraints ";
		s += "WHERE UPPER(table_name) = UPPER('" + getTableName() + "') AND CONSTRAINT_TYPE = 'P')";

		ResultSet rs = dao.execute(s, null);

		ArrayList<Obj> objs = new ArrayList<Obj>();
		String primaryKey = null;
		if (rs != null) {
			objs = getObjs(rs);
		}
		
		if (Cache.active) {
			Cache.put(getTableName() + "_primarykey", objs);
		}
		
		if (objs != null && objs.size() > 0) {
			primaryKey = objs.get(0).getString("column_name");
		}
		
		dao.disconnect();
		return primaryKey.toLowerCase();
	}
	
	public String delete(Map<String, String> attributes) throws Exception {
		
		checkIfAttributesExist(attributes);
				
		if (select(attributes).size() == 0) {
			return "Nothing to delete.";
		}		
		
		DAO dao = new DAO();
		dao.connect();

		dao.execute("DELETE FROM " + getTableName() + " " + getWhereClause(attributes), null);

		dao.commit();
		dao.disconnect();

		Cache.clear(getTableName());

		return "Object(s) deleted.";
	}
	
	public String getTableName() {
		return null;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return this.session;
	}
	
	public String getOrderBy() throws Exception {
		return getPrimaryKeyName();
	}

}

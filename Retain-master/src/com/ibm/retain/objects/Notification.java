package com.ibm.retain.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.Utils;

public class Notification extends Action {

	private static Map<String, Obj> notifications;
	private static String NEW_PMR_ON_QUEUE = "NEW_PMR_ON_QUEUE";
	private static String PMR_ALTERED = "PMR_ALTERED";
	private static Timer timer = new Timer();
	
	@Override
	public String getTableName() {
		return "NOTIFICATIONS";
	}
	
	public ArrayList<Obj> get(Map<String, String> attributes) throws Exception {		
		if (notifications == null)
			notifications = new HashMap<String, Obj>();		
		
		ArrayList<Obj> objs = new ArrayList<Obj>();
		
		for (String s : notifications.keySet()) {
			Obj n = notifications.get(s);
			if (n.getString("readby").indexOf(getSession().getUser().getRetainID()) < 0) {
				objs.add(n);	
			}
		}
		
		return objs;
	}
	
	public String ack(Map<String, String> attributes) {
		if (notifications == null)
			notifications = new HashMap<String, Obj>();
		
		for (String s : notifications.keySet()) {
			Obj n = notifications.get(s);
			n.setValue("readby", n.getString("readby") + "," + getSession().getUser().getRetainID());
		}
		
		return "ack";
	}
	
	public static void check(ArrayList<Obj> pmrs) throws Exception {		
		if (notifications == null)
			notifications = new HashMap<String, Obj>();		
		
		for (Obj pmr : pmrs) {			
			Obj n = notifications.get(pmr.getString("number"));
			
			if (pmr.getLong("daysaltered") <= 3 && (n == null || (n != null && n.getObj("pmr").getString("datealtered") != pmr.getString("datealtered")))) {
				addNotification(pmr, PMR_ALTERED);
			
			} else if (pmr.getLong("minutesonqueue") <= 3 && (n == null || (n != null && n.getObj("pmr").getString("queue") != pmr.getString("queue")))) {
				addNotification(pmr, NEW_PMR_ON_QUEUE);
			}
		}	
		
		timer.schedule(new java.util.TimerTask() {
			 @Override
			  public void run() {
				 try {
						for (String s : notifications.keySet()) {
							Obj n = notifications.get(s);
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(n.getLong("notificationdate"));
							if (Utils.getDiffDays(c, Calendar.getInstance()) >= 5) {
								notifications.remove(s);
							}
						}
				} catch (Exception e) {
				}
			  }
		}, 0, 5*24*60*60*1000);
	}
	
	
	public static void addNotification(Obj pmr, String type) {
		if (notifications == null)
			notifications = new HashMap<String, Obj>();	
		
		Obj obj = new Obj();
		obj.setValue("pmr", pmr);
		obj.setValue("type", type);
		obj.setValue("readby", "");
		obj.setValue("notificationdate", Calendar.getInstance().getTime().getTime());
		
		notifications.put(pmr.getString("number"), obj);
	}
	
	@Override
	public String create(Map<String, String> attributes) throws Exception {
		String json = attributes.get("json");

		if (json == null) {
			throw new Exception("Parameter json is missing.");
		}
		
		HashMap<String, String> tempAttributes = new HashMap<String, String>();
		tempAttributes.put("retainid", attributes.get("retainid"));
		super.delete(tempAttributes);
		
		return super.create(attributes);
	}
	
	@Override
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {
		return super.read(attributes);
	}	
}

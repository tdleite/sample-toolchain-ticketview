package com.ibm.retain.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Timer;

import javax.servlet.ServletRequest;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.retain.sdi.CallMgr;
import com.ibm.retain.sdi.CallMgrExt;
import com.ibm.retain.sdi.PmrMgr;
import com.ibm.retain.sdi.dataflags.CallData;
import com.ibm.retain.sdi.dataflags.CallFlags;
import com.ibm.retain.sdi.dataflags.CallId;
import com.ibm.retain.sdi.dataflags.PmrData;
import com.ibm.retain.sdi.dataflags.PmrFlags;
import com.ibm.retain.sdi.dataflags.PmrId;
import com.ibm.retain.sdi.dataflags.PmrTextElement;
import com.ibm.retain.sdi.dataflags.PmrTextLine;
import com.ibm.retain.sdi.dataflags.QueueId;
import com.ibm.retain.sdi.dataflags.RetainInfo;
import com.ibm.retain.sdi.dataflags.SoftHard;
import com.ibm.retain.session.Cache;
import com.ibm.retain.session.User;
import com.ibm.retain.utils.Action;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.Flags;
import com.ibm.retain.utils.Retain;
import com.ibm.retain.utils.Utils;

public class PMR extends Action {

	private static RetainInfo ri;
	private static Timer timer;
	
	@Override
	public String getTableName() {
		return "PMRS";
	}
	
	@Override
	public ArrayList<String> getAttributesList() throws ClassNotFoundException, SQLException {
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("pmr");
		attributes.add("queue");
		attributes.add("resolver5");
		
		return attributes;
	}

	@Override
	public String create(Map<String, String> attributes) throws Exception {
		return super.create(attributes);
	}
	
	@Override
	public ArrayList<Obj> select(Map<String, String> attributes) throws Exception {
		return super.select(attributes);
	}
	
	@Override
	public String update(Map<String, String> attributes) throws Exception {
		User user = getSession().getUser();
		ri = Retain.getRetainInfo(user);
		
		String pmr = attributes.get("pmr");
		String queue = attributes.get("queue");
		String resolver5 = attributes.get("resolver5");
		
		if (pmr == null) {
			throw new Exception("Parameter pmr is missing.");
		}
		
		Obj cachePMR = new Obj();
		if (Cache.active) {
			ArrayList<Obj> objs = Cache.get(new PMR().getTableName());
			for (Obj o : objs) {
				if (o.getString("number").equalsIgnoreCase(pmr)) {
					cachePMR = o;
					break;
				}
			}
		}
		
		StringTokenizer st;
		String pmrNumber = null;
		String pmrBranch = null;
		String pmrCountry = null;
		try {
			st = new StringTokenizer(pmr, ",");
			pmrNumber = st.nextToken();
			pmrBranch = st.nextToken();
			pmrCountry = st.nextToken();
			
		} catch (Exception e) {
			throw new Exception("Invalid format of PMR number.");
		}
		
		PmrData pd = new PmrData();
		pd.setPmrId(new PmrId(pmrNumber, pmrBranch, pmrCountry, "C", SoftHard.SOFTWARE));		
		pd.setSoftwareHardwareFlag('s');
		
		PmrMgr pm = new PmrMgr();
		
		PmrFlags pf = new PmrFlags();
		
		CallMgr cm = new CallMgr();
		
		if (queue != null) {
			PmrId pmrId = new PmrId(pmrNumber, pmrBranch, pmrCountry, "A", SoftHard.SOFTWARE);
			PmrMgr pmrMgr = new PmrMgr();
			PmrData pmrData;			
			
			try {
				pmrData = pmrMgr.browse(pmrId, "N", getSession().getUser().getTimeZone() + Retain.getTimezone(), new Flags().pmrFlags, ri);
			} catch (Exception e) {
				throw new Exception("PMR not found.");
			}
			
			String oldQueue = pmrData.getQueue();
			String oldCenter = pmrData.getCenter();
			
			String[][] queueCenterPair = new String[1][2];
			queueCenterPair[0][0] = oldQueue;
			queueCenterPair[0][1] = oldCenter;
	      
			CallFlags flags = new Flags().callflags;
			flags.PPGFlag = true;
			
			CallData cd[] = cm.search(queueCenterPair, flags, SoftHard.SOFTWARE, ri);
			for (int i=1; i < cd.length; i++) {
		    	if (pmr.equalsIgnoreCase(cd[i].getPmrId().getPmrNo() + "," + cd[i].getPmrId().getBranch() + "," + cd[i].getPmrId().getCountry())) {
		    		 
					byte[] ppg = cd[i].getCallId().getPPG();
		    		st = new StringTokenizer(queue, ",");
					String newQueue = st.nextToken();
					String newCenter = st.nextToken();
							
					CallFlags cf = new CallFlags();
					try {
						cm.dispatch(cd[i], cf, oldQueue, oldCenter, "CD", ppg, SoftHard.SOFTWARE, ri);
					} catch (Exception e) {
					}
		    		
					cd[i].setTargetQueue(newQueue);
					cd[i].setTargetCenter(newCenter);
					cf.InTargetCenterFlag = true;
					cf.InTargetQueueFlag = true; 
						
		   		  	cm.callRequeue(cd[i], cf, null, null, null, oldQueue, oldCenter, null, ppg, SoftHard.SOFTWARE, ri);
		   		  	
		   		  	cachePMR.setValue("queue", queue);
		   		  	cachePMR.setValue("text", null);
		   		  	
		   		  	break;		    		
		    	  }
			}			
		}

		if (resolver5 != null) {
			pd.setAdditionalResolver5Id(resolver5);
			pf.InAdditionalResolver5Flag = true;
			pm.updatePMR(pd, pf, ri);
			cachePMR.setValue("resolver5", resolver5);
			cachePMR.setValue("text", null);
		}
		
		return "Done.";		
	}
	
	@Override
	public String delete(Map<String, String> attributes) throws Exception {
		return super.delete(attributes);
	}
	
	@Override
	public ArrayList<Obj> read(Map<String, String> attributes) throws Exception {		
		User user = getSession().getUser();
		ri = Retain.getRetainInfo(user);

		if (Cache.active) {
			ArrayList<Obj> objs = Cache.get(getTableName());
			if (objs == null) {
				objs = searchPMRs();
				Cache.put(getTableName(), objs);
				if (timer == null) {
					timer = new Timer();
					timer.schedule(new java.util.TimerTask() {
						 @Override
						  public void run() {
							 try {
								ArrayList<Obj> objs = searchPMRs();
								Cache.put(getTableName(), objs);
							} catch (Exception e) {
							}
						  }
					}, 0, 5*60*1000);
				}				
			}

			String pmrNo = attributes.get("pmr");
			ArrayList<Obj> filteredObjs = new ArrayList<Obj>();
			if (pmrNo != null) {
				for (Obj o : objs) {
					if (o.getString("number").equalsIgnoreCase(pmrNo)) {
						if (o.getString("text") == null) {
							Obj pmr = searchPMR(pmrNo);
							o.setValue("text", pmr.getString("text"));
							o.setValue("privatenotes", pmr.getString("privatenotes"));
							o.setValue("ecurepdata", pmr.getString("ecurepdata"));
							o.setValue("apar", pmr.getString("apar"));
						}
						filteredObjs.add(o);
						return filteredObjs;
					}
				}
				filteredObjs.add(searchPMR(pmrNo));
				return filteredObjs;
			}
			return objs;

		} else {
			ArrayList<Obj> objs = new ArrayList<Obj>();
			String pmrNo = attributes.get("pmr");
			if (pmrNo != null) {
				objs.add(searchPMR(pmrNo));
				return objs;
			}
			objs = searchPMRs();
			return searchPMRs();
		}
	}

	public ArrayList<Obj> searchPMRs() throws Exception {
		ArrayList<Obj> objs = new ArrayList<Obj>();
		ArrayList<Obj> queues = new Queue().read(null);
		HashMap<String, ArrayList<Obj>> queuesByCenter = new HashMap<String, ArrayList<Obj>>();

		for (Obj queue : queues) {
			StringTokenizer st = new StringTokenizer(queue.getString("name"), ",");
			String queueName = st.nextToken();
			String queueCenter = st.nextToken();

			ArrayList<Obj> temp = queuesByCenter.get(queueCenter);
			if (temp == null) {
				temp = new ArrayList<Obj>();
			}
			temp.add(queue);
			queuesByCenter.put(queueCenter, temp);
		}

		for (String center : queuesByCenter.keySet()) {
			String[][] queueCenterPair = new String[queuesByCenter.get(center).size()][2];
			int count = 0;
			
			for (Obj queue : queuesByCenter.get(center)) {
				StringTokenizer st = new StringTokenizer(queue.getString("name"), ",");
				queueCenterPair[count][0] = st.nextToken();
				queueCenterPair[count][1] = st.nextToken();
				count++;
			}

			Flags f = new Flags();
			f.callflags.ErrorMessageFlag = true;
			ri.setReturnErrorMessage(true);

			CallMgr cm = new CallMgr();
			CallData cd[] = cm.search(queueCenterPair, f.callflags, SoftHard.SOFTWARE, ri);

			for (int i=1; i < cd.length; i++) {
				if (cd[i].callPrimarySecondary.equalsIgnoreCase("S"))
					continue;

				Obj obj = new Obj();
				obj.setValue("number", cd[i].pmrId.pmrNo + "," + cd[i].pmrId.branch + "," + cd[i].pmrId.country);
				obj.setValue("comment", cd[i].commentLine);
				obj.setValue("severity", "" + cd[i].severity);

				obj.setValue("age", getAge(cd[i].callCreateDate));
				obj.setValue("datecreated",getDateCreated(cd[i].callCreateDate));
				
				obj.setValue("daysaltered", getMinutesAltered(cd[i].alterDate));
				obj.setValue("datealtered",getAlterDate(cd[i].alterDate));

				obj.setValue("queue", cd[i].queue + "," + cd[i].center);
				obj.setValue("timeputonqueue", getTimePutOnQueue(cd[i].timePutOnQueueFormatted));
				obj.setValue("daysonqueue", getDaysOnQueue(cd[i].timePutOnQueueFormatted));
				obj.setValue("minutesonqueue", getMinutesOnQueue(cd[i].timePutOnQueueFormatted));

				obj.setValue("owner", cd[i].ownerEmployeeNumber.trim());
				obj.setValue("resolver", cd[i].resolverId.trim());
				obj.setValue("resolver5", cd[i].additionalResolver5ID.trim());
				obj.setValue("icn", cd[i].customerNo);
				obj.setValue("client", cd[i].customerName);
				
				if (cd[i].pmrId.pmrNo.toString().indexOf("18601") >= 0) {
					int x = 2;
				}
				
				String critsit = "" + cd[i].criticalSituation;
				if (!critsit.equalsIgnoreCase("Y"))
					critsit = "N";
				obj.setValue("critsit", critsit);
				
				String apar = cd[i].aparNumber;
				if (apar == null)
					apar = "";
				obj.setValue("apar", apar);
				
				objs.add(obj);
			 }
		}
		Notification.check(objs);
		return objs;
	}

	public Obj searchPMR(String pmr) throws Exception {
		String pmrNumber = null;
		String pmrBranch = null;
		String pmrCountry = null;

		try {
			StringTokenizer st = new StringTokenizer(pmr, ",");
			pmrNumber = st.nextToken();
			pmrBranch = st.nextToken();
			pmrCountry = st.nextToken();
			
		} catch (Exception e) {
			throw new Exception("Invalid format of PMR number.");
		}
		
		PmrId pmrId = new PmrId(pmrNumber, pmrBranch, pmrCountry, "A", SoftHard.SOFTWARE);
		PmrMgr pmrMgr = new PmrMgr();
		PmrData pmrData;
		
		try {
			pmrData = pmrMgr.browse(pmrId, "N", getSession().getUser().getTimeZone() + Retain.getTimezone(), new Flags().pmrFlags, ri);
		} catch (Exception e) {
			throw new Exception("PMR not found.");
		}		

		Obj obj = new Obj();
		obj.setValue("number", pmrData.pmrId.pmrNo + "," + pmrData.pmrId.branch + "," + pmrData.pmrId.country);

		JSONArray lines = new JSONArray();
		lines = formatRetainSignatureLines(pmrData.text);
		obj.setValue("text", lines.toString());
		
		try {
			PrivateNote p = new PrivateNote();
			p.setSession(getSession());
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put("pmr", pmr);
			ArrayList<Obj> privatenotes = p.read(attributes);
			JSONArray array = new JSONArray();
			for (Obj o : privatenotes) {
				array.add(o.toJSON());
			}
			obj.setValue("privatenotes", array.toString());
			
			EcuRepData e = new EcuRepData();
			e.setSession(getSession());
			ArrayList<Obj> ecurepdata = e.read(attributes);
			array = new JSONArray();
			for (Obj o : ecurepdata) {
				array.add(o.toJSON());
			}
			obj.setValue("ecurepdata", array.toString());
			
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			obj.setValue("privatenotes", array.toString());
			obj.setValue("privatenotes", array.toString());
		}
		
		
		String apar = pmrData.aparNumber;
		if (apar == null)
			apar = "";
		obj.setValue("apar", apar);
		
		return obj;
	}
	
	public JSONArray formatRetainSignatureLines(PmrTextLine[] textLines) {	
		JSONArray lines = new JSONArray();
		for (int i = 0; i < textLines.length; i++) {
			
			PmrTextElement textElement = textLines[i].getPmrTextElements()[0];
			
			JSONObject line = new JSONObject();
			
			byte attr = textElement.getAttr();
			String text = textElement.getText().replace("  ","").trim();
			String date = "";
			
			line.put("attr", attr);				
			line.put("text", text);
			line.put("formattedText", text);
						
			if (attr == 0) {
				lines.add(line);
				continue;
			}	
			
			String previousTag = "";
			String previousText = "";
			if (i > 0 && textLines[i-1].getPmrTextElements()[0].getAttr() != 0) {
				previousText = textLines[i-1].getPmrTextElements()[0].getText().replace("  ","").trim();
				if (previousText.length() > 0)
					previousTag = previousText.substring(previousText.length()-4, previousText.length());
			}
			
			String nextTag = "";
			String nextText = "";
			if (i+1 < textLines.length && textLines[i+1].getPmrTextElements()[0].getAttr() != 0) {
				nextText = textLines[i+1].getPmrTextElements()[0].getText().replace("  ","").trim();
				if (nextText.length() > 0)
					nextTag = nextText.substring(nextText.length()-4, nextText.length());
			}			
			
			String tag = "";
			if (text.length() > 0)
				tag = text.substring(text.length()-4, text.length());
			
			if (tag.equalsIgnoreCase("--CE")) {
				String who = text.substring(1, text.indexOf("-"));
				String l = text.substring(text.indexOf("-L")+2, text.indexOf("/"));
				String where = text.substring(text.indexOf("/")+1, text.indexOf("-", text.indexOf("/")+2)) + "," +  l;
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Call entry on " + where + " by " + who + " on " + when;
				date = when;
			
			} else if (tag.equalsIgnoreCase("--AL")) {
				String who;
				String when;
				if (text.startsWith("-")) {
					who = text.substring(1, text.indexOf("-", text.indexOf(" ")));
					when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				} else {
					who = text.substring(1, text.indexOf("-"));
					when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				}
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "PMR altered by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("--AT") && !previousTag.equalsIgnoreCase("--CR")) {
				String who = text.substring(1, text.indexOf("-"));
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Text added by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("--AT") && previousTag.equalsIgnoreCase("--CR")) {
				continue;
					
			} else if (tag.equalsIgnoreCase("--CT")) {
				String who = text.substring(1, text.indexOf("-"));
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Contact made by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("-SCG")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("-SCE")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("-SAT")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("--2D")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("-SCR")) {
				String who = text.substring(1, text.indexOf("-"));
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Text added by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("-SCC")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("--CR") && nextTag.equalsIgnoreCase("--AT")) {
				String who = text.substring(1, text.indexOf("-"));				
				String l = text.substring(text.indexOf("-L")+2, text.indexOf("/"));
				String from = text.substring(text.indexOf("/")+1, text.indexOf("-", text.indexOf("/")+2)) + "," +  l;				
				l = nextText.substring(nextText.indexOf("-L")+2, nextText.indexOf("/"));
				String to = nextText.substring(nextText.indexOf("/")+1, nextText.indexOf("-", nextText.indexOf("/")+2)) + "," +  l;								
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Call requeue from  " + from + " to " + to + " by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("--DA")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("-S2D")) {
				String who = text.substring(1, text.indexOf("-"));
				String l = text.substring(text.indexOf("-L")+2, text.indexOf("/"));
				String where = text.substring(text.indexOf("/")+1, text.indexOf("-", text.indexOf("/")+2)) + "," +  l;
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Call delete on " + where + " by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("/99/")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("/89/")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("/T9/")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("/W9/")) {
				continue;
				
			} else if (tag.equalsIgnoreCase("/29/")) {
				text = "Service given: [29] Non-defect solution provided";
				
			} else if (tag.equalsIgnoreCase("--CC")) {
				String who = text.substring(1, text.indexOf("-"));
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Contact complete by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("--CG")) {
				String who = text.substring(1, text.indexOf("-"));
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Call generated by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("--CR")) {
				String who;
				String l;
				String where;
				String when;		
				if (text.startsWith("-")) {
					who = text.substring(1, text.indexOf("-", text.indexOf(" ")));
					l = text.substring(text.indexOf("-L")+2, text.indexOf("/"));
					where = text.substring(text.indexOf("/")+1, text.indexOf("-", text.indexOf("/")+2)) + "," +  l;
					when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				} else {
					who = text.substring(1, text.indexOf("-"));
					l = text.substring(text.indexOf("-L")+2, text.indexOf("/"));
					where = text.substring(text.indexOf("/")+1, text.indexOf("-", text.indexOf("/")+2)) + "," +  l;
					when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				}
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "Text added by " + who + " on " + when;
				date = when;
				
			} else if (tag.equalsIgnoreCase("--SB")) {
				String apar = nextText.substring(nextText.indexOf("APAR - ")+7, nextText.indexOf("is closed")-1);
				String nextNextText = textLines[i+2].getPmrTextElements()[0].getText().replace("  ","").trim();
				String queue = nextNextText.substring(nextNextText.indexOf("queue ")+7, nextNextText.indexOf("."));
				String when = text.substring(text.length()-18,text.length()-4).replace("-", " ");
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(when.substring(0,2)));
				c.set(Calendar.MONTH, Integer.parseInt(when.substring(3,5))-1);
				c.set(Calendar.DATE, Integer.parseInt(when.substring(6,8)));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(9,11)));
				c.set(Calendar.MINUTE, Integer.parseInt(when.substring(12,14)));
				when = Utils.toString(c);
				text = "APAR " + apar + " subscribed on " + when + ". Call will generate upon closure on " + queue;
				i = i+3;
				date = when;
				
			}
								
			line.put("date", date);			
			line.put("formattedText", text);			
			lines.add(line);
		}
		
		return lines;	
	}
	
	public String getDateCreated(String date) {		
		try {
			int y = 2000 + Integer.parseInt(date.substring(0,2));
			int m = Integer.parseInt(date.substring(3,5)) - 1;
			int d = Integer.parseInt(date.substring(6,8));
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m);
			c.set(Calendar.DATE, d);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			
			return Utils.toString(c);
			
		} catch (Exception e) {
			return "";
		}		
	}
	
	public long getAge(String date) {
		try {
			int y = 2000 + Integer.parseInt(date.substring(0,2));
			int m = Integer.parseInt(date.substring(3,5)) - 1;
			int d = Integer.parseInt(date.substring(6,8));
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m);
			c.set(Calendar.DATE, d);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			
			Calendar now = Calendar.getInstance();
			
			return Utils.getDiffDays(c, now);
			
		} catch (Exception e) {
			return 0;
		}
	}

	public String getTimePutOnQueue(String date) {
		int y = Integer.parseInt(date.substring(0,4));
		int m = Integer.parseInt(date.substring(5,7)) - 1;
		int d = Integer.parseInt(date.substring(8,10));
		int h = Integer.parseInt(date.substring(11,13));
		int mi = Integer.parseInt(date.substring(14,16));
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m);
		c.set(Calendar.DATE, d);
		c.set(Calendar.HOUR_OF_DAY, h);
		c.set(Calendar.MINUTE, mi);
		
		return Utils.toString(c);
	}

	public long getDaysOnQueue(String date) {
		int y = Integer.parseInt(date.substring(0,4));
		int m = Integer.parseInt(date.substring(5,7)) - 1;
		int d = Integer.parseInt(date.substring(8,10));
		int h = Integer.parseInt(date.substring(11,13));
		int mi = Integer.parseInt(date.substring(14,16));
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m);
		c.set(Calendar.DATE, d);
		c.set(Calendar.HOUR_OF_DAY, h);
		c.set(Calendar.MINUTE, mi);
		
		Calendar now = Calendar.getInstance();
		
		return Utils.getDiffDays(c, now);
	}
	
	public long getMinutesOnQueue(String date) {
		int y = Integer.parseInt(date.substring(0,4));
		int m = Integer.parseInt(date.substring(5,7)) - 1;
		int d = Integer.parseInt(date.substring(8,10));
		int h = Integer.parseInt(date.substring(11,13));
		int mi = Integer.parseInt(date.substring(14,16));
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m);
		c.set(Calendar.DATE, d);
		c.set(Calendar.HOUR_OF_DAY, h);
		c.set(Calendar.MINUTE, mi);
		
		Calendar now = Calendar.getInstance();
		
		return Utils.getDiffMinutes(c, now);
	}
	
	public String getAlterDate(String date) {
		try {
			int y = 2000 + Integer.parseInt(date.substring(0,2));
			int m = Integer.parseInt(date.substring(3,5)) - 1;
			int d = Integer.parseInt(date.substring(6,8));
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m);
			c.set(Calendar.DATE, d);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			
			return Utils.toString(c);
			
		} catch (Exception e) {
			return "";
		}
	}

	public long getMinutesAltered(String date) {
		try {
			int y = 2000 + Integer.parseInt(date.substring(0,2));
			int m = Integer.parseInt(date.substring(3,5)) - 1;
			int d = Integer.parseInt(date.substring(6,8));
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m);
			c.set(Calendar.DATE, d);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			
			Calendar now = Calendar.getInstance();
			
			return Utils.getDiffMinutes(c, now);
			
		} catch (Exception e) {
			return 0;
		}
	}
}

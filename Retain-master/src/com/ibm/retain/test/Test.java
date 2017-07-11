package com.ibm.retain.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.io.File;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;

import com.ibm.json.java.JSONObject;
import com.ibm.retain.objects.PMR;
import com.ibm.retain.objects.PrivateNote;
import com.ibm.retain.objects.Queue;
import com.ibm.retain.sdi.CallMgr;
import com.ibm.retain.sdi.CallMgrExt;
import com.ibm.retain.sdi.PmrMgr;
import com.ibm.retain.sdi.QueueMgr;
import com.ibm.retain.sdi.RETAINSDIException;
import com.ibm.retain.sdi.dataflags.CallData;
import com.ibm.retain.sdi.dataflags.CallFlags;
import com.ibm.retain.sdi.dataflags.CallId;
import com.ibm.retain.sdi.dataflags.PmrData;
import com.ibm.retain.sdi.dataflags.PmrFlags;
import com.ibm.retain.sdi.dataflags.PmrId;
import com.ibm.retain.sdi.dataflags.PmrTextElement;
import com.ibm.retain.sdi.dataflags.QueueData;
import com.ibm.retain.sdi.dataflags.QueueId;
import com.ibm.retain.sdi.dataflags.RetainInfo;
import com.ibm.retain.sdi.dataflags.SoftHard;
import com.ibm.retain.sdi.helpers.ByteHelper;
import com.ibm.retain.sdi.test.CallQueueCenterSearchTest;
import com.ibm.retain.sdi.test.CenterBrowseListTest;
import com.ibm.retain.sdi.test.CenterBrowseTest;
import com.ibm.retain.sdi.test.QueuesListfromCenter;
import com.ibm.retain.session.Session;
import com.ibm.retain.session.SessionManager;
import com.ibm.retain.session.User;
import com.ibm.retain.utils.Obj;
import com.ibm.retain.utils.EcuRep;
import com.ibm.retain.utils.Flags;
import com.ibm.retain.utils.Retain;
import com.ibm.retain.utils.Utils;

import org.apache.commons.codec.binary.Base64;


public class Test {

	public static void main(String[] args) throws Exception {


//		User user = new User();
//		user.setRetainID("120548");
//		user.setPassword("");
//		user.setCountry("631");
//		RetainInfo ri = Retain.getRetainInfo(user);
//
//		StringTokenizer st = new StringTokenizer("75337,999,744", ",");
//		String pmrNumber = st.nextToken();
//		String pmrBranch = st.nextToken();
//		String pmrCountry = st.nextToken();
//		PmrId pmrId = new PmrId(pmrNumber, pmrBranch, pmrCountry, "A", SoftHard.SOFTWARE);
//
//		PmrFlags flags = new Flags().pmrFlags;
//		flags.TextFlag = true;
//
//		PmrMgr pmrMgr = new PmrMgr();
//		PmrData pmrData = pmrMgr.browse(pmrId, "N", -300, flags, ri);
//
//		for (int i = 0; i < pmrData.text.length; i++) {
//			PmrTextElement[] lines = pmrData.text[i].getPmrTextElements();
//			for (int j = 0; j < lines.length; j++) {
//				System.out.println(lines[j].getText());
//			}
//		}







//		User user = new User();
//		user.setRetainID("120548");
//		user.setPassword("");
//		user.setCountry("631");
//		RetainInfo ri = Retain.getRetainInfo(user);
//
//		ArrayList<Obj> queues = new Queue().read(new HashMap<String, String>());
//		HashMap<String, ArrayList<Obj>> queuesByCenter = new HashMap<String, ArrayList<Obj>>();
//
//		queues.clear();
//		Obj x = new Obj();
//		x.setValue("name", "INSLFE,12H");
//		queues.add(x);
//
//		for (Obj queue : queues) {
//			StringTokenizer st = new StringTokenizer(queue.getString("name"), ",");
//			String queueName = st.nextToken();
//			String queueCenter = st.nextToken();
//
//			ArrayList<Obj> temp = queuesByCenter.get(queueCenter);
//			if (temp == null) {
//				temp = new ArrayList<Obj>();
//			}
//			temp.add(queue);
//			queuesByCenter.put(queueCenter, temp);
//		}
//
//		for (String center : queuesByCenter.keySet()) {
//			String[][] queueCenterPair = new String[queuesByCenter.get(center).size()][2];
//			int count = 0;
//			for (Obj queue : queuesByCenter.get(center)) {
//				StringTokenizer st = new StringTokenizer(queue.getString("name"), ",");
//				queueCenterPair[count][0] = st.nextToken();
//				queueCenterPair[count][1] = st.nextToken();
//				count++;
//			}
//			
//			queueCenterPair =  new String[1][2];
//			queueCenterPair[0][0] = "CMISL3";
//			queueCenterPair[0][1] = "13K";
//					
//			Flags f = new Flags();
//			f.callflags.ErrorMessageFlag = true;
//			ri.setReturnErrorMessage(true);
//
//			 CallMgr cm = new CallMgr();
//			 CallData cd[] = cm.search(queueCenterPair, f.callflags, SoftHard.SOFTWARE, ri);
//
//			 ArrayList<Obj> pmrs = new ArrayList<Obj>();
//			 for (int i=1; i < cd.length; i++) {
//				System.out.println("Number: " + cd[i].pmrId.pmrNo + "," + cd[i].pmrId.branch + "," + cd[i].pmrId.country);
//				System.out.println("Created Date: " + cd[i].callCreateDate);
//				System.out.println("Created Date Formatted: " + new PMR().getDateCreated(cd[i].callCreateDate));
//				System.out.println("Age: " + new PMR().getAge(cd[i].callCreateDate));
//				System.out.println("Time Put On Queue: " + cd[i].timePutOnQueueFormatted);
//				System.out.println("Time Put On Queue Formatted " + new PMR().getTimePutOnQueue(cd[i].timePutOnQueueFormatted));
//				System.out.println("Time On Queue: " + new PMR().getTimeOnQueue(cd[i].timePutOnQueueFormatted));
//				
//				System.out.println("Updated on: " + cd[i].alterDate);
//				System.out.println("Updated Formatted on: " + new PMR().getAlterDate(cd[i].alterDate));
//				System.out.println("Time Updated: " + new PMR().getTimeAltered(cd[i].alterDate));
//				
//				
//				System.out.println("_______________________________________________");
//			 }
//		}

		
		
		
		
		
		
		
		
		
		


//		ArrayList<BObj> objs = new ArrayList<BObj>();
//		BObj o = new BObj();
//		o.setValue("name", "MTFEWK,12H");
//		objs.add(o);		
//		
//		String[][] queueCenterPair = new String[objs.size()][2];
//		for (int i=0; i < objs.size(); i++) {
//			java.util.StringTokenizer st = new java.util.StringTokenizer(objs.get(i).getString("name"), ",");
//			queueCenterPair[i][0] = st.nextToken();
//			queueCenterPair[i][1] = st.nextToken();
//		}
//		
//		Flags f = new Flags();
//		f.callflags.ErrorMessageFlag = true;
//		f.callflags.FollowupInfoFlag = true;
//		f.callflags.InPrimaryFupCenterFlag = true;
//		f.callflags.InPrimaryFupQueueFlag = true;
//		f.callflags.PersonalizedFupFlag = true;
//		f.callflags.PrimaryFupCenterFlag = true;
//		f.callflags.PrimaryFupQueueFlag = true;
//		f.callflags.FollowupInfoFlag = true;
//		
//		CallMgr cm = new CallMgr();
//		CallData cd[] = cm.search(queueCenterPair, f.callflags, SoftHard.SOFTWARE, new RetainInfo("120548", "", "retwb", "retwb", "631", RetainInfo.RS4_PROD1));
//		for (int i = 0; i < cd.length; i++) {
//			if (cd[i].pmrId != null)
//				System.out.println(cd[i].pmrId.pmrNo + "," + cd[i].pmrId.branch + "," + cd[i].pmrId.country + " - " + cd[i].commentLine);
//		}
		
		
		
		
		
		
		
		
//		QueueId qi = new QueueId();
//		qi.setQueue("MMWK10");
//		qi.setCenter("12H");
//		qi.setSwHd("s");
//		
//		CallFlags cf = new Flags().callflags;
//		cf.FollowupInfoFlag = true;
//		
//		User user = new User();
//		user.setRetainID("120548");
//		user.setPassword("");
//		user.setCountry("631");
//		RetainInfo ri = Retain.getRetainInfo(user);
//		
//		CallMgr cm = new CallMgr();
//		CallData cd[] = cm.search(qi, cf, ri);
//		for (int i = 0; i < cd.length; i++) {
//		if (cd[i].pmrId != null)
//			System.out.println(cd[i].pmrId.pmrNo + "," + cd[i].pmrId.branch + "," + cd[i].pmrId.country + " - " + cd[i].commentLine);
//		}
		
		



//		String[] s = new String [7];
//		s[0] = "120548";
//		s[1] = "";
//		s[2] = "retwb";
//		s[3] = "retwb";
//		s[4] = "13K";
//		s[5] = "s";
//		s[6] = "RS4";
//		QueuesListfromCenter.main(s);

//		String[] s = new String [5];
//		s[0] = "120548";
//		s[1] = "";
//		s[2] = "retwb";
//		s[3] = "retwb";
//		s[4] = "RS4";
//		CenterBrowseListTest.main(s);
//		CenterBrowseTest.main(s);

//		String[] s = new String [6];
//		s[0] = "120548";
//		s[1] = "";
//		s[2] = "retwb";
//		s[3] = "retwb";
//		s[4] = "s";
//		s[5] = "RS4";
//		CallQueueCenterSearchTest.main(s);






		// get all queues from a center
		//QueueMgr q = new QueueMgr();
		//QueueData queues = q.retrieveQueuesFromCenter("13K", "S", new RetainInfo("", "", "retwb", "retwb", "631", RetainInfo.RS4_PROD1));
		//int a = 1;
		
		
		
	
	
		
		
		
		
		
		
		
		
		
		
		
//      RetainInfo ri = new RetainInfo();
//      ri.setID("120548");
//      ri.setPW("");
//      ri.setBill("retwb");
//      ri.setExtBill("retwb");
//      ri.setCountry("000");
//      ri.setNode(21);
//      ri.setSocketTimeout(30000);
//      ri.setReturnErrorMessage(true);   
//               
//      PmrFlags pf = new PmrFlags();
//      //pf.InSeverityFlag = true;
//      //pf.InAdditionalResolver5Flag = true;
//      //pf.InOwnerEmployeeNumberFlag = true;
//      //pf.InResolverIdFlag = true;
//      pf.InQueueFlag = true;
//      pf.InCenterFlag = true;
//      pf.UpdateCallDataFlag = true;
//      
//      PmrData pd = new PmrData();
//      pd.setPmrId(new PmrId("18601","999","000", "C", SoftHard.SOFTWARE));    
//      //pd.setSeverity('2');
//      //pd.setAdditionalResolver5Id("120548");
//      //pd.setOwnerEmployeeNumber("120548");
//      //pd.setResolverId("120548");
//      pd.setQueue("TRISL3");
//      pd.setCenter("13K");
//     CallId callID = new CallId(new QueueId("TRISL3", "13K", SoftHard.SOFTWARE));
//      byte[] myPPG = new byte[2];
//      myPPG[0] = (byte)0x03;     
//      myPPG[1] = (byte)0x03;
//      callID.setPPG(myPPG);
//      callID.setQueue("TRISL3");
//      callID.setCenter("13K");
//      callID.setSwHd(SoftHard.SOFTWARE);
//      pd.setCallId(callID);
//      
//      pd.setSoftwareHardwareFlag('s');
//      
//      PmrMgr pm = new PmrMgr();
//      pm.updatePMR(pd,pf,ri);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//      RetainInfo ri = new RetainInfo();
//      ri.setID("120548");
//      ri.setPW("");
//      ri.setBill("retwb");
//      ri.setExtBill("retwb");
//      ri.setCountry("631");
//      ri.setNode(21);
//      ri.setSocketTimeout(30000);
//      ri.setReturnErrorMessage(true); 
//		
//      CallFlags flags = new Flags().callflags;
//      flags.PPGFlag = true;
//      
//      String[][] queueCenterPair = new String[1][2];
//      queueCenterPair[0][0] = "TRISL3";
//      queueCenterPair[0][1] = "13K";
//      
//      CallMgr cm = new CallMgr();
//      CallData cd[] = cm.search(queueCenterPair, flags, SoftHard.SOFTWARE, ri);
      
//	
//      for (int i=1; i < cd.length; i++) {
//    	  String pmr = cd[i].getPmrId().getPmrNo() + "," + cd[i].getPmrId().getBranch() + "," + cd[i].getPmrId().getCountry();
//    	  
//    	  if (pmr.equalsIgnoreCase("18601,999,000")) {
//    		  byte[] ppg = cd[i].getCallId().getPPG();
//    		  String queue = cd[i].getQueue();
//    		  String center = cd[i].getCenter();
//    		  String newQueue = "TRISL3";
//    		  String newCenter = "13K";
//    		  
//    		  System.out.println("PMR " + pmr);
//    		  
//    		  System.out.println("Dispatching...");
//    		  
//    		  CallFlags cf=new CallFlags();
//    		  
//    		  try {
//    			  cm.dispatch(cd[i], cf, queue, center, "CD", ppg, SoftHard.SOFTWARE, ri);
//			} catch (Exception e) {
//
//			}
//    		  
//      		  cd[i].setTargetQueue(newQueue);
//      		  cd[i].setTargetCenter(newCenter);
//      		  cf.InTargetCenterFlag = true;
//      		  cf.InTargetQueueFlag = true; 
//      		  
//    		  System.out.println("queue = " + queue);
//    		  System.out.println("center = " + center);
//    		  
//    		  System.out.println("new queue = " + newQueue);
//    		  System.out.println("new center = " + newCenter);
//    		  
//    		  System.out.println("Requeuing...");
//    		  
//    		  CallMgrExt cme = new CallMgrExt();
//    		  cm.callRequeue(cd[i], cf, null, null, null, queue, center, null, ppg, SoftHard.SOFTWARE, ri);
//    	  }
//      }

      
      
   	
		
		
		
		
		
		
//		System.out.println("starting...");
//		
//		URIBuilder builder = new URIBuilder();
//		builder.setScheme("https").
//			setHost("ecurep.mainz.de.ibm.com").
//			setPort(443).
//			setPath("/rest/1/ticket/10368,004,000/notes/id");
//			
//		java.net.URI uri = builder.build();
//		
//		HttpGet post = new HttpGet(uri);
//		
//		String username = "gribeiro@br.ibm.com";
//		String password = "";		  
//        String authentication = new Base64().encodeAsString(new String(username + ":" + password).getBytes());
//				
//		post.setHeader("Authorization", "Basic " + authentication);
//		
//		HttpClient httpclient = new DefaultHttpClient();
//		
//		HttpResponse response = httpclient.execute(post);
//		
//		HttpEntity entity = response.getEntity();   
//		
//		new BufferedReader(new InputStreamReader(entity.getContent()))
//		  .lines().collect(java.util.stream.Collectors.joining("\n"));
//		
//		System.out.println("status = " + response.getStatusLine().getStatusCode());
		
	
		
		
		
		
		
		
		
		
		
		
		
		// 118882
		// mx01l3is
		
		
//		User user = new User();
//		user.setRetainID("120548");
//		user.setPassword("");
//		user.setCountry("631");
//		RetainInfo ri = Retain.getRetainInfo(user);
//
//		String[][] queueCenterPair =  new String[1][2];
//		queueCenterPair[0][0] = "TRISL3";
//		queueCenterPair[0][1] = "13K";
//					
//		Flags f = new Flags();
//		f.callflags.ErrorMessageFlag = true;
//		ri.setReturnErrorMessage(true);
//
//		 CallMgr cm = new CallMgr();
//		 QueueId qi = new QueueId();
//		 qi.setQueue("TRISL3");
//		 qi.setCenter("13K");
//		 qi.setSwHd(SoftHard.SOFTWARE);
// 
//		 CallData cd[] = cm.search(qi, f.callflags, ri);
//
//		 ArrayList<Obj> pmrs = new ArrayList<Obj>();
//		 for (int i=1; i < cd.length; i++) {
//			System.out.println("Number: " + cd[i].pmrId.pmrNo + "," + cd[i].pmrId.branch + "," + cd[i].pmrId.country);
//		 }

		
		
		
		
		
		
		
		
		
		
		
//		int a = -180;
//		int b = 0;
//		
//		User user = new User();
//		user.setRetainID("120548");
//		user.setRetainPassword("");
//		user.setCountry("000");
//		RetainInfo ri = Retain.getRetainInfo(user);
//		PmrId pmrId = new PmrId("13209", "047", "649", "A", SoftHard.SOFTWARE);
//		PmrData pmrData;
//		PmrMgr pmrMgr = new PmrMgr();
//		pmrData = pmrMgr.browse(pmrId, "N", a + b, new Flags().pmrFlags, ri);
//		
//		for (int i = 0; i < pmrData.text.length; i++) {
//			System.out.println(pmrData.text[i].getPmrTextElements()[0].getText());
//		}
		
		
		
		

//		User user = new User();
//		user.setIntranetID("gribeiro@br.ibm.com");
//		user.setIntranetPassword("");
//		user.setCountry("000");
//		
//		Session s = new Session();
//		s.setUser(user);
//		
//		PrivateNote p = new PrivateNote();
//		p.setSession(s);
//		
//		HashMap<String, String> attributes = new HashMap<String, String>();
//		attributes.put("pmr", "10368,004,000");
//		
//		ArrayList<Obj> objs =  p.read(attributes);
//		
//		for (Obj o : objs) {
//			System.out.println(o.toJSON());
//		}
		
		
		
		
		
		
		
//		URIBuilder builder = new URIBuilder();
//		builder.setScheme("https").
//			setHost(EcuRep.getURL()).
//			setPort(EcuRep.getPort()).
//			setPath("/rest/1/ticket/P-USR3SBLC2D9/notes");
//				
//		java.net.URI uri = builder.build();
//			
//		HttpPost post = new HttpPost(uri);
//			
//		String username = "gribeiro@br.ibm.com";
//		String password = "";		  
//	    String authentication = new Base64().encodeAsString(new String(username + ":" + password).getBytes());
//					
//		post.setHeader("Authorization", "Basic " + authentication);
//		
//		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//		params.add(new BasicNameValuePair("body", "gabriel teste 1"));
//		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//		
//		HttpClient httpclient = HttpClientBuilder.create().build();
//			
//		HttpResponse response = httpclient.execute(post);	
//		
//		System.out.println(response.getStatusLine().getStatusCode());
		
		
		
		

		
//		User user = new User();
//		user.setRetainID("120548");
//		user.setRetainPassword("");
//		user.setCountry("000");
//		
//		Session s = new Session();
//		s.setUser(user);
//		
//		PMR p = new PMR();
//		p.setSession(s);
//		
//		HashMap<String, String> a = new HashMap<String, String>();
//		a.put("pmr", "18601,999,000");
//		a.put("queue", "TRISL3,13K");
//		p.update(a);
		
		
		
		
		
		
		
		
	
	
	}
	

	

}
	


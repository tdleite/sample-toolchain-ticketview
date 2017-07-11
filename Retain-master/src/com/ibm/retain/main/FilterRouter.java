package com.ibm.retain.main;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.ibm.json.java.JSONObject;
import com.ibm.retain.objects.Client;
import com.ibm.retain.objects.EcuRepData;
import com.ibm.retain.objects.Engineer;
import com.ibm.retain.objects.Notification;
import com.ibm.retain.objects.View;
import com.ibm.retain.objects.PMR;
import com.ibm.retain.objects.PMRStatus;
import com.ibm.retain.objects.PrivateNote;
import com.ibm.retain.objects.Queue;
import com.ibm.retain.session.Cache;
import com.ibm.retain.session.SessionManager;
import com.ibm.retain.utils.Utils;

@WebFilter("/*")
public class FilterRouter implements Filter {

    public FilterRouter() {
    }

	public void init(FilterConfig fConfig) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (SessionManager.getSessions() == null)
			SessionManager.setSessions();

		String uri = ((HttpServletRequest) request).getRequestURI();
		String[] parts = uri.split("/");

		if (uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".gif")) {
			chain.doFilter(request, response);
			return;

		} else if (parts.length == 2) {
			if (parts[1].equalsIgnoreCase("login")) {
				SessionManager.login(request, response);
				return;

			} else 	if (parts[1].equalsIgnoreCase("checksession")) {
				SessionManager.checkSession(request, response);
				return;
			}

		} else if (parts.length == 0) {
			Utils.getWebApp(request, response);
			return;

		} else if (!SessionManager.authenticate(request, response)) {
			return;

		} else if (parts.length == 2) {
			if (parts[1].equalsIgnoreCase("logout")) {
				SessionManager.logout(request, response);
				return;
			}

		} else if (parts.length == 3) {
			if (parts[1].equalsIgnoreCase("cache") && parts[2].equalsIgnoreCase("clear")) {
				Cache.clear();
				JSONObject json = new JSONObject();
				json.put("message", "Cache cleared");
				Utils.sendResponseOK(request, response, "application/json", json.toString());
				return;

			} else if (parts[1].equalsIgnoreCase("view") && parts[2].equalsIgnoreCase("create")) {
				new View().doIt(request, response,  "create");
				return;

			} else if (parts[1].equalsIgnoreCase("view") && parts[2].equalsIgnoreCase("read")) {
				new View().doIt(request, response,  "read");
				return;
				
			} else if (parts[1].equalsIgnoreCase("view") && parts[2].equalsIgnoreCase("update")) {
				new View().doIt(request, response,  "update");
				return;
				
			} else if (parts[1].equalsIgnoreCase("view") && parts[2].equalsIgnoreCase("delete")) {
				new View().doIt(request, response,  "delete");
				return;

			} else if (parts[1].equalsIgnoreCase("pmr") && parts[2].equalsIgnoreCase("read")) {
				new PMR().doIt(request, response,  "read");
				return;
				
			} else if (parts[1].equalsIgnoreCase("pmr") && parts[2].equalsIgnoreCase("update")) {
				new PMR().doIt(request, response,  "update");
				return;

			} else if (parts[1].equalsIgnoreCase("queue") && parts[2].equalsIgnoreCase("read")) {
				new Queue().doIt(request, response, "read");
				return;

			} else if (parts[1].equalsIgnoreCase("engineer") && parts[2].equalsIgnoreCase("read")) {
				new Engineer().doIt(request, response, "read");
				return;

			} else if (parts[1].equalsIgnoreCase("client") && parts[2].equalsIgnoreCase("read")) {
				new Client().doIt(request, response, "read");
				return;

			} else if (parts[1].equalsIgnoreCase("pmrstatus") && parts[2].equalsIgnoreCase("create")) {
				new PMRStatus().doIt(request, response, "create");
				return;

			} else if (parts[1].equalsIgnoreCase("pmrstatus") && parts[2].equalsIgnoreCase("read")) {
				new PMRStatus().doIt(request, response, "read");
				return;
				
			} else if (parts[1].equalsIgnoreCase("notification") && parts[2].equalsIgnoreCase("create")) {
				new Notification().doIt(request, response, "create");
				return;
			
			} else if (parts[1].equalsIgnoreCase("notification") && parts[2].equalsIgnoreCase("read")) {
				new Notification().doIt(request, response, "read");
				return;
			
			} else if (parts[1].equalsIgnoreCase("notification") && parts[2].equalsIgnoreCase("get")) {
				new Notification().doIt(request, response, "get");
				return;
			
			} else if (parts[1].equalsIgnoreCase("notification") && parts[2].equalsIgnoreCase("ack")) {
				new Notification().doIt(request, response, "ack");
				return;
				
			} else if (parts[1].equalsIgnoreCase("privatenote") && parts[2].equalsIgnoreCase("read")) {
				new PrivateNote().doIt(request, response, "read");
				return;
				
			} else if (parts[1].equalsIgnoreCase("privatenote") && parts[2].equalsIgnoreCase("create")) {
				new PrivateNote().doIt(request, response, "create");
				return;
			
			} else if (parts[1].equalsIgnoreCase("ecurepdata") && parts[2].equalsIgnoreCase("read")) {
				new EcuRepData().doIt(request, response, "read");
				return;
			
			}
		}

		chain.doFilter(request, response);
	}

}

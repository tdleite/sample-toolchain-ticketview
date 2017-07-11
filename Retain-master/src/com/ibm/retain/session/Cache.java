package com.ibm.retain.session;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.retain.utils.Obj;

public class Cache {

	public static boolean active = true;
	private static final ConcurrentHashMap<String, ArrayList<Obj>> data = new ConcurrentHashMap<String, ArrayList<Obj>>();

	public static void add(String name, Obj obj) {
		if (!active)
			return;
		ArrayList<Obj> objs = get(name);
		if (objs == null) {
			objs = new ArrayList<Obj>();
		}
		objs.add(obj);
		put(name.toLowerCase(), objs);
	}

	public static void put(String name, ArrayList<Obj> objs) {
		if (!active)
			return;
		data.put(name.toLowerCase(), objs);
	}

	public static ArrayList<Obj> get(String name) {
		if (!active)
			return null;
		return data.get(name.toLowerCase());
	}

	public static void clear() {
		if (!active)
			return;
		data.clear();
	}

	public static void clear(String name) {
		if (!active)
			return;
		data.remove(name.toLowerCase());
	}

}

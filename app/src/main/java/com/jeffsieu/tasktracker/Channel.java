package com.jeffsieu.tasktracker;

import java.util.ArrayList;

/**
 * Created by user on 10/7/2016.
 */
public class Channel {
	private String name;
	private String code;
	private ArrayList<String> admins;

	public Channel(String name, String code, String uid) {
		this.name = name;
		this.code = code;
		admins = new ArrayList<>();
		admins.add(uid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}

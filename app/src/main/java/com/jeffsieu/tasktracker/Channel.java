package com.jeffsieu.tasktracker;

import java.util.ArrayList;

/**
 * Created by user on 10/7/2016.
 */
public class Channel {
    private String name;
    private String key;
    private ArrayList<String> admins;

    public Channel(String name) {
        this.name = name;
        this.key = "";
        this.admins = new ArrayList<>();
    }

    public Channel(String name, String key) {
        this.name = name;
        this.key = key;
        this.admins = new ArrayList<>();
    }

    public Channel(String name, String key, String uid) {
        this(name, key);
        admins = new ArrayList<>();
        addAdmin(uid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getAdmins() {
        return admins;
    }

    public void setAdmins(ArrayList<String> admins) {
        this.admins = admins;
    }

    public void addAdmin(String uid) {
        this.admins.add(uid);
    }

    public void setChannel(Channel channel) {
        channel.name = name;
        channel.key = key;
        channel.admins = admins;
    }
}

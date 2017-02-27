package com.example.pbkou.smarthouse;

import java.util.UUID;

/**
 * Created by Alexiah on 24/02/2017.
 */

public class UserGroup {

    private String ugID = UUID.randomUUID().toString();
    private String user;
    private String group;

    public UserGroup(){
        this.user="";
        this.group="";
    }

    public UserGroup(String name, String group){
        this.user = name;
        this.group = group;
    }

    /* Getters  - Setters*/

    public String getId() {
        return ugID;
    }

    public void setId(String id) {
        this.ugID = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


}

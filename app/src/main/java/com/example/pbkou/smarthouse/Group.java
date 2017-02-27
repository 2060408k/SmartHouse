package com.example.pbkou.smarthouse;

import java.util.UUID;

/**
 * Created by Alexiah on 24/02/2017.
 */

public class Group {



    private String groupID= UUID.randomUUID().toString();
    private String name;
    private String createDate;

    public Group(){
        this.name="";
        this.createDate="";
    }

    public Group(String name, String createDate){
        this.name = name;
        this.createDate = createDate;
    }

    /* Getters  - Setters*/

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }


}

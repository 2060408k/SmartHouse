package com.example.pbkou.smarthouse;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by Alexiah on 24/02/2017.
 */

public class Message {

    private String id= UUID.randomUUID().toString();
    private String creator;
    private String body;
    private String createDate;
    private String parent;

    public Message(){
        this.creator="";
        this.body="";
        this.createDate= "";
        this.parent="";

    }

    public Message(String creator, String body, String date, String parent){
        this.creator=creator;
        this.body=body;
        this.createDate= date;
        this.parent=parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}

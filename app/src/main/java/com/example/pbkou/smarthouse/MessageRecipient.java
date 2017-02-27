package com.example.pbkou.smarthouse;

import java.util.UUID;

/**
 * Created by Alexiah on 24/02/2017.
 */

public class MessageRecipient {

    private String id= UUID.randomUUID().toString();
    private String group;
    private String message;

    public MessageRecipient(){
        this.group="";
        this.message="";
    }

    public MessageRecipient(String group, String message){
        this.group = group;
        this.message = message;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

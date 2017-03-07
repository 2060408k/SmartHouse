package com.example.pbkou.smarthouse;

import java.util.UUID;

/**
 * Created by Alexiah on 24/02/2017.
 */

public class Task {



    private String taskId= UUID.randomUUID().toString();


    private String user;
    private String date;
    private String body;

    public Task(){
        this.user="";
        this.date="";
        this.body="";
    }

    public Task(String user, String date, String body){
        this.user = user;
        this.date = date;
        this.body = body;
    }

    /* Getters  - Setters*/

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



}

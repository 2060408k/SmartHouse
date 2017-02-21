package com.example.pbkou.smarthouse;

import java.util.UUID;

/**
 * Created by pbkou on 21/02/2017.
 */

public class Beacon {

    private String id= UUID.randomUUID().toString();
    private String name;
    private String address;
    private String area;

    public Beacon(){
        this.name="";
        this.address="";
    }

    public Beacon(String name, String address, String area){
        this.name = name;
        this.address = address;
        this.area = area;
    }



    // Getters-Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {return area;}

    public void setArea(String area) {this.area = area;}

}

package com.univpm.homestuff.entities;

import java.util.Date;

public class Domesticas {
    private String name;
    private String uid;
    private Date day;

    public Domesticas(String name, String uid, Date day) {
        this.name = name;
        this.uid = uid;
        this.day = day;
    }

    public Domesticas() {
    }

    public String getName() {  return this.name; }
    public void setName(String val) {
        this.name = val;
    }

    public String getUid() { return this.uid; }
    public void setUid(String val) {
        this.uid = val;
    }

    public Date getDay() {  return this.day; }
    public void setDay(Date val) { this.day = val; }
}


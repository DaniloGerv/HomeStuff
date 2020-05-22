package com.univpm.homestuff.entities;

import java.util.Date;

public class Domesticas {
    private String name;
    private String user;
    private String familyID;
    private String domesticaID;

    public Domesticas(String name, String user) {
        this.name = name;
        this.user = user;
    }

    public Domesticas() {

    }

    public String getName() {  return this.name; }
    public void setName(String val) {
        this.name = val;
    }

    public String getUser() { return this.user; }
    public void setUser(String val) {
        this.user = val;
    }

    public String getDomesticaID() {  return this.domesticaID; }
    public void setDomesticaID(String val) {
        this.domesticaID = val;
    }

    public String getFamilyID() {  return this.familyID; }
    public void setFamilyID(String val) { this.familyID = val; }


}


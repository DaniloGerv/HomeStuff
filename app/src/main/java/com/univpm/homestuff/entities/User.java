package com.univpm.homestuff.entities;

import java.util.Set;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String city;
    private String address;
    private String CAP;
    private String uid;

    public User(String firstName,String lastName,String email,String password){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password=password;
    }

    public User(String email)
    {
        this.email=email;
    }

    public User()
    {

    }

    public String getFirstName(){
        return this.firstName;
    }
    public void setFirstName(String val)
    {
        this.firstName=val;
    }

    public String getLastName(){return this.lastName; }
    public void setLastName(String val)
    {
        this.lastName=val;
    }

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String val)
    {
        this.email=val;
    }

    public String getPassword(){
        return this.password;
    }
    public void setPassword(String val)
    {
        this.password=val;
    }

    public String getUID(){
        return this.uid;
    }
    public void setUID(String val)
    {
        this.uid=val;
    }

    public void setCity(String val)
    {
        this.city=val;
    }

    public void setAddress(String val)
    {
        this.address=val;
    }

    public void setCAP(String val){ this.CAP=val; }



}

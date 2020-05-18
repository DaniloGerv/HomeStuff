package com.univpm.homestuff.entities;



public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String uid;
    private Location place;
    private String photoURL;
    private String familyId;

    public User(String firstName,String lastName,String email,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

    }

    public User(String email)
    {
        this.email=email;
    }

    public User(String firstName,String lastName,String email,String password,String familyId,double latitude,double longitude,String address,String city,String state,String country,String postalCode,String name)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password=password;
        this.familyId=familyId;
        this.place=new Location(latitude,longitude,address,city,state,country,postalCode,name);
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
    public void setPassword(String val){ this.password=val; }

    public String getUID(){
        return this.uid;
    }
    public void setUID(String val)
    {
        this.uid=val;
    }

    public String getFamilyID(){
        return this.familyId;
    }
    public void setFamilyID(String val)
    {
        this.familyId=val;
    }

    public Location getPlace() {return this.place;}
    public void setPlace(Location val){this.place=val;}

    public String getPhotoURL(){
        return this.photoURL;
    }
    public void setPhotoURL(String val)
    {
        this.photoURL=val;
    }





}

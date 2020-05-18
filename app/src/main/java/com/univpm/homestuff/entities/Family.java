package com.univpm.homestuff.entities;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Family {

    private String fid;
    private String familyName;
    private ArrayList<String> membersID;
    private Location place;
    private ArrayList<Domesticas> toDoList;

    public Family(String familyName,String fid,double latitude,double longitude,String address,String city,String state,String country,String postalCode,String name)
    {
            this.familyName=familyName;
             this.place=new Location(latitude,longitude,address,city,state,country,postalCode,name);
            this.fid=fid;
            this.membersID=new ArrayList<String>();
            this.toDoList=new ArrayList<Domesticas>();
    }

    public Family() {}

    public void addMember(String u)
    {
        if (membersID==null)
            membersID=new ArrayList<String>();
        membersID.add(u);
    }

    public void removeMember(String u)
    {
        if (membersID!=null)
        {
            for (int i=0;i<membersID.size();i++)
                if (membersID.get(i).equals(u))
                    membersID.remove(i);
        }
    }

    public ArrayList<String> getMembers() { return this.membersID;}
    public void setMembers(ArrayList<String> value) {this.membersID=value;}

    public String getFamilyName() {return this.familyName;}
    public void setFamilyName(String value) {this.familyName=value;}

    public Location getPlace() {return this.place;}
    public void setPlace(Location val){this.place=val;}

    public String getFID() {return this.fid;}
    public void setFID(String value) {this.fid=value;}

    public ArrayList<Domesticas> getToDoList() {return this.toDoList;}
    public void setToDoList (ArrayList<Domesticas> val) {this.toDoList=val;}

    public void addDomesticas(Domesticas d )
    {
        this.toDoList.add(d);
    }

    public void removeDomestica(Domesticas d)
    {
        this.toDoList.remove(d);
    }

    public void reset()
    {
        if (toDoList!=null)
        this.toDoList.clear();
        this.familyName="";
        if (membersID!=null)
        this.membersID.clear();
    }



}

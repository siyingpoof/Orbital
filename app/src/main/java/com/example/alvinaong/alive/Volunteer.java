package com.example.alvinaong.alive;

import java.util.ArrayList;

public class Volunteer {

    private String name;
    private String gender;
    private String school;
    private String contactNumber;
    private String email;
    private int hours;
    private String volID;

    public Volunteer() {
        this.name = "";
        this.gender = "";
        this.school = "";
        this.contactNumber = "";
        this.email = "";
        this.hours = 0;
        volID = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHours() {
        return this.hours;
    }

    public void addHours(int hoursToAdd) {
        this.hours += hoursToAdd;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getVolID() {
        return volID;
    }

    public void setVolID(String volID) {
        this.volID = volID;
    }
}

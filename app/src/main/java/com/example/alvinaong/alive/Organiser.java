package com.example.alvinaong.alive;

public class Organiser {

    private String name;
    private String mobile;
    private String description;
    private String orgID;
    private String email;

    public Organiser() {
        this.name = "";
        this.mobile = "";
        this.description = "";
        this.email = "";
        orgID = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setContact(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
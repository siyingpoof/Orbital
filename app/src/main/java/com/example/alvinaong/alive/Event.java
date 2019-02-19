package com.example.alvinaong.alive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    private String name, description, time, venue, organiser, date, eventID;
//    private Date date;
    private int quota, hours;
    private ArrayList<String> signUpsList, attendanceList;
    private boolean eventStatus;

    public Event() {
        name = "";
        description = "";
        date = "";
        time = "";
        venue = "";
        organiser = "";
        quota = -1;
        hours = -1;
        signUpsList = new ArrayList<>();
        attendanceList = new ArrayList<>();
        eventID = "";
        eventStatus = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganiser() { return organiser; }

    public void setOrganiser(String organiser) { this.organiser = organiser; }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public void reduceQuota() { --this.quota; }

    public String getEventID() { return eventID; }

    public void setEventID(String key) {
        this.eventID = key;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    // Returns true if vol successfully added
    public boolean canAddVolunteer(String Uid) {
        if (!this.signUpsList.isEmpty()) {
            if (this.signUpsList.contains(Uid)) {
                return false;
            }
        }
        this.signUpsList.add(Uid);
        return true;
    }

    public ArrayList<String> getSignUpsList() {
        return this.signUpsList;
    }

    public ArrayList<String> getAttendanceList() {
        return this.attendanceList;
    }

    public void updateAttendance(String attendee) {
        this.signUpsList.remove(attendee);
        this.attendanceList.add(attendee);
    }

    public boolean getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(boolean newStatus) {
        this.eventStatus = newStatus;
    }
}

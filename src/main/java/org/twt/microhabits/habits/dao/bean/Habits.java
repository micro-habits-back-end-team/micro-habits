package org.twt.microhabits.habits.dao.bean;

import java.sql.Date;

public class Habits {
    private int id;
    private String name;
    private int checkInDays;
    private int consecutiveCheckInDays;
    private Date lastCheckInDate;
    private Date startDate;
    private int statesNumber;
    private int finishedDays;
    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCheckInDays() {
        return checkInDays;
    }

    public void setCheckInDays(int checkInDays) {
        this.checkInDays = checkInDays;
    }

    public int getConsecutiveCheckInDays() {
        return consecutiveCheckInDays;
    }

    public void setConsecutiveCheckInDays(int consecutiveCheckInDays) {
        this.consecutiveCheckInDays = consecutiveCheckInDays;
    }

    public Date getLastCheckInDate() {
        return lastCheckInDate;
    }

    public void setLastCheckInDate(Date lastCheckInDate) {
        this.lastCheckInDate = lastCheckInDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getStatesNumber() {
        return statesNumber;
    }

    public void setStatesNumber(int statesNumber) {
        this.statesNumber = statesNumber;
    }

    public int getFinishedDays() {
        return finishedDays;
    }

    public void setFinishedDays(int finishedDays) {
        this.finishedDays = finishedDays;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

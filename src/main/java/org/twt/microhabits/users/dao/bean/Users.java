package org.twt.microhabits.users.dao.bean;

import java.sql.Date;

public class Users {
    private String name;
    private String password;
    private float xp;
    private int consecutiveCheckInDays;
    private Date lastCheckInDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
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
}

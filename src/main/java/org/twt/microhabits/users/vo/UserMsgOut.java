package org.twt.microhabits.users.vo;

public class UserMsgOut extends UserRawMsg{
    private String user_name;
    private float xp;
    private int consecutive_check_days;
    private String last_check_date;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public int getConsecutive_check_days() {
        return consecutive_check_days;
    }

    public void setConsecutive_check_days(int consecutive_check_days) {
        this.consecutive_check_days = consecutive_check_days;
    }

    public String getLast_check_date() {
        return last_check_date;
    }

    public void setLast_check_date(String last_check_date) {
        this.last_check_date = last_check_date;
    }
}

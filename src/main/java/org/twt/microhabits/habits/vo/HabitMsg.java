package org.twt.microhabits.habits.vo;

public class HabitMsg {
    private int id;
    private String name;
    private String content;
    private int check_days;
    private int consecutive_check_days;
    private String last_check_date;
    private String start_date;
    private int states_number;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCheck_days() {
        return check_days;
    }

    public void setCheck_days(int check_days) {
        this.check_days = check_days;
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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public int getStates_number() {
        return states_number;
    }

    public void setStates_number(int states_number) {
        this.states_number = states_number;
    }
}

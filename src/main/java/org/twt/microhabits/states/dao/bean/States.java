package org.twt.microhabits.states.dao.bean;

public class States {
    private int id;
    private int habit_id;
    private String content;
    private int start_day;
    private int end_day;


    public States(int id, int habti_id, String content, int start_day, int end_day) {
        this.id = id;
        this.habit_id = habti_id;
        this.content = content;
        this.start_day = start_day;
        this.end_day = end_day;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHabit_id() {
        return habit_id;
    }

    public void setHabit_id(int habit_id) {
        this.habit_id = habit_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStart_day() {
        return start_day;
    }

    public void setStart_day(int start_day) {
        this.start_day = start_day;
    }

    public int getEnd_day() {
        return end_day;
    }

    public void setEnd_day(int end_day) {
        this.end_day = end_day;
    }
}

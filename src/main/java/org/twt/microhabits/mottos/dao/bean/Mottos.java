package org.twt.microhabits.mottos.dao.bean;

public class Mottos {
    private int id;
    private String motto;

    public Mottos(int id, String motto) {
        this.id = id;
        this.motto = motto;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }
}

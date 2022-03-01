package org.twt.microhabits.states.vo;

import java.util.List;

public class IdState {
    private int habit_id;
    private List<StateMsg> states;

    public int getHabit_id() {
        return habit_id;
    }

    public void setHabit_id(int habit_id) {
        this.habit_id = habit_id;
    }

    public List<StateMsg> getStates() {
        return states;
    }

    public void setStates(List<StateMsg> states) {
        this.states = states;
    }
}

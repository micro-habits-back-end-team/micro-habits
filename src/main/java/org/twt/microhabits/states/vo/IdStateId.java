package org.twt.microhabits.states.vo;

import java.util.List;

public class IdStateId {
    private int habit_id;
    private List<Integer> states_id;

    public int getHabit_id() {
        return habit_id;
    }

    public void setHabit_id(int habit_id) {
        this.habit_id = habit_id;
    }

    public List<Integer> getStates_id() {
        return states_id;
    }

    public void setStates_id(List<Integer> states_id) {
        this.states_id = states_id;
    }
}

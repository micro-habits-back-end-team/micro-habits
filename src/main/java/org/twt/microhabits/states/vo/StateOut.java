package org.twt.microhabits.states.vo;

import java.util.List;

public class StateOut extends StateRawMsg {
    private List<StateMsg> states;

    public List<StateMsg> getStates() {
        return states;
    }

    public void setStates(List<StateMsg> states) {
        this.states = states;
    }
}

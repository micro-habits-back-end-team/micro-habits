package org.twt.microhabits.states.vo;

import java.util.List;

public class StateCreateOut extends StateRawMsg {
    private List<Integer> states_id;

    public List<Integer> getStates_id() {
        return states_id;
    }

    public void setStates_id(List<Integer> states_id) {
        this.states_id = states_id;
    }
}

package org.twt.microhabits.service.states;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.twt.microhabits.states.dao.bean.States;
import org.twt.microhabits.states.dao.mapper.StatesMapper;
import org.twt.microhabits.states.vo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StatesService {
    private final StatesMapper statesMapper;

    @Autowired
    public StatesService(StatesMapper statesMapper) {
        this.statesMapper = statesMapper;
    }

    public StateCreateOut createStates(IdStateIn idStateIn) {
        int habitId = idStateIn.getHabit_id();
        List<StateMsgIn> states = idStateIn.getStates();
        StateCreateOut stateCreateOut = new StateCreateOut();
        for (StateMsgIn i : states) {// Detect content length
            if (i.getContent().length() > 200) {
                stateCreateOut.setCode(2);
                stateCreateOut.setMsg(String.format("Detected state_content:%s content longer than 200!", i.getContent()));
                return stateCreateOut;
            }
        }
        int count = 0;
        List<Integer> states_id = new ArrayList<>();
        for (StateMsgIn i : states) {// Insert states
            States temp = new States(0, habitId, i.getContent(), i.getStart_day(), i.getEnd_day());
            statesMapper.insertAState(temp);
            count += temp.getId()==0? 0:1;
            states_id.add(temp.getId());
        }
        if (count == states.size()) {// Detect success or not
            stateCreateOut.setStates_id(states_id);
            stateCreateOut.setCode(0);
            stateCreateOut.setMsg("Create successful!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                stateCreateOut.setCode(1);
                stateCreateOut.setMsg("Create failed!");
                e.printStackTrace();
                return stateCreateOut;
            }
        }
        List<Integer> stateEndDays = statesMapper.selectForEndDay(habitId);
        int temp = 0, endDay = Collections.max(stateEndDays);
        temp = statesMapper.updateFinishedDays(endDay, habitId);// Update habit's end_day
        count += temp==0? 0:1;
        temp = statesMapper.updateStatesNumber(stateEndDays.size(), habitId);// Update states_number
        count += temp==0? 0:1;
        if (count != states.size() + 2) {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                stateCreateOut.setCode(1);
                stateCreateOut.setMsg("Create failed!");
                e.printStackTrace();
                return stateCreateOut;
            }
        }
        return stateCreateOut;
    }

    public StateRawMsg changeStates(IdState idState) {
        int habitId = idState.getHabit_id();
        List<StateMsg> states = idState.getStates();
        StateRawMsg stateRawMsg = new StateRawMsg();
        List<Integer> habit_states_id = statesMapper.selectForId(habitId);
        for (StateMsg i : states) {
            if (i.getContent().length() > 200) {// Detect content length
                stateRawMsg.setCode(2);
                stateRawMsg.setMsg("Detected content longer than 200!");
                return stateRawMsg;
            }
            if (!habit_states_id.contains(i.getId())) {// Detect if habit_id match state_id or not
                stateRawMsg.setCode(3);
                stateRawMsg.setMsg(String.format("The habit_id does not match the state_id:%d!", i.getId()));
                return stateRawMsg;
            }
        }
        int count = 0;
        for (StateMsg i : states) {// Update states
            int temp = 0;
            temp = statesMapper.updateAState(i);
            count += temp==0? 0:1;
        }
        if (count == states.size()) {// Detect success or not
            stateRawMsg.setCode(0);
            stateRawMsg.setMsg("Change successfully!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                stateRawMsg.setCode(1);
                stateRawMsg.setMsg("Create failed!");
                e.printStackTrace();
                return stateRawMsg;
            }
        }
        List<Integer> stateEndDays = statesMapper.selectForEndDay(habitId);
        int temp = 0, endDay = Collections.max(stateEndDays);
        temp = statesMapper.updateFinishedDays(endDay, habitId);// Update habit's end_day
        count += temp==0? 0:1;
        temp = statesMapper.updateStatesNumber(stateEndDays.size(), habitId);// Update states_number
        count += temp==0? 0:1;
        if (count != states.size() + 2) {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                stateRawMsg.setCode(1);
                stateRawMsg.setMsg("Create failed!");
                e.printStackTrace();
                return stateRawMsg;
            }
        }
        return stateRawMsg;
    }

    public StateRawMsg deleteStates(IdStateId idStateId) {
        int habitId = idStateId.getHabit_id();
        List<Integer> statesId = idStateId.getStates_id();
        StateRawMsg stateRawMsg = new StateRawMsg();
        List<Integer> habit_states_id = statesMapper.selectForId(habitId);
        for (int i : statesId) {
            if (!habit_states_id.contains(i)) {// Detect if habit_id match state_id or not
                stateRawMsg.setCode(3);
                stateRawMsg.setMsg(String.format("The habit_id does not match the state_id:%d!", i));
                return stateRawMsg;
            }
        }
        int count = 0;
        for (int i : statesId) {// Delete statesId
            int temp = 0;
            temp = statesMapper.deleteAState(i);
            count += temp==0? 0:1;
        }
        if (count == statesId.size()) {// Detect success or not
            stateRawMsg.setCode(0);
            stateRawMsg.setMsg("Change successfully!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                stateRawMsg.setCode(1);
                stateRawMsg.setMsg("Create failed!");
                e.printStackTrace();
                return stateRawMsg;
            }
        }
        List<Integer> stateEndDays = statesMapper.selectForEndDay(habitId);
        stateEndDays.add(0);
        int temp = 0, endDay = Collections.max(stateEndDays);
        temp = statesMapper.updateFinishedDays(endDay, habitId);// Update habit's end_day
        count += temp==0? 0:1;
        temp = statesMapper.updateStatesNumber(stateEndDays.size() - 1, habitId);// Update states_number
        count += temp==0? 0:1;
        if (count != statesId.size() + 2) {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                stateRawMsg.setCode(1);
                stateRawMsg.setMsg("Create failed!");
                e.printStackTrace();
                return stateRawMsg;
            }
        }
        return stateRawMsg;
    }

    public StateOut selectStates(int habit_id) {
        StateOut stateOut = new StateOut();
        stateOut.setStates(statesMapper.selectStates(habit_id));
        if (stateOut.getStates() != null) {
            stateOut.setCode(0);
            stateOut.setMsg("Select successfully!");
        }
        else {
            stateOut.setCode(1);
            stateOut.setMsg("Database error!");
        }
        return stateOut;
    }
}

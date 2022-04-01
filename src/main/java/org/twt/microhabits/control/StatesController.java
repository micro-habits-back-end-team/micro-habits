package org.twt.microhabits.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.twt.microhabits.service.states.StatesService;
import org.twt.microhabits.states.vo.*;

@RestController
public class StatesController {
    private final StatesService statesService;

    @Autowired
    public StatesController(StatesService statesService) {
        this.statesService = statesService;
    }

    @Transactional
    @RequestMapping("/state/create")
    public StateCreateOut createStates(@RequestBody IdStateIn idStateIn) {
        return statesService.createStates(idStateIn);
    }

    @Transactional
    @RequestMapping("/state/change")
    public StateRawMsg changeStates(@RequestBody IdState idState) {
        return statesService.changeStates(idState);
    }

    @Transactional
    @RequestMapping("/state/delete")
    public StateRawMsg deleteStates(@RequestBody IdStateId idStateId) {
        return statesService.deleteStates(idStateId);
    }

    @RequestMapping("/state/select")
    public StateOut selectStates(int habit_id) {
        return statesService.selectStates(habit_id);
    }
}

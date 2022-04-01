package org.twt.microhabits.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.twt.microhabits.habits.vo.*;
import org.twt.microhabits.service.habits.HabitsService;

@RestController
public class HabitsController {
    private final HabitsService habitsService;

    @Autowired
    public HabitsController(HabitsService habitsService) {
        this.habitsService = habitsService;
    }

    @Transactional
    @RequestMapping("/habits/create")
    public HabitCreateOut createAHabit(HabitCreateIn habitCreateIn) {
        return habitsService.createAHabit(habitCreateIn);
    }

    @Transactional
    @RequestMapping("/habits/delete")
    public HabitRawMsg deleteAHabit(int habit_id) {
        return habitsService.deleteAHabit(habit_id);
    }

    @Transactional
    @RequestMapping("/habits/change_name")
    public HabitRawMsg updateAHabitName(HabitChangeNameIn habitChangeNameIn) {
        return habitsService.updateAHabitName(habitChangeNameIn);
    }

    @Transactional
    @RequestMapping("/habits/check")
    public HabitRawMsg checkAHabit(int habit_id) {
        return habitsService.checkAHabit(habit_id);
    }

    @RequestMapping("/habits/select/training")
    public HabitSelectOut selectTrainingHabits(String user_name) {
        return habitsService.selectTrainingHabits(user_name);
    }

    @RequestMapping("/habits/select/trained")
    public HabitSelectOut selectTrainedHabits(String user_name) {
        return habitsService.selectTrainedHabits(user_name);
    }

    @RequestMapping("/habits/select/checked")
    public HabitSelectOut selectCheckedHabits(String user_name) {
        return habitsService.selectCheckedHabits(user_name);
    }

    @RequestMapping("/habits/select/unchecked")
    public HabitSelectOut selectUncheckedHabits(String user_name) {
        return habitsService.selectUncheckedHabits(user_name);
    }
}

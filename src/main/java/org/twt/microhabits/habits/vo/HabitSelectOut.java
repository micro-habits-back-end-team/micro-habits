package org.twt.microhabits.habits.vo;

import java.util.List;

public class HabitSelectOut extends HabitRawMsg{
    private List<HabitMsg> habits;

    public List<HabitMsg> getHabits() {
        return habits;
    }

    public void setHabits(List<HabitMsg> habits) {
        this.habits = habits;
    }
}

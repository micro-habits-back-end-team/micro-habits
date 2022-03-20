package org.twt.microhabits.service.habits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.twt.microhabits.habits.dao.bean.Habits;
import org.twt.microhabits.habits.dao.mapper.HabitsMapper;
import org.twt.microhabits.habits.vo.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class HabitsService {
    private final HabitsMapper habitsMapper;

    @Autowired
    public HabitsService(HabitsMapper habitsMapper) {
        this.habitsMapper = habitsMapper;
    }

    public HabitCreateOut createAHabit(HabitCreateIn habitCreateIn) {
        String userName = habitCreateIn.getUser_name();
        String habitName = habitCreateIn.getHabit_name();
        String startDateString = habitCreateIn.getStart_date();
        HabitCreateOut habitCreateOut = new HabitCreateOut();

        if (habitName.length() > 30) {// Detect habit_name length
            habitCreateOut.setCode(2);
            habitCreateOut.setMsg(String.format("Detected habit_name:%s longer than 30!", habitName));
            return habitCreateOut;
        }

        if (startDateString.equals("0000-00-00")) {
            startDateString = new Date(getTodayCalendar().getTime().getTime()).toString();
        }

        // Create a habits bean
        Habits habits = new Habits();
        {
            habits.setId(0);
            habits.setName(habitName);
            habits.setCheckInDays(0);
            habits.setConsecutiveCheckInDays(0);
            habits.setLastCheckInDate(null);
            try {
                habits.setStartDate(new Date((new SimpleDateFormat("yyyy-MM-dd")).parse(startDateString).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                habitCreateOut.setCode(3);
                habitCreateOut.setMsg(String.format("Detected start_date:%s format error!", startDateString));
                return habitCreateOut;
            }
            habits.setStatesNumber(0);
            habits.setFinishedDays(0);
            habits.setUserName(userName);
        }
        habitsMapper.insertAHabit(habits);
        if (habits.getId() != 0) {
            habitCreateOut.setHabit_id(habits.getId());
            habitCreateOut.setCode(0);
            habitCreateOut.setMsg("Create successful!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                habitCreateOut.setCode(1);
                habitCreateOut.setMsg("Create failed!");
                e.printStackTrace();
                return habitCreateOut;
            }
        }
        return habitCreateOut;
    }

    public HabitRawMsg deleteAHabit(int habit_id) {
        int habitId = habit_id;
        HabitRawMsg habitRawMsg = new HabitRawMsg();
        int result = habitsMapper.deleteAHabit(habitId);
        if (result == 1) {
            habitRawMsg.setCode(0);
            habitRawMsg.setMsg("Delete successful!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                habitRawMsg.setCode(1);
                habitRawMsg.setMsg("Delete failed!");
                e.printStackTrace();
                return habitRawMsg;
            }
        }
        return habitRawMsg;
    }

    public HabitRawMsg updateAHabitName(HabitChangeNameIn habitChangeNameIn) {
        int habitId = habitChangeNameIn.getHabit_id();
        String habitName = habitChangeNameIn.getHabit_name();
        HabitRawMsg habitRawMsg = new HabitRawMsg();

        if (habitName.length() > 30) {// Detect habit_name length
            habitRawMsg.setCode(2);
            habitRawMsg.setMsg(String.format("Detected habit_name:%s longer than 30!", habitName));
            return habitRawMsg;
        }

        int result = habitsMapper.updateAHabitName(habitId, habitName);
        if (result == 1) {
            habitRawMsg.setCode(0);
            habitRawMsg.setMsg("Change name successful!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                habitRawMsg.setCode(1);
                habitRawMsg.setMsg("Change name failed!");
                e.printStackTrace();
                return habitRawMsg;
            }
        }
        return habitRawMsg;
    }

    public HabitRawMsg checkAHabit(int habit_id) {
        int habitId = habit_id;
        HabitRawMsg habitRawMsg = new HabitRawMsg();
        String userName = habitsMapper.selectUserName(habitId);

        Calendar nowTime = Calendar.getInstance();
        Calendar calendarToday = Calendar.getInstance();
        {// Time check
            if (nowTime.get(Calendar.HOUR_OF_DAY) == 0 &&
                    nowTime.get(Calendar.MINUTE) >= 0 && nowTime.get(Calendar.MINUTE) <= 30) {
                try {
                    throw new Exception("Banning check in time!");
                } catch (Exception e) {
                    habitRawMsg.setCode(2);
                    habitRawMsg.setMsg("Banning check in time!");
                    e.printStackTrace();
                    return habitRawMsg;
                }
            }
            calendarToday.clear();
            calendarToday.set(Calendar.YEAR, nowTime.get(Calendar.YEAR));
            calendarToday.set(Calendar.MONTH, nowTime.get(Calendar.MONTH));
            calendarToday.set(Calendar.DATE, nowTime.get(Calendar.DATE));
        }
        Date today = new Date(calendarToday.getTime().getTime());
        calendarToday.add(Calendar.DATE, -1);
        Date yesterday = new Date(calendarToday.getTime().getTime());
        Date lastCheckInDate = habitsMapper.selectLastCheckInDate(habitId);
        Date userLastCheckInDate = habitsMapper.selectUserLastCheckInDate(userName);

        int count = 0;
        // Date checking and updating
        if (lastCheckInDate.before(yesterday)) {
            count += habitsMapper.oneConsecutiveCheckInDays(habitId);
            count += habitsMapper.updateLastCheckInDate(habitId, today.toString());
        }
        else {
            if (lastCheckInDate.before(today)) {
                count += habitsMapper.addConsecutiveCheckInDays(habitId);
                count += habitsMapper.updateLastCheckInDate(habitId, today.toString());
            }
            else {
                try {
                    throw new Exception("Habit has checked today!");
                } catch (Exception e) {
                    habitRawMsg.setCode(3);
                    habitRawMsg.setMsg("Habit has checked today!");
                    e.printStackTrace();
                    return habitRawMsg;
                }
            }
        }
        count += habitsMapper.addCheckInDays(habitId);
        if (userLastCheckInDate.before(yesterday)) {
            count += habitsMapper.oneUserConsecutiveCheckInDays(userName);
            count += habitsMapper.updateUserLastCheckInDate(userName, today.toString());
        }
        else {
            if (userLastCheckInDate.before(today)) {
                count += habitsMapper.addUserConsecutiveCheckInDays(userName);
                count += habitsMapper.updateUserLastCheckInDate(userName, today.toString());
            }
            else {
                count += 2;
            }
        }

        if (count == 5) {
            habitRawMsg.setCode(0);
            habitRawMsg.setMsg("Check in successfully!");
        }
        else {
            try {
                throw new Exception("Database or time error!");
            } catch (Exception e) {
                habitRawMsg.setCode(1);
                habitRawMsg.setMsg("Check in failed!");
                e.printStackTrace();
                return habitRawMsg;
            }
        }
        return habitRawMsg;
    }

    public HabitSelectOut selectTrainingHabits(String user_name) {
        String userName = user_name;
        HabitSelectOut habitSelectOut = new HabitSelectOut();

        if (userName.length() > 10) {// Detect user_name length
            habitSelectOut.setCode(2);
            habitSelectOut.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return habitSelectOut;
        }

        Calendar todayCalendar = getTodayCalendar();
        Date today = new Date(todayCalendar.getTime().getTime());

        List<Habits> habits = habitsMapper.selectTrainingHabits(userName, today.toString());
        if (habits != null) {
            habitSelectOut.setCode(0);
            habitSelectOut.setMsg("Select successfully!");
        }
        else {
            habitSelectOut.setCode(1);
            habitSelectOut.setMsg("Database error!");
        }
        List<HabitMsg> habitMsgs = new ArrayList<>();
        for (Habits i : habits) {
            HabitMsg habitMsg = new HabitMsg();
            habitMsg.setId(i.getId());
            habitMsg.setName(i.getName());
            habitMsg.setContent(getHabitStateContent(i.getId(), todayCalendar, i.getStartDate()));
            habitMsg.setCheck_days(i.getCheckInDays());
            habitMsg.setConsecutive_check_days(i.getConsecutiveCheckInDays());
            habitMsg.setLast_check_date(i.getLastCheckInDate().toString());
            habitMsg.setStart_date(i.getStartDate().toString());
            habitMsg.setStates_number(i.getStatesNumber());
            habitMsgs.add(habitMsg);
        }
        habitSelectOut.setHabits(habitMsgs);
        return habitSelectOut;
    }

    public HabitSelectOut selectTrainedHabits(String user_name) {
        String userName = user_name;
        HabitSelectOut habitSelectOut = new HabitSelectOut();

        if (userName.length() > 10) {// Detect user_name length
            habitSelectOut.setCode(2);
            habitSelectOut.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return habitSelectOut;
        }

        Calendar todayCalendar = getTodayCalendar();
        Date today = new Date(todayCalendar.getTime().getTime());

        List<Habits> habits = habitsMapper.selectTrainedHabits(userName, today.toString());
        if (habits != null) {
            habitSelectOut.setCode(0);
            habitSelectOut.setMsg("Select successfully!");
        }
        else {
            habitSelectOut.setCode(1);
            habitSelectOut.setMsg("Database error!");
        }
        List<HabitMsg> habitMsgs = new ArrayList<>();
        for (Habits i : habits) {
            HabitMsg habitMsg = new HabitMsg();
            habitMsg.setId(i.getId());
            habitMsg.setName(i.getName());
            habitMsg.setContent(habitsMapper.selectFinishedStateContent(i.getId(), i.getFinishedDays()));
            habitMsg.setCheck_days(i.getCheckInDays());
            habitMsg.setConsecutive_check_days(i.getConsecutiveCheckInDays());
            habitMsg.setLast_check_date(i.getLastCheckInDate().toString());
            habitMsg.setStart_date(i.getStartDate().toString());
            habitMsg.setStates_number(i.getStatesNumber());
            habitMsgs.add(habitMsg);
        }
        habitSelectOut.setHabits(habitMsgs);
        return habitSelectOut;
    }

    public HabitSelectOut selectCheckedHabits(String user_name) {
        String userName = user_name;
        HabitSelectOut habitSelectOut = new HabitSelectOut();

        if (userName.length() > 10) {// Detect user_name length
            habitSelectOut.setCode(2);
            habitSelectOut.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return habitSelectOut;
        }

        Calendar todayCalendar = getTodayCalendar();
        Date todayDate = new Date(todayCalendar.getTime().getTime());

        List<Habits> habits = habitsMapper.selectCheckedHabits(userName, todayDate.toString());
        if (habits != null) {
            habitSelectOut.setCode(0);
            habitSelectOut.setMsg("Select successfully!");
        }
        else {
            habitSelectOut.setCode(1);
            habitSelectOut.setMsg("Database error!");
        }
        List<HabitMsg> habitMsgs = new ArrayList<>();
        for (Habits i : habits) {
            HabitMsg habitMsg = new HabitMsg();
            habitMsg.setId(i.getId());
            habitMsg.setName(i.getName());
            habitMsg.setContent(getHabitStateContent(i.getId(), todayCalendar, i.getStartDate()));
            habitMsg.setCheck_days(i.getCheckInDays());
            habitMsg.setConsecutive_check_days(i.getConsecutiveCheckInDays());
            habitMsg.setLast_check_date(i.getLastCheckInDate().toString());
            habitMsg.setStart_date(i.getStartDate().toString());
            habitMsg.setStates_number(i.getStatesNumber());
            habitMsgs.add(habitMsg);
        }
        habitSelectOut.setHabits(habitMsgs);
        return habitSelectOut;
    }

    public HabitSelectOut selectUncheckedHabits(String user_name) {
        String userName = user_name;
        HabitSelectOut habitSelectOut = new HabitSelectOut();

        if (userName.length() > 10) {// Detect user_name length
            habitSelectOut.setCode(2);
            habitSelectOut.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return habitSelectOut;
        }

        Calendar todayCalendar = getTodayCalendar();
        Date todayDate = new Date(todayCalendar.getTime().getTime());

        List<Habits> habits = habitsMapper.selectUncheckedHabits(userName, todayDate.toString());
        if (habits != null) {
            habitSelectOut.setCode(0);
            habitSelectOut.setMsg("Select successfully!");
        }
        else {
            habitSelectOut.setCode(1);
            habitSelectOut.setMsg("Database error!");
        }
        List<HabitMsg> habitMsgs = new ArrayList<>();
        for (Habits i : habits) {
            HabitMsg habitMsg = new HabitMsg();
            habitMsg.setId(i.getId());
            habitMsg.setName(i.getName());
            habitMsg.setContent(getHabitStateContent(i.getId(), todayCalendar, i.getStartDate()));
            habitMsg.setCheck_days(i.getCheckInDays());
            habitMsg.setConsecutive_check_days(i.getConsecutiveCheckInDays());
            habitMsg.setLast_check_date(i.getLastCheckInDate().toString());
            habitMsg.setStart_date(i.getStartDate().toString());
            habitMsg.setStates_number(i.getStatesNumber());
            habitMsgs.add(habitMsg);
        }
        habitSelectOut.setHabits(habitMsgs);
        return habitSelectOut;
    }

    public Calendar getTodayCalendar() {
        Calendar nowTime = Calendar.getInstance();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.clear();
        calendarToday.set(Calendar.YEAR, nowTime.get(Calendar.YEAR));
        calendarToday.set(Calendar.MONTH, nowTime.get(Calendar.MONTH));
        calendarToday.set(Calendar.DATE, nowTime.get(Calendar.DATE));
        return calendarToday;
    }

    public String getHabitStateContent(int habitId, Calendar todayCalendar, Date startDate) {
        Calendar temp = Calendar.getInstance();
        temp.clear();
        temp.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR));
        temp.set(Calendar.MONTH, todayCalendar.get(Calendar.MONTH));
        temp.set(Calendar.DATE, todayCalendar.get(Calendar.DATE));
        Date tempDate = new Date(temp.getTime().getTime());
        int pastedDays = (int)((tempDate.getTime() - startDate.getTime())  / (1000 * 60 * 60 *24)) + 1;
        return habitsMapper.selectStateContent(habitId, pastedDays);
    }
}

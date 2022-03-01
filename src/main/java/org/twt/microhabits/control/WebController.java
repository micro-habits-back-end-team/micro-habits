package org.twt.microhabits.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.twt.microhabits.habits.dao.bean.Habits;
import org.twt.microhabits.habits.dao.mapper.HabitsMapper;
import org.twt.microhabits.habits.vo.*;
import org.twt.microhabits.mottos.dao.bean.Mottos;
import org.twt.microhabits.mottos.dao.mapper.MottosMapper;
import org.twt.microhabits.pictures.dao.bean.Pictures;
import org.twt.microhabits.pictures.dao.mapper.PicturesMapper;
import org.twt.microhabits.pictures.vo.NameIn;
import org.twt.microhabits.states.dao.bean.States;
import org.twt.microhabits.states.dao.mapper.StatesMapper;
import org.twt.microhabits.states.vo.*;
import org.twt.microhabits.users.dao.bean.Users;
import org.twt.microhabits.users.dao.mapper.UsersMapper;
import org.twt.microhabits.users.vo.UserMsgOut;
import org.twt.microhabits.users.vo.UserRawMsg;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class WebController {
    private final MottosMapper mottosMapper;
    private final PicturesMapper picturesMapper;
    private final StatesMapper statesMapper;
    private final HabitsMapper habitsMapper;
    private final UsersMapper usersMapper;

    @Autowired
    public WebController(MottosMapper mottosMapper, PicturesMapper picturesMapper, StatesMapper statesMapper, HabitsMapper habitsMapper, UsersMapper usersMapper) {
        this.mottosMapper = mottosMapper;
        this.picturesMapper = picturesMapper;
        this.statesMapper = statesMapper;
        this.habitsMapper = habitsMapper;
        this.usersMapper = usersMapper;
    }

    @RequestMapping("/")
    public String index() {
        return "Server setup successfully!";
    }

    @RequestMapping("/motto")
    public Mottos getAMotto() {
        Mottos mottoReturn = mottosMapper.selectAMotto();
        if (mottoReturn == null) {
            return new Mottos(-1, "Database is empty!");
        }
        else {
            return mottoReturn;
        }
    }

    @RequestMapping("/picture")
    public Pictures getAPicture(NameIn nameIn) {
        Pictures pictureReturn = picturesMapper.selectAPicture(nameIn.getName());
        if (pictureReturn == null) {
            return new Pictures(-1, "error", "Database can not find this picture!");
        }
        else {
            return pictureReturn;
        }
    }

    @RequestMapping("/date")
    public String getDate() {
        return (new Date(getTodayCalendar().getTime().getTime())).toString();
    }

    @RequestMapping("/time")
    public String getTime() {
        return (new java.util.Date()).toString();
    }

    @RequestMapping("/time2")
    public String getTime2() {
        Calendar nowTime = Calendar.getInstance();
        return String.format("%s:%s:%s", nowTime.get(Calendar.HOUR_OF_DAY), nowTime.get(Calendar.MINUTE), nowTime.get(Calendar.SECOND));
    }

    @Transactional
    @RequestMapping("/state/create")
    public StateCreateOut createStates(@RequestBody IdStateIn idStateIn) {
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

    @Transactional
    @RequestMapping("/state/change")
    public StateRawMsg changeStates(@RequestBody IdState idState) {
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

    @Transactional
    @RequestMapping("/state/delete")
    public StateRawMsg deleteStates(@RequestBody IdStateId idStateId) {
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

    @RequestMapping("/state/select")
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

    @Transactional
    @RequestMapping("/habits/create")
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

    @Transactional
    @RequestMapping("/habits/delete")
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

    @Transactional
    @RequestMapping("/habits/change_name")
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

    @Transactional
    @RequestMapping("/habits/check")
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

    @RequestMapping("/habits/select/training")
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

    @RequestMapping("/habits/select/trained")
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

    @RequestMapping("/habits/select/checked")
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

    @RequestMapping("/habits/select/unchecked")
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

    private Calendar getTodayCalendar() {
        Calendar nowTime = Calendar.getInstance();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.clear();
        calendarToday.set(Calendar.YEAR, nowTime.get(Calendar.YEAR));
        calendarToday.set(Calendar.MONTH, nowTime.get(Calendar.MONTH));
        calendarToday.set(Calendar.DATE, nowTime.get(Calendar.DATE));
        return calendarToday;
    }

    private String getHabitStateContent(int habitId, Calendar todayCalendar, Date startDate) {
        Calendar temp = Calendar.getInstance();
        temp.clear();
        temp.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR));
        temp.set(Calendar.MONTH, todayCalendar.get(Calendar.MONTH));
        temp.set(Calendar.DATE, todayCalendar.get(Calendar.DATE));
        Date tempDate = new Date(temp.getTime().getTime());
        int pastedDays = (int)((tempDate.getTime() - startDate.getTime())  / (1000 * 60 * 60 *24)) + 1;
        return habitsMapper.selectStateContent(habitId, pastedDays);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RequestMapping("/users/register")
    public UserRawMsg registerAUser(String user_name, String user_password) {
        String userName = user_name;
        String userPassword = user_password;
        UserRawMsg userRawMsg = new UserRawMsg();

        if (userName.length() > 10) {// Detect user_name length
            userRawMsg.setCode(2);
            userRawMsg.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return userRawMsg;
        }
        if (userPassword.length() > 32) {// Detect user_password length
            userRawMsg.setCode(3);
            userRawMsg.setMsg(String.format("Detected user_password:%s longer than 32!", userPassword));
            return userRawMsg;
        }

        String nameSelect = usersMapper.selectUserName(userName);
        if (nameSelect != null) {
            try {
                throw new Exception("This name has existed!");
            } catch (Exception e) {
                userRawMsg.setCode(1);
                userRawMsg.setMsg(String.format("This name:%s has existed!", userName));
                e.printStackTrace();
                return userRawMsg;
            }
        }

        int result = usersMapper.userRegister(userName, userPassword);
        if (result == 1) {
            userRawMsg.setCode(0);
            userRawMsg.setMsg("Register successfully!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                userRawMsg.setCode(4);
                userRawMsg.setMsg("Register failed!");
                e.printStackTrace();
                return userRawMsg;
            }
        }

        return userRawMsg;
    }

    @Transactional
    @RequestMapping("/users/login")
    public UserRawMsg userLogin(String user_name, String user_password) {
        String userName = user_name;
        String userPassword = user_password;
        UserRawMsg userRawMsg = new UserRawMsg();

        if (userName.length() > 10) {// Detect user_name length
            userRawMsg.setCode(2);
            userRawMsg.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return userRawMsg;
        }
        if (userPassword.length() > 32) {// Detect user_password length
            userRawMsg.setCode(3);
            userRawMsg.setMsg(String.format("Detected user_password:%s longer than 32!", userPassword));
            return userRawMsg;
        }

        String result = usersMapper.userLogin(userName, userPassword);
        if (result != null) {
            userRawMsg.setCode(0);
            userRawMsg.setMsg("Login test passed!");
        }
        else {
            try {
                throw new Exception("Username and password don't matched!");
            } catch (Exception e) {
                userRawMsg.setCode(1);
                userRawMsg.setMsg("Username and password don't matched!");
                e.printStackTrace();
                return userRawMsg;
            }
        }

        return userRawMsg;
    }

    @Transactional
    @RequestMapping("/users/basc_msg")
    public UserMsgOut selectUserMsg(String user_name) {
        String userName = user_name;
        UserMsgOut userMsgOut = new UserMsgOut();

        if (userName.length() > 10) {// Detect user_name length
            userMsgOut.setCode(2);
            userMsgOut.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return userMsgOut;
        }

        String nameSelect = usersMapper.selectUserName(userName);
        if (nameSelect == null) {
            try {
                throw new Exception("This name don't exist!");
            } catch (Exception e) {
                userMsgOut.setCode(1);
                userMsgOut.setMsg(String.format("This name:%s don't exist!", userName));
                e.printStackTrace();
                return userMsgOut;
            }
        }

        Users result = usersMapper.selectUserMsg(userName);
        if (result != null) {
            userMsgOut.setCode(0);
            userMsgOut.setMsg("Gain msg successfully!");
            userMsgOut.setUser_name(result.getName());
            userMsgOut.setXp(result.getXp());
            userMsgOut.setConsecutive_check_days(result.getConsecutiveCheckInDays());
            userMsgOut.setLast_check_date(result.getLastCheckInDate().toString());
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                userMsgOut.setCode(2);
                userMsgOut.setMsg("Gain msg failed!");
                e.printStackTrace();
                return userMsgOut;
            }
        }

        return userMsgOut;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RequestMapping("/users/change_name")
    public UserRawMsg updateUserName(String user_name, String user_name_new) {
        String userName = user_name;
        String userNameNew = user_name_new;
        UserRawMsg userRawMsg = new UserRawMsg();

        if (userName.length() > 10) {// Detect user_name length
            userRawMsg.setCode(2);
            userRawMsg.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return userRawMsg;
        }
        if (userNameNew.length() > 10) {// Detect user_name_new length
            userRawMsg.setCode(2);
            userRawMsg.setMsg(String.format("Detected user_name_new:%s longer than 10!", userNameNew));
            return userRawMsg;
        }

        String nameSelect = usersMapper.selectUserName(userName);
        if (nameSelect == null) {
            try {
                throw new Exception("This name don't exist!");
            } catch (Exception e) {
                userRawMsg.setCode(3);
                userRawMsg.setMsg(String.format("This name:%s don't exist!", userName));
                e.printStackTrace();
                return userRawMsg;
            }
        }
        nameSelect = usersMapper.selectUserName(userNameNew);
        if (nameSelect != null) {
            try {
                throw new Exception("This new name has existed!");
            } catch (Exception e) {
                userRawMsg.setCode(2);
                userRawMsg.setMsg(String.format("This new name:%s has existed!", userNameNew));
                e.printStackTrace();
                return userRawMsg;
            }
        }

        int result = usersMapper.updateUserName(userName, userNameNew);
        if (result == 1) {
            userRawMsg.setCode(0);
            userRawMsg.setMsg("Change name successfully!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                userRawMsg.setCode(2);
                userRawMsg.setMsg("Change name failed!");
                e.printStackTrace();
                return userRawMsg;
            }
        }

        return userRawMsg;
    }

    @Transactional
    @RequestMapping("/users/change_xp")
    public UserRawMsg updateUserXp(String user_name, int user_xp_new) {
        String userName = user_name;
        int userXpNew = user_xp_new;
        UserRawMsg userRawMsg = new UserRawMsg();

        if (userName.length() > 10) {// Detect user_name length
            userRawMsg.setCode(2);
            userRawMsg.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return userRawMsg;
        }

        String nameSelect = usersMapper.selectUserName(userName);
        if (nameSelect == null) {
            try {
                throw new Exception("This name don't exist!");
            } catch (Exception e) {
                userRawMsg.setCode(2);
                userRawMsg.setMsg(String.format("This name:%s don't exist!", userName));
                e.printStackTrace();
                return userRawMsg;
            }
        }

        int result = usersMapper.updateUserXp(userName, userXpNew);
        if (result == 1) {
            userRawMsg.setCode(0);
            userRawMsg.setMsg("Change xp successfully!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                userRawMsg.setCode(2);
                userRawMsg.setMsg("Change xp failed!");
                e.printStackTrace();
                return userRawMsg;
            }
        }

        return userRawMsg;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RequestMapping("/users/change_password")
    public UserRawMsg updateUserName(String user_name, String user_password, String user_password_new) {
        String userName = user_name;
        String userPassword = user_password;
        String userPasswordNew = user_password_new;
        UserRawMsg userRawMsg = new UserRawMsg();

        if (userName.length() > 10) {// Detect user_name length
            userRawMsg.setCode(2);
            userRawMsg.setMsg(String.format("Detected user_name:%s longer than 10!", userName));
            return userRawMsg;
        }
        if (userPassword.length() > 32) {// Detect user_password length
            userRawMsg.setCode(3);
            userRawMsg.setMsg(String.format("Detected user_password:%s longer than 32!", userPassword));
            return userRawMsg;
        }
        if (userPasswordNew.length() > 32) {// Detect user_password_new length
            userRawMsg.setCode(4);
            userRawMsg.setMsg(String.format("Detected user_password_new:%s longer than 32!", userPasswordNew));
            return userRawMsg;
        }

        String nameSelect = usersMapper.selectUserName(userName);
        if (nameSelect == null) {
            try {
                throw new Exception("This name don't exist!");
            } catch (Exception e) {
                userRawMsg.setCode(5);
                userRawMsg.setMsg(String.format("This name:%s don't exist!", userName));
                e.printStackTrace();
                return userRawMsg;
            }
        }
        String passwordMatch = usersMapper.userLogin(userName, userPassword);
        if (passwordMatch == null) {
            try {
                throw new Exception("Username and password don't matched!");
            } catch (Exception e) {
                userRawMsg.setCode(6);
                userRawMsg.setMsg("Username and password don't matched!");
                e.printStackTrace();
                return userRawMsg;
            }
        }

        int result = usersMapper.updateUserPassword(userName, userPasswordNew);
        if (result == 1) {
            userRawMsg.setCode(0);
            userRawMsg.setMsg("Change password successfully!");
        }
        else {
            try {
                throw new Exception("Database error!");
            } catch (Exception e) {
                userRawMsg.setCode(1);
                userRawMsg.setMsg("Change password failed!");
                e.printStackTrace();
                return userRawMsg;
            }
        }

        return userRawMsg;
    }
}

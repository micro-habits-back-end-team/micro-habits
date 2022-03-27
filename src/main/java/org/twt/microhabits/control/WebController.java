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
import org.twt.microhabits.service.habits.HabitsService;
import org.twt.microhabits.service.states.StatesService;
import org.twt.microhabits.service.users.UsersService;
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
    private final StatesService statesService;
    private final HabitsService habitsService;
    private final UsersService usersService;

    @Autowired
    public WebController(MottosMapper mottosMapper, PicturesMapper picturesMapper, StatesService statesService, HabitsService habitsService, UsersService usersService) {
        this.mottosMapper = mottosMapper;
        this.picturesMapper = picturesMapper;
        this.statesService = statesService;
        this.habitsService = habitsService;
        this.usersService = usersService;
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
        return (new Date(habitsService.getTodayCalendar().getTime().getTime())).toString();
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RequestMapping("/users/register")
    public UserRawMsg registerAUser(String user_name, String user_password) {
        return usersService.registerAUser(user_name, user_password);
    }

    @Transactional
    @RequestMapping("/users/login")
    public UserRawMsg userLogin(String user_name, String user_password) {
        return usersService.userLogin(user_name, user_password);
    }

    @Transactional
    @RequestMapping("/users/basc_msg")
    public UserMsgOut selectUserMsg(String user_name) {
        return usersService.selectUserMsg(user_name);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RequestMapping("/users/change_name")
    public UserRawMsg updateUserName(String user_name, String user_name_new) {
        return usersService.updateUserName(user_name, user_name_new);
    }

    @Transactional
    @RequestMapping("/users/change_xp")
    public UserRawMsg updateUserXp(String user_name, int user_xp_new) {
        return usersService.updateUserXp(user_name, user_xp_new);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RequestMapping("/users/change_password")
    public UserRawMsg updateUserPassword(String user_name, String user_password, String user_password_new) {
        return usersService.updateUserPassword(user_name, user_password, user_password_new);
    }
}

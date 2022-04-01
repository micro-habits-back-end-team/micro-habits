package org.twt.microhabits.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.twt.microhabits.mottos.dao.bean.Mottos;
import org.twt.microhabits.mottos.dao.mapper.MottosMapper;
import org.twt.microhabits.pictures.dao.bean.Pictures;
import org.twt.microhabits.pictures.dao.mapper.PicturesMapper;
import org.twt.microhabits.pictures.vo.NameIn;
import org.twt.microhabits.service.habits.HabitsService;

import java.sql.Date;
import java.util.Calendar;

@RestController
public class WebController {
    private final MottosMapper mottosMapper;
    private final PicturesMapper picturesMapper;
    private final HabitsService habitsService;


    @Autowired
    public WebController(MottosMapper mottosMapper, PicturesMapper picturesMapper, HabitsService habitsService) {
        this.mottosMapper = mottosMapper;
        this.picturesMapper = picturesMapper;
        this.habitsService = habitsService;
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
        } else {
            return mottoReturn;
        }
    }

    @RequestMapping("/picture")
    public Pictures getAPicture(NameIn nameIn) {
        Pictures pictureReturn = picturesMapper.selectAPicture(nameIn.getName());
        if (pictureReturn == null) {
            return new Pictures(-1, "error", "Database can not find this picture!");
        } else {
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
}

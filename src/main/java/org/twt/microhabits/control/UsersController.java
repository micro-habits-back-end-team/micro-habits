package org.twt.microhabits.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.twt.microhabits.service.users.UsersService;
import org.twt.microhabits.users.vo.UserMsgOut;
import org.twt.microhabits.users.vo.UserRawMsg;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
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

    @Transactional
    @RequestMapping("/users/set_head")
    public UserRawMsg updateUserHeadPortrait(String user_name, MultipartFile user_head) {
        try {
            return usersService.updateUserHeadPortrait(user_name, user_head);
        } catch (IOException e) {
            UserRawMsg userRawMsg = new UserRawMsg();
            userRawMsg.setCode(2);
            userRawMsg.setMsg("IOException!");
            e.printStackTrace();
            return userRawMsg;
        }
    }

    @RequestMapping(value = "/users/head/{user_name}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getUserHeadPortrait(@PathVariable String user_name) {
        try {
            return usersService.getUserHeadPortrait(user_name);
        } catch (IOException e) {
            return null;
        }
    }
}

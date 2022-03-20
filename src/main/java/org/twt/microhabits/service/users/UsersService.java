package org.twt.microhabits.service.users;

import org.springframework.stereotype.Component;
import org.twt.microhabits.users.dao.bean.Users;
import org.twt.microhabits.users.dao.mapper.UsersMapper;
import org.twt.microhabits.users.vo.UserMsgOut;
import org.twt.microhabits.users.vo.UserRawMsg;

@Component
public class UsersService {
    private final UsersMapper usersMapper;

    public UsersService(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

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

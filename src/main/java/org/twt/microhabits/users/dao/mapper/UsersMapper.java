package org.twt.microhabits.users.dao.mapper;

import org.apache.ibatis.annotations.*;
import org.twt.microhabits.users.dao.bean.Users;

@Mapper
public interface UsersMapper {
//    @Results(
//            id = "usersMap",
//            value = {
//                    @Result(property = "name", column = "name", id = true),
//                    @Result(property = "password", column = "password"),
//                    @Result(property = "xp", column = "xp"),
//                    @Result(property = "consecutiveCheckInDays", column = "consecutive_check-in_days"),
//                    @Result(property = "lastCheckInDate", column = "last_check-in_date")
//            }
//    )

    @Insert("INSERT INTO users (name, password, xp, `consecutive_check-in_days`, `last_check-in_date`) " +
            "VALUES (#{name}, #{password}, 0, 0, '1000-01-01')")
    int userRegister(@Param("name") String userName, @Param("password") String userPassword);

    @Update("UPDATE users SET name=#{new_name} WHERE name=#{name}")
    int updateUserName(@Param("name") String userName, @Param("new_name") String userNameNew);

    @Update("UPDATE users SET xp=#{xp} WHERE name=#{name}")
    int updateUserXp(@Param("name") String userName, @Param("xp") int xp);

    @Update("UPDATE users SET password=#{newPassword} WHERE name=#{name}")
    int updateUserPassword(@Param("name") String userName, @Param("newPassword") String userPasswordNew);

    @Select("SELECT name FROM users WHERE name=#{name}")
    String selectUserName(@Param("name") String userName);

    @Select("SELECT name FROM users WHERE name=#{name} AND password=#{password}")
    String userLogin(@Param("name") String userName, @Param("password") String userPassword);

    @Results(
            id = "usersMap",
            value = {
                    @Result(property = "name", column = "name", id = true),
                    @Result(property = "password", column = "password"),
                    @Result(property = "xp", column = "xp"),
                    @Result(property = "consecutiveCheckInDays", column = "consecutive_check-in_days"),
                    @Result(property = "lastCheckInDate", column = "last_check-in_date")
            }
    )
    @Select("SELECT * FROM users WHERE name=#{name}")
    Users selectUserMsg(@Param("name") String userName);
}

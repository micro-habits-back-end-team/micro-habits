package org.twt.microhabits.users.dao.mapper;

import org.apache.ibatis.annotations.*;
import org.twt.microhabits.users.dao.bean.UsersInfo;

@Mapper
public interface UsersInfoMapper {
//    @Results(
//            id = "usersInfoMap",
//            value = {
//                    @Result(property = "userName", column = "user_name", id = true),
//                    @Result(property = "headPortrait", column = "head_portrait"),
//            }
//    )

    @Insert("INSERT INTO users_info (user_name, head_portrait) VALUES (#{user_name}, 'Empty')")
    int userInfoInit(@Param("user_name") String userName);

    @Update("UPDATE users_info SET head_portrait=#{head_portrait} WHERE user_name=#{user_name}")
    int updateUserHeadPortrait(@Param("user_name") String userName, @Param("head_portrait") String userPortrait);

    @Results(
            id = "usersInfoMap",
            value = {
                    @Result(property = "userName", column = "user_name", id = true),
                    @Result(property = "headPortrait", column = "head_portrait"),
            }
    )
    @Select("SELECT * FROM users_info WHERE user_name=#{user_name}")
    UsersInfo selectUserInfo(@Param("user_name") String userName);

    @Select("SELECT head_portrait FROM users_info WHERE user_name=#{user_name}")
    String selectUserHeadPortrait(@Param("user_name") String userName);
}

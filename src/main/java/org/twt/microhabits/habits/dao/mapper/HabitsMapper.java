package org.twt.microhabits.habits.dao.mapper;

import org.apache.ibatis.annotations.*;
import org.twt.microhabits.habits.dao.bean.Habits;

import java.sql.Date;
import java.util.List;

@Mapper
public interface HabitsMapper {
//    @Results(
//            id = "habitsMap",
//            value = {
//                    @Result(property = "id", column = "id", id = true),
//                    @Result(property = "name", column = "name"),
//                    @Result(property = "checkInDay", column = "check-in_days"),
//                    @Result(property = "consecutiveCheckInDays", column = "consecutive_check-in_days"),
//                    @Result(property = "lastCheckInDate", column = "last_check-in_date"),
//                    @Result(property = "startDate", column = "start_date"),
//                    @Result(property = "statesNumber", column = "states_number"),
//                    @Result(property = "finishedDays", column = "finished_days"),
//                    @Result(property = "userName", column = "user_name")
//            }
//    )

    @Insert("INSERT INTO habits " +
            "(name, `check-in_days`, `consecutive_check-in_days`, `last_check-in_date`, start_date, states_number, finished_days, user_name)" +
            " VALUES (#{habit.name}, 0, 0, '1000-01-01', #{habit.startDate}, 0, 0, #{habit.userName})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertAHabit(@Param("habit") Habits habits);

    @Delete("DELETE FROM habits WHERE id=#{id}")
    int deleteAHabit(@Param("id") int habitId);

    @Update("UPDATE habits SET name=#{name} WHERE id=#{id}")
    int updateAHabitName(@Param("id") int habitId, @Param("name") String habitName);

    @Update("UPDATE habits SET `check-in_days`=`check-in_days`+1 WHERE id=#{id}")
    int addCheckInDays(@Param("id") int habitId);

    @Update("UPDATE habits SET `consecutive_check-in_days`=`consecutive_check-in_days`+1 WHERE id=#{id}")
    int addConsecutiveCheckInDays(@Param("id") int habitId);

    @Update("UPDATE habits SET `consecutive_check-in_days`=1 WHERE id=#{id}")
    int oneConsecutiveCheckInDays(@Param("id") int habitId);

    @Update("UPDATE habits SET `last_check-in_date`=#{lastCheckInDate} WHERE id=#{id}")
    int updateLastCheckInDate(@Param("id") int habitId, @Param("lastCheckInDate") String lastCheckInDate);

    @Update("UPDATE users SET `consecutive_check-in_days`=`consecutive_check-in_days`+1 WHERE name=#{userName}")
    int addUserConsecutiveCheckInDays(@Param("userName") String userName);

    @Update("UPDATE users SET `consecutive_check-in_days`=1 WHERE name=#{userName}")
    int oneUserConsecutiveCheckInDays(@Param("userName") String userName);

    @Update("UPDATE users SET `last_check-in_date`=#{lastCheckInDate} WHERE name=#{userName}")
    int updateUserLastCheckInDate(@Param("userName") String userName, @Param("lastCheckInDate") String lastCheckInDate);

    @Select("SELECT user_name FROM habits WHERE id=#{id}")
    String selectUserName(@Param("id") int habitId);

    @Select("SELECT `last_check-in_date` FROM habits WHERE id=#{id}")
    Date selectLastCheckInDate(@Param("id") int habitId);

    @Select("SELECT `last_check-in_date` FROM users WHERE name=#{userName}")
    Date selectUserLastCheckInDate(@Param("userName") String userName);

    @Select("SELECT start_date FROM habits WHERE id=#{id}")
    Date selectStartDate(@Param("id") int habitId);

    @Results(
        id = "habitsMap",
        value = {
                @Result(property = "id", column = "id", id = true),
                @Result(property = "name", column = "name"),
                @Result(property = "checkInDays", column = "check-in_days"),
                @Result(property = "consecutiveCheckInDays", column = "consecutive_check-in_days"),
                @Result(property = "lastCheckInDate", column = "last_check-in_date"),
                @Result(property = "startDate", column = "start_date"),
                @Result(property = "statesNumber", column = "states_number"),
                @Result(property = "finishedDays", column = "finished_days"),
                @Result(property = "userName", column = "user_name")
        }
    )
    @Select("SELECT * FROM habits WHERE user_name=#{userName} AND finished_days!=0" +
            " AND finished_days>=(DATEDIFF(#{today}, start_date) + 1)")
    List<Habits> selectTrainingHabits(@Param("userName") String userName, @Param("today") String today);

    @ResultMap("habitsMap")
    @Select("SELECT * FROM habits WHERE user_name=#{userName} AND finished_days!=0" +
            " AND finished_days<(DATEDIFF(#{today}, start_date) + 1)")
    List<Habits> selectTrainedHabits(@Param("userName") String userName, @Param("today") String today);

    @ResultMap("habitsMap")
    @Select("SELECT * FROM habits WHERE user_name=#{userName} AND `last_check-in_date`=#{today}")
    List<Habits> selectCheckedHabits(@Param("userName") String userName, @Param("today") String today);

    @ResultMap("habitsMap")
    @Select("SELECT * FROM habits WHERE user_name=#{userName} AND `last_check-in_date`<#{today}")
    List<Habits> selectUncheckedHabits(@Param("userName") String userName, @Param("today") String today);

    @Select("SELECT content FROM states WHERE habit_id=#{habitId} AND " +
            "start_day<=#{pastedDays} AND end_day>=#{pastedDays}")
    String selectStateContent(@Param("habitId") int habitId, @Param("pastedDays") int pastedDays);

    @Select("SELECT content FROM states WHERE habit_id=#{habitId} AND end_day=#{finishedDays}")
    String selectFinishedStateContent(@Param("habitId") int habitId, @Param("finishedDays") int finishedDays);
}

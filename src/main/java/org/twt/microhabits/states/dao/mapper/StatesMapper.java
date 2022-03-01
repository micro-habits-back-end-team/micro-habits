package org.twt.microhabits.states.dao.mapper;

import org.apache.ibatis.annotations.*;
import org.twt.microhabits.states.dao.bean.States;
import org.twt.microhabits.states.vo.StateMsg;

import java.util.List;

@Mapper
public interface StatesMapper {
    @Insert("INSERT INTO states (habit_id, content, start_day, end_day) VALUES " +
            "(#{state.habit_id},#{state.content},#{state.start_day},#{state.end_day})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertAState(@Param("state") States state);

    @Update("UPDATE states SET content=#{state.content},start_day=#{state.start_day},end_day=#{state.end_day} " +
            "WHERE id=#{state.id}")
    int updateAState(@Param("state") StateMsg state);

    @Update("UPDATE habits SET finished_days=#{finished_days} WHERE id=#{habit_id}")
    int updateFinishedDays(@Param("finished_days") int finishedDays, @Param("habit_id") int habitId);

    @Update("UPDATE habits SET states_number=#{states_number} WHERE id=#{habit_id}")
    int updateStatesNumber(@Param("states_number") int statesNumber, @Param("habit_id") int habitId);

    @Select("SELECT id FROM states WHERE habit_id=#{habit_id}")
    List<Integer> selectForId(@Param("habit_id") int habitId);

    @Select("SELECT end_day FROM states WHERE habit_id=#{habit_id}")
    List<Integer> selectForEndDay(@Param("habit_id") int habitId);

    @Select("SELECT id,content,start_day,end_day FROM states WHERE habit_id=#{habit_id}")
    List<StateMsg> selectStates(@Param("habit_id") int habitId);

    @Delete("DELETE FROM states WHERE id=#{id}")
    int deleteAState(@Param("id") int stateId);
}

package org.twt.microhabits.pictures.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.twt.microhabits.pictures.dao.bean.Pictures;

@Mapper
public interface PicturesMapper {
    @Select("SELECT * FROM pictures WHERE name=#{name}")
    Pictures selectAPicture(@Param("name") String name);
}

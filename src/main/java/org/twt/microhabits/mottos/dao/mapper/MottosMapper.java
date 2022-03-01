package org.twt.microhabits.mottos.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.twt.microhabits.mottos.dao.bean.Mottos;

@Mapper
public interface MottosMapper {
//    @Select("SELECT * FROM mottos ORDER BY RAND() LIMIT 1;")
    @Select("SELECT * FROM mottos  AS t1  JOIN (SELECT ROUND(RAND() * ((SELECT MAX(id) FROM `mottos`)-(SELECT MIN(id) FROM mottos))+(SELECT MIN(id) FROM mottos)) AS id) AS t2 WHERE t1.id >= t2.id ORDER BY t1.id LIMIT 1;")
    Mottos selectAMotto();
}

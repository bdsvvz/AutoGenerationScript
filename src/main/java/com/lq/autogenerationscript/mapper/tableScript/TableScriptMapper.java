package com.lq.autogenerationscript.mapper.tableScript;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2022-10-29 20:43
 * @Version 1.0
 */
@Mapper
public interface TableScriptMapper {

    @MapKey("id")
    List<Map<String, Object>> getTableList(Map<String, Object> params);

    Integer getTablesCount(@Param("prefix") String prefix);
}

package com.lq.autogenerationscript.mapper.sqlScript;

import com.lq.autogenerationscript.entity.KeyColumnUsage;
import com.lq.autogenerationscript.entity.SysIndexes;
import com.lq.autogenerationscript.entity.TableStructure;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2022-09-22 15:53
 * @Version 1.0
 */
@Mapper
public interface SqlScriptMapper {

    String GetProcContent(Integer procName);

    @MapKey("id")
    List<Map<String,Object>> GetProcList(String procPrefix);

    @MapKey("id")
    List<Map<String, Object>> GetTableList(String tablePrefix);

    List<TableStructure> getTableStructure(int objectId);

    List<KeyColumnUsage> getPrimaryKey(String tableName);

    int getFillFactor(int objectId);

    List<SysIndexes> getTableIndexes(int objectId);
}

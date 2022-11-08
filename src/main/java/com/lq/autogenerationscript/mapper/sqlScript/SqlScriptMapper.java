package com.lq.autogenerationscript.mapper.sqlScript;

import com.lq.autogenerationscript.entity.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2022-09-22 15:53
 * @Version 1.0
 */
@Mapper
public interface SqlScriptMapper {

    String getMssqlProcContent(Integer objectId);

    @MapKey("id")
    List<Map<String, Object>> getMssqlProcList(String procPrefix);

    @MapKey("id")
    List<Map<String, Object>> getTableList(String tablePrefix);

    List<TableStructure> getTableStructure(int objectId);

    List<KeyColumnUsage> getPrimaryKey(String tableName);

    int getFillFactor(int objectId);

    List<SysIndexes> getTableIndexes(int objectId);

    List<String> getOracleProcList(@Param("procPrefix") String procPrefix);

    String getOracleProcContent(String procName);

    List<String> getOracleTableList(String tablePrefix);

    List<OracleTableStructure> getOracleTableStructure(String tableName);

    String getOracleTableScript(String tableName);

    List<OracleSysIndexes> getOracleTableIndexes(String tableName);

    List<OracleSysPrimary> getOracleTablePrimary(String tableName);
}

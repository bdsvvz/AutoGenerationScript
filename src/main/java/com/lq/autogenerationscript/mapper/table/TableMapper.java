package com.lq.autogenerationscript.mapper.table;

import com.lq.autogenerationscript.entity.OracleTableColumnDefaultValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2023-12-28 10:55
 * @Version 1.0
 */
@Mapper
public interface TableMapper {

    List<Map> getTableColumnInfo(String tableName);

    Map<String, Object> getClustered(String tableName);

    List<Map> getNonClustered(String tableName);

    int isIncludedColumn(String tableName, String indexName);

    List<Map> getNonClusteredIncludedColumn(String tableName, String indexName);

    List<Map> getNonClusteredNotIncludedColumn(String tableName, String indexName);

    List<Map> getHasDefaultColumn(String tableName);

    String getCreateTableScript(String tableName);

    List<Map> getOracleTableColumnInfo(String tableName);

    List<Map> getOracleTableIndexInfo(String upperCase);

    /**
     * 获取sqlserver 表集合
     *
     * @param prefix 表名前缀
     * @return
     */
    List<String> getMssqlTableList(@Param("prefix") String prefix);

    /**
     * 获取oracle 表集合
     *
     * @param prefix 表名前缀
     * @return
     */
    List<String> getOracleTableList(String prefix);

    List<Map> getClusteredColumn(String tableName);

    OracleTableColumnDefaultValue getOracleTableColumnDefaultValue(String tableName, String columnName);
}

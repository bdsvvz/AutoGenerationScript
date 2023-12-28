package com.lq.autogenerationscript.mapper.table;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

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
}

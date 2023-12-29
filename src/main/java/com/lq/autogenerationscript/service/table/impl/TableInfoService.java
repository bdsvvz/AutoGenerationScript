package com.lq.autogenerationscript.service.table.impl;

import com.lq.autogenerationscript.mapper.table.TableMapper;
import com.lq.autogenerationscript.service.table.ITableInfoService;
import com.lq.autogenerationscript.utils.DbTypeUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2023-12-28 11:03
 * @Version 1.0
 */
@Service
@Slf4j
public class TableInfoService implements ITableInfoService {

    @Resource
    public TableMapper tableMapper;


    /**
     * 获取表信息
     *
     * @param tableName 表名
     */
    @Override
    public void createTableInfo(String tableName) throws IOException {
        int dbType = DbTypeUtils.getDbType();
        //创建Configuration对象
        Configuration configuration = new Configuration();
        //设置模板所在目录
        configuration.setDirectoryForTemplateLoading(new File("E:\\java-project\\AutoGenerationScript\\src\\main\\resources\\templates"));
        //获取模板
        Template template = null;
        //数据载体
        Map map = new HashMap();
        //设置数据并执行
        map.put("table_name", tableName);
        if (dbType == 1) {
            //sqlserver
            //获取模板
            template = configuration.getTemplate("createMssqlTable.ftl");
            //查询表字段信息集合
            List<Map> tableColumnInfo = tableMapper.getTableColumnInfo(tableName);
            //查询聚集索引信息
            Map<String, Object> primaryKeyInfo = tableMapper.getClustered(tableName);
            //查询非聚集索引信息集合
            List<Map> nonClusteredList = tableMapper.getNonClustered(tableName);
            //循环获取非聚集索引的列集合
            nonClusteredList.forEach(e -> {
                List<Map> nonClusteredIncludedColumnInfo = tableMapper.getNonClusteredIncludedColumn(tableName, e.get("name").toString());
                List<Map> nonClusteredNotIncludedColumnInfo = tableMapper.getNonClusteredNotIncludedColumn(tableName, e.get("name").toString());
                int includeCount = tableMapper.isIncludedColumn(tableName, e.get("name").toString());
                e.put("includeCount", includeCount);
                e.put("list1", nonClusteredIncludedColumnInfo);
                e.put("list2", nonClusteredNotIncludedColumnInfo);
            });
            List<Map> defaultColumnList = tableMapper.getHasDefaultColumn(tableName);
            map.put("tableColumnInfo", tableColumnInfo);
            map.put("primaryKeyInfo", primaryKeyInfo);
            map.put("nonClusteredList", nonClusteredList);
            map.put("defaultColumnList", defaultColumnList);
        } else {
            //获取模板
            template = configuration.getTemplate("createOracleTable.ftl");
            //获取建表语句
            String tableScript = tableMapper.getCreateTableScript(tableName.toUpperCase());
            //获取表字段信息
            List<Map> tableColumnInfo = tableMapper.getOracleTableColumnInfo(tableName.toUpperCase());
            //获取索引信息
            List<Map> tableIndexInfo = tableMapper.getOracleTableIndexInfo(tableName.toUpperCase());
            //渲染建表脚本
            map.put("data", tableScript);
            //渲染表字段信息
            map.put("tableColumnInfo", tableColumnInfo);
            //渲染索引信息
            map.put("tableIndexInfo", tableIndexInfo);
        }
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream("E:\\java-project\\AutoGenerationScript\\src\\main\\script\\table\\test.sql"));
            template.process(map, writer);
        } catch (TemplateException e) {
            log.error(e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

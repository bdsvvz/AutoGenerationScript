package com.lq.autogenerationscript.service.table.impl;

import com.lq.autogenerationscript.entity.OracleTableColumnDefaultValue;
import com.lq.autogenerationscript.mapper.table.TableMapper;
import com.lq.autogenerationscript.service.table.ITableInfoService;
import com.lq.autogenerationscript.utils.DbTypeUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
     * @param prefix 表前缀
     */
    @Override
    public void createTableInfo(String prefix) throws IOException {
//        prefix = "mhis_biz_infusion";
        int dbType = DbTypeUtils.getDbType();
        //创建Configuration对象
        Configuration configuration = new Configuration();
        //设置模板所在目录
        //生产环境
//        ClassLoader classLoader = getClass().getClassLoader();
//        URL resource = classLoader.getResource("templates");
        //开发环境
        File file = new File("src/main/resources/templates");
        String path = file.getAbsolutePath();
        log.info("path:" + path);
        configuration.setDirectoryForTemplateLoading(new File(path));
        //数据载体
        Map map = new HashMap();
        //获取模板
        Template template;
        if (dbType == 1) {
            template = configuration.getTemplate("createMssqlTable.ftl");
            generateMssqlTable(prefix, map, template);
        } else {
            //获取模板
            template = configuration.getTemplate("createOracleTable.ftl");
            generateOracleTable(prefix, map, template);
        }
        log.info("------------------------------end------------------------------");
    }


    public void generateMssqlTable(String prefix, Map map, Template template) throws IOException {
        FileWriter writer = null;
        //获取表数据集
        List<String> tableList = tableMapper.getMssqlTableList(prefix);
        try {
            for (String tableName : tableList) {
                //设置数据并执行
                map.put("table_name", tableName);
                //查询表字段信息集合
                List<Map> tableColumnInfo = tableMapper.getTableColumnInfo(tableName);
                //查询聚集索引信息
                Map<String, Object> primaryKeyInfo = tableMapper.getClustered(tableName);
                //查询聚集索引的字段名称等信息
                List<Map> primaryKeyColumnList = tableMapper.getClusteredColumn(tableName);
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
                map.put("primaryKeyColumnList", primaryKeyColumnList);
                map.put("nonClusteredList", nonClusteredList);
                map.put("defaultColumnList", defaultColumnList);
                writer = new FileWriter("E:\\java-project\\AutoGenerationScript\\src\\main\\script\\table\\test.sql", true);
                template.process(map, writer);
            }
        } catch (TemplateException e) {
            log.error(e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    public void generateOracleTable(String prefix, Map map, Template template) throws IOException {
        FileWriter writer = null;
        //获取oracle 表集合
        List<String> tableList = tableMapper.getOracleTableList(prefix);
        try {
            for (String tableName : tableList) {
                //设置数据并执行
                map.put("table_name", tableName);
                //获取建表语句
                String tableScript = tableMapper.getCreateTableScript(tableName.toUpperCase());
                //获取表字段信息
                List<Map> tableColumnInfo = tableMapper.getOracleTableColumnInfo(tableName.toUpperCase());
                for (Map<String, Object> item : tableColumnInfo) {
                    item.put("data_default", (tableMapper.getOracleTableColumnDefaultValue(tableName.toUpperCase(), item.get("column_name").toString())!=null?
                            (tableMapper.getOracleTableColumnDefaultValue(tableName.toUpperCase(), item.get("column_name").toString())).getData_default():null));
                }
                //获取索引信息
                List<Map> tableIndexInfo = tableMapper.getOracleTableIndexInfo(tableName.toUpperCase());
                //渲染建表脚本
                map.put("data", tableScript);
                //渲染表字段信息
                map.put("tableColumnInfo", tableColumnInfo);
                //渲染索引信息
                map.put("tableIndexInfo", tableIndexInfo);
                writer = new FileWriter("E:\\java-project\\AutoGenerationScript\\src\\main\\script\\table\\test.sql", true);
                template.process(map, writer);
            }
        } catch (TemplateException e) {
            log.error(e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

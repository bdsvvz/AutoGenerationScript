package com.lq.autogenerationscript.utils;

import com.lq.autogenerationscript.entity.KeyColumnUsage;
import com.lq.autogenerationscript.entity.SysIndexes;
import com.lq.autogenerationscript.entity.TableStructure;
import com.lq.autogenerationscript.mapper.sqlScript.SqlScriptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 自动生成数据库存储过程脚本（可重复执行）
 *
 * @Author: liQing
 * @Date: 2022-09-22 15:48
 * @Version 1.0
 */
@Slf4j
@Component
public class GeneraterTableUtils {

    @Resource
    SqlScriptMapper sqlScriptMapper;

    public static GeneraterTableUtils generaterTableUtils;

    @PostConstruct
    public void init() {
        generaterTableUtils = this;
        generaterTableUtils.sqlScriptMapper = this.sqlScriptMapper;
    }


    public void start() {
        String tablePrefix = "";
        //获取相关前缀开头的表集合
        List<Map<String, Object>> tableList = sqlScriptMapper.GetTableList(tablePrefix);
        //目标文件路径
        String targetPath = "E:\\java-project\\AutoGenerationScript\\target\\tableScript.sql";
        //新建文件
        File f = new File(targetPath);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            tableList.forEach((item) -> {
                getTableStructure(Integer.valueOf(item.get("object_id").toString()), item.get("name").toString(), bw);
            });
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取表的字段类型大小，是否为null
     */
    public void getTableStructure(int objectId, String tableName, BufferedWriter bw) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("/* Table:[" + tableName + "]   Create By: 自动创建表结构        */\r\n");
        stringBuffer.append("/*==============================================================*/\r\n");
//        stringBuffer.append("GO\r\n");
//        stringBuffer.append("SET ANSI_NULLS ON\r\n");
//        stringBuffer.append("GO\r\n");
//        stringBuffer.append("SET QUOTED_IDENTIFIER ON\r\n");
//        stringBuffer.append("GO\r\n");
        stringBuffer.append("if OBJECT_ID(N'" + tableName + "',N'U') is null\r\n");
        stringBuffer.append("begin\r\n");
        stringBuffer.append("CREATE TABLE [dbo].[" + tableName + "](\r\n");
        List<TableStructure> tableStructureList = sqlScriptMapper.getTableStructure(objectId);
        tableStructureList.forEach((item) -> {
            stringBuffer.append(computedCreateField(item));
        });
        //获取表的主键
        List<KeyColumnUsage> keyColumnUsages = sqlScriptMapper.getPrimaryKey(tableName);
        if (!CollectionUtils.isEmpty(keyColumnUsages)) {
            stringBuffer.append("CONSTRAINT [" + keyColumnUsages.get(0).getConstraint_name() + "] PRIMARY KEY CLUSTERED\r\n");
            stringBuffer.append("(\r\n");
            //跌倒主键索引
            KeyColumnUsage keyColumnUsage;
            Iterator<KeyColumnUsage> i = keyColumnUsages.iterator();
            while (i.hasNext()) {
                keyColumnUsage = i.next();
                if (!i.hasNext()) {
                    stringBuffer.append("[" + keyColumnUsage.getColumn_name() + "] ASC\r\n");
                } else {
                    stringBuffer.append("[" + keyColumnUsage.getColumn_name() + "] ASC,\r\n");
                }
            }
            //获取表的填充因子
            int fillFactor = sqlScriptMapper.getFillFactor(objectId);
            stringBuffer.append(")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON" + (fillFactor > 0 ? ", FILLFACTOR = " + fillFactor : "") + ") ON [PRIMARY]\r\n");

        }
        stringBuffer.append(") ON [PRIMARY]\r\n");
        stringBuffer.append("end\r\n");
        stringBuffer.append("else\r\n");
        stringBuffer.append("begin\r\n");
        tableStructureList.forEach((item) -> {
            stringBuffer.append("IF COL_LENGTH('" + tableName + "', '" + item.getColumn_name() + "') IS NULL\r\n  ");
            stringBuffer.append(computedAddField(item, tableName));
        });
        stringBuffer.append("end\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("/* Index:[" + tableName + "]   Create By: 自动创建索引        */\r\n");
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("\r\n");
        //获取索引
        List<SysIndexes> sysIndexesList = sqlScriptMapper.getTableIndexes(objectId);
        sysIndexesList.forEach(item -> {
            stringBuffer.append("if not exists(select * from sysindexes where id=object_id('" + tableName + "') and name='" + item.getName() + "')\r\n");
            stringBuffer.append("create " + item.getType_desc() + " index [" + item.getName() + "] on [" + tableName + "](" + item.getColumn_name() + " " + (item.getIs_descending_key() == 1 ? "DESC" : "ASC") + ")\r\n");
        });
        stringBuffer.append("\r\n");
        try {
            bw.write(stringBuffer.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 计算创建表字段的组成
     *
     * @param tableStructure 表字段对象
     * @return
     */
    public String computedCreateField(TableStructure tableStructure) {
        String str = "[" + tableStructure.getColumn_name() + "] ";
        str = commonSwitch(tableStructure, str);
        return str + ",\r\n";
    }

    /**
     * 计算添加表字段的组成
     *
     * @param tableStructure 表字段对象
     * @return
     */
    public String computedAddField(TableStructure tableStructure, String tableName) {
        String str = "ALTER TABLE [" + tableName + "] ADD [" + tableStructure.getColumn_name() + "] ";
        str = commonSwitch(tableStructure, str);
        return str + "\r\n";
    }

    /**
     * 方法提取
     *
     * @param tableStructure 表字段对象
     * @param str            字符串
     * @return
     */
    public String commonSwitch(TableStructure tableStructure, String str) {
        switch (tableStructure.getName().toLowerCase()) {
            case "varchar":
                str += "[VARCHAR](" + (tableStructure.getMax_length() > 0 ? tableStructure.getMax_length() : "MAX") + ")";
                break;
            case "nchar":
                str += "[NCHAR](" + (tableStructure.getMax_length() > 0 ? tableStructure.getMax_length() : "MAX") + ")";
                break;
            case "char":
                str += "[CHAR](" + (tableStructure.getMax_length() > 0 ? tableStructure.getMax_length() : "MAX") + ")";
                break;
            case "smallint":
                str += "[SMALLINT] ";
                break;
            case "datetime":
                str += "[DATETIME] ";
                break;
            case "date":
                str += "[DATE] ";
                break;
            case "numeric":
                str += "[NUMERIC](" + tableStructure.getPrecision() + ", " + tableStructure.getScale() + ") ";
                break;
            case "int":
                str += "[INT] ";
                break;
            case "nvarchar":
                str += "[NVARCHAR](" + (tableStructure.getMax_length() < 0 ? "MAX" : tableStructure.getMax_length()) + ")";
                break;
            case "image":
                str += "[IMAGE] ";
                break;
            case "bigint":
                str += "[BIGINT] " + (tableStructure.getIs_identity() == 1 ? "IDENTITY(" + tableStructure.getSeed_value() + "," + tableStructure.getIncrement_value() + ")" : " ");
                break;
            case "tinyint":
                str += "[TINYINT] ";
                break;
            case "text":
                str += "[TEXT] ";
                break;
            case "float":
                str += "[FLOAT] ";
                break;
            case "xml":
                str += "[XML] ";
                break;
            case "sysname":
                str += "[SYSNAME] ";
                break;
            case "varbinary":
                str += "[VARBINARY](" + (tableStructure.getMax_length() < 0 ? "MAX" : tableStructure.getMax_length()) + ")";
                break;
            default:
                str = "";
                break;
        }
        str += (tableStructure.getIs_nullable() == 0 ? " NOT NULL " : " NULL ") + (StringUtils.isEmpty(tableStructure.getDefinition()) ? "" : "DEFAULT " + tableStructure.getDefinition());
        return str;
    }
}

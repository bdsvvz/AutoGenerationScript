package com.lq.autogenerationscript.utils;

import com.lq.autogenerationscript.entity.*;
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


    public void start(String targetPath) {
        String tablePrefix = "mhis_";
        //目标文件路径
        targetPath = targetPath + System.currentTimeMillis() + ".sql";
        //新建文件
        File f = new File(targetPath);
        switch (DbTypeUtils.getDbType()) {
            case 1:
                getMssqlCreateTableScript(tablePrefix, f);
                break;
            case 2:
                getOracleCreateTableScript(tablePrefix, f);
                break;
            default:
                break;
        }
    }


    /**
     * 获取建表语句（oracle）
     *
     * @param tablePrefix 后缀
     * @param f           文件
     */
    public void getOracleCreateTableScript(String tablePrefix, File f) {
        //获取相关前缀开头的表集合
        List<String> tableList = sqlScriptMapper.getOracleTableList(tablePrefix);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            tableList.forEach((item) -> {
                getOracleTableStructure(item, bw);
            });
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取建表语句（sqlserver）
     *
     * @param tablePrefix 后缀
     * @param f           文件
     */
    public void getMssqlCreateTableScript(String tablePrefix, File f) {
        //获取相关前缀开头的表集合
        List<Map<String, Object>> tableList = sqlScriptMapper.getTableList(tablePrefix);
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
     * 获取表的构建语句
     */
    public void getOracleTableStructure(String tableName, BufferedWriter bw) {
        //建表语句
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("\r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("prompt  ---- Table:[" + tableName + "]   Create By: 自动创建表 \r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("\r\n");

        stringBuffer.append("DECLARE\r\n");
        stringBuffer.append("FLAG_NUM NUMBER;\r\n");
        stringBuffer.append("BEGIN\r\n");
        stringBuffer.append("SELECT COUNT(*) INTO FLAG_NUM \r\n");
        stringBuffer.append("FROM USER_TABLES ATS \r\n");
        stringBuffer.append("WHERE ATS.TABLE_NAME = UPPER('" + tableName + "'); \r\n");
        stringBuffer.append("IF FLAG_NUM = 0 THEN \r\n");
        stringBuffer.append("EXECUTE IMMEDIATE '\r\n");
        String oracleCreateTableScript = getOracleTableHandler(sqlScriptMapper.getOracleTableScript(tableName));
        stringBuffer.append(oracleCreateTableScript);
        stringBuffer.append("';\r\n");
        stringBuffer.append("END IF;\r\n");
        stringBuffer.append("END;\r\n");
        stringBuffer.append("/\r\n");

        stringBuffer.append("\r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("prompt  ---- Column:[" + tableName + "]   Create By: 自动创建字段 \r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("\r\n");

        //创建表字段
        computedOracleCreateField(tableName, stringBuffer);

        stringBuffer.append("\r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("prompt  ---- Primary:[" + tableName + "]   Create By: 自动创建主键 \r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("\r\n");

        //创建主键
        computedOracleCreatePrimary(tableName, stringBuffer);


        stringBuffer.append("\r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("prompt  ---- Index:[" + tableName + "]   Create By: 自动创建索引 \r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("\r\n");


        //创建索引
        computedOracleCreateIndex(tableName, stringBuffer);

        try {
            bw.write(stringBuffer.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * 获取表的字段类型大小，是否为null
     *
     * @param objectId  sqlserver 表唯一标识
     * @param tableName 表名
     * @param bw        字符流
     */
    public void getTableStructure(int objectId, String tableName, BufferedWriter bw) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("/* Table:[" + tableName + "]   Create By: 自动创建表结构        */\r\n");
        stringBuffer.append("/*==============================================================*/\r\n");
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
        //获取非聚集索引
        List<SysIndexes> sysIndexesList = sqlScriptMapper.getTableIndexes(objectId);
        sysIndexesList.forEach(item -> {
            stringBuffer.append("if not exists(select * from sysindexes where id=object_id('" + tableName + "') and name='" + item.getName() + "') and not exists(SELECT * FROM   INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE  TABLE_NAME = '" + tableName + "' AND COLUMN_NAME = '" + item.getColumn_name() + "')\r\n");
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
     * 计算创建主键的组成
     *
     * @param tableName 表名称
     * @return
     */
    public StringBuffer computedOracleCreatePrimary(String tableName, StringBuffer sb) {
        List<OracleSysPrimary> sysPrimaryList = sqlScriptMapper.getOracleTablePrimary(tableName);
        for (int i = 0; i < sysPrimaryList.size(); i++) {
            sb.append("\r\n");
            sb.append("prompt  ---- Index:[" + sysPrimaryList.get(i).getColumn_name() + "]   Create By: 自动创建主键 \r\n");
            sb.append("\r\n");
            sb.append("DECLARE\n");
            sb.append("FLAG_NUM NUMBER;\n");
            sb.append("BEGIN\n");
            sb.append("SELECT COUNT(*) INTO FLAG_NUM \n");
            sb.append("FROM user_ind_columns \n");
            sb.append("WHERE TABLE_NAME = UPPER('" + tableName + "') \n");
            sb.append("AND INDEX_NAME='" + sysPrimaryList.get(i).getConstraint_name() + "';\n");
            sb.append("IF FLAG_NUM < 1 THEN \n");
            sb.append("SELECT COUNT(*) INTO FLAG_NUM FROM user_ind_columns \n");
            sb.append("WHERE  TABLE_NAME = upper('" + tableName + "') AND COLUMN_NAME = upper('" + sysPrimaryList.get(i).getColumn_name() + "');\n");
            sb.append("IF FLAG_NUM < 1 THEN \n");
            sb.append("EXECUTE IMMEDIATE 'alter table " + tableName + " add constraint " + sysPrimaryList.get(i).getConstraint_name() + " primary key (" + sysPrimaryList.get(i).getColumn_name() + ")';\n");
            sb.append(" END IF;\n");
            sb.append(" END IF;\n");
            sb.append(" END;\n");
            sb.append("/\r\n");
        }
        return sb;
    }


    /**
     * 计算创建索引的组成
     *
     * @param tableName 表名称
     * @return
     */
    public StringBuffer computedOracleCreateIndex(String tableName, StringBuffer sb) {
        List<OracleSysIndexes> sysIndexesList = sqlScriptMapper.getOracleTableIndexes(tableName);
        for (int i = 0; i < sysIndexesList.size(); i++) {
            sb.append("\r\n");
            sb.append("prompt  ---- Index:[" + sysIndexesList.get(i).getIndex_name() + "]   Create By: 自动创建索引 \r\n");
            sb.append("\r\n");
            sb.append("DECLARE\n");
            sb.append("FLAG_NUM NUMBER;\n");
            sb.append("BEGIN\n");
            sb.append("SELECT COUNT(*) INTO FLAG_NUM \n");
            sb.append("FROM user_ind_columns \n");
            sb.append("WHERE TABLE_NAME = UPPER('" + tableName + "') \n");
            sb.append("AND INDEX_NAME='" + sysIndexesList.get(i).getIndex_name() + "';\n");
            sb.append("IF FLAG_NUM < 1 THEN \n");
            sb.append("SELECT COUNT(*) INTO FLAG_NUM FROM user_ind_columns \n");
            sb.append("WHERE  TABLE_NAME = upper('" + tableName + "') AND COLUMN_NAME = upper('" + sysIndexesList.get(i).getColumn_name() + "');\n");
            sb.append("IF FLAG_NUM < 1 THEN \n");
            sb.append("EXECUTE IMMEDIATE 'CREATE INDEX " + sysIndexesList.get(i).getIndex_name() + " on " + tableName + "(" + sysIndexesList.get(i).getColumn_name() + " " + sysIndexesList.get(i).getDescend() + ")';\n");
            sb.append(" END IF;\n");
            sb.append(" END IF;\n");
            sb.append(" END;\n");
            sb.append("/\r\n");
        }
        return sb;
    }


    /**
     * 计算创建表字段的组成
     *
     * @param tableName 表名称
     * @return
     */
    public StringBuffer computedOracleCreateField(String tableName, StringBuffer sb) {
        List<OracleTableStructure> oracleTableStructureList = sqlScriptMapper.getOracleTableStructure(tableName);
        for (int i = 0; i < oracleTableStructureList.size(); i++) {
            sb.append("\r\n");
            sb.append("prompt  ---- Column:[" + oracleTableStructureList.get(i).getColumn_name() + "]   Create By: 自动创建字段 \r\n");
            sb.append("\r\n");
            sb.append("DECLARE\n");
            sb.append("FLAG_NUM NUMBER;\n");
            sb.append("BEGIN\n");
            sb.append("SELECT COUNT(*) INTO FLAG_NUM \n");
            sb.append("FROM USER_TABLES ATS \n");
            sb.append("WHERE ATS.TABLE_NAME = UPPER('" + tableName + "'); \n");
            sb.append("IF FLAG_NUM > 0 THEN \n");
            sb.append("SELECT COUNT(*) INTO FLAG_NUM FROM USER_TAB_COLUMNS T \n");
            sb.append("WHERE T.TABLE_NAME = UPPER('" + tableName + "') AND T.COLUMN_NAME = UPPER('" + oracleTableStructureList.get(i).getColumn_name() + "');\n");
            sb.append("IF FLAG_NUM = 0 THEN \n");
            sb.append("EXECUTE IMMEDIATE '");
            sb.append(commonOracleSwitch(oracleTableStructureList.get(i), tableName));
            sb.append("';\n");
            sb.append(" END IF;\n");
            sb.append(" END IF;\n");
            sb.append(" END;\n");
            sb.append("/\r\n");
        }
        return sb;
    }

    /**
     * 方法提取
     *
     * @param tableStructure 表字段对象
     * @return
     */
    public String commonOracleSwitch(OracleTableStructure tableStructure, String tableName) {
        String str = "ALTER TABLE " + tableName + " ADD " + tableStructure.getColumn_name() + " ";
        switch (tableStructure.getData_type()) {
            case "VARCHAR2":
            case "CHAR":
            case "RAW":
            case "NVARCHAR2":
                str += tableStructure.getData_type() + "(" + tableStructure.getData_length() + ")";
                break;
            case "CLOB":
            case "DATE":
            case "NCLOB":
            case "LONG":
            case "BLOB":
            case "TIMESTAMP(6)":
                str += tableStructure.getData_type();
                break;
            case "NUMBER":
                str += "0".equals(tableStructure.getData_precision()) ? "INTEGER" : tableStructure.getData_type() + (StringUtils.isEmpty(tableStructure.getData_precision()) ? "" : "(" + tableStructure.getData_precision() +
                        (StringUtils.isEmpty(tableStructure.getData_scale()) ? ")" : "," + tableStructure.getData_scale() + ")"));
                break;
            default:
                str = " ";
                break;
        }
        str += (StringUtils.isEmpty(tableStructure.getData_default()) ? "" : "DEFAULT " + tableStructure.getData_default()) + (tableStructure.getNullable() == 0 ? " NOT NULL " : " NULL ");
        return str;
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
     * @param tableName      表名
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


    /**
     * oracle 建表语句截取提取（） 之间的
     *
     * @param str 原始建表语句
     * @return
     */
    public String getOracleTableHandler(String str) {
        String[] strings = str.split("");
        System.out.println(strings.length);

        int k = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if ("(".equals(strings[i])) {
                k++;
                continue;
            }
            if (")".equals(strings[i])) {
                k--;
                if (k == 0) {
                    break;
                }
                continue;
            }
        }
        return sb.toString();
    }
}

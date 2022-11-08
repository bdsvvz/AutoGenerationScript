package com.lq.autogenerationscript.utils;

import com.lq.autogenerationscript.mapper.sqlScript.SqlScriptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
public class GeneraterProcUtils {

    @Resource
    SqlScriptMapper sqlScriptMapper;

    public static GeneraterProcUtils generaterProcUtils;

    @PostConstruct
    public void init() {
        generaterProcUtils = this;
        generaterProcUtils.sqlScriptMapper = this.sqlScriptMapper;
    }


    /**
     * 将数据库读出的脚本写入对应的路径文件中(sqlserver)
     *
     * @param objectId sqlserver 标识id
     * @param procName 存储过程名称
     * @param path     路径
     */
    public void writeMssqlProcContent(Integer objectId, String procName, String path) {

        //String procName = "pm_comm_get_message";
        //读取存储过程
        String procContent = sqlScriptMapper.getMssqlProcContent(objectId);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("/* Procedure:[" + procName + "]   Create By: 自动创建        */\r\n");
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("IF ( OBJECT_ID('" + procName + "') IS NOT NULL )\r\n");
        stringBuffer.append("DROP PROCEDURE [" + procName + "];\r\n");
        stringBuffer.append("GO\r\n");
        stringBuffer.append("SET ANSI_NULLS ON\r\n");
        stringBuffer.append("GO\r\n");
        stringBuffer.append("SET QUOTED_IDENTIFIER ON\r\n");
        stringBuffer.append("GO\r\n");
        stringBuffer.append(procContent);
        stringBuffer.append("\r\n");
        stringBuffer.append("GO\r\n");
        path = path + procName + ".sql";
        //新建文件
        File f = new File(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            bw.write(stringBuffer.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将数据库读出的脚本写入对应的路径文件中(oracle)
     *
     * @param procName 存储过程名称
     * @param path     路径
     */
    public void writeOracleProcContent(String procName, String path) {
        //String procName = "pm_comm_get_message";
        //读取存储过程
        String procContent = sqlScriptMapper.getOracleProcContent(procName);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("prompt  -----------------Procedure:[" + procName + "]   Create By: 自动创建 \r\n");
        stringBuffer.append("prompt  -------------------------------\r\n");
        stringBuffer.append("/**\n");
        stringBuffer.append("*  作者：邹德友\n");
        stringBuffer.append("*  时间：10-25 09:22:10\n");
        stringBuffer.append("*  描述：创建存储过程【" + procName + "】\n");
        stringBuffer.append("*  脚本可重复执行\n");
        stringBuffer.append("*/\n");
        /**
         *  作者：zkongbai
         *  时间：11-21 09:22:10
         *  描述：创建表【双随机任务,序:SEQ_ZZ_DOUBLE_RANDOM_TASK】
         *  脚本可重复执行
         */
        stringBuffer.append("CREATE OR REPLACE ");
        stringBuffer.append(procContent);
        stringBuffer.append("\r\n");
        stringBuffer.append("/");
        path = path + procName + ".sql";
        //新建文件
        File f = new File(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            bw.write(stringBuffer.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * sqlserver 存储过程脚本
     *
     * @param procPrefix 前缀
     * @param targetPath 路径
     */
    public void generateMssqlProc(String procPrefix, String targetPath) {
        List<Map<String, Object>> prcoList = sqlScriptMapper.getMssqlProcList(procPrefix);
        //目标文件路径
        prcoList.forEach((item) -> {
            if (item.get("name").toString().indexOf(".") < 0 && item.get("name").toString().indexOf("_bak") < 0) {
                writeMssqlProcContent(Integer.valueOf(item.get("object_id").toString()), item.get("name").toString(), targetPath);
            }
        });
    }


    /**
     * oracle 存储过程脚本
     *
     * @param procPrefix 前缀
     * @param targetPath 路径
     */
    public void generateOracleProc(String procPrefix, String targetPath) {
        List<String> prcoList = sqlScriptMapper.getOracleProcList(procPrefix);
        //目标文件路径
        prcoList.forEach((item) -> {
            if (item.indexOf(".") < 0 && item.indexOf("_bak") < 0) {
                writeOracleProcContent(item, targetPath);
            }
        });
    }


    /**
     * 自动创建存储过程脚本
     *
     * @param targetPath 路径
     */
    public void generateProc(String targetPath) {
        String procPrefix = "pm_";
        switch (DbTypeUtils.getDbType()) {
            case 1:
                generateMssqlProc(procPrefix, targetPath);
                break;
            case 2:
                generateOracleProc(procPrefix, targetPath);
                break;
            default:
                break;
        }
    }


}

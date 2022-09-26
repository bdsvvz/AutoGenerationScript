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


    public void start() {
        Integer procName = 1;
        //读取存储过程
        String procContent = sqlScriptMapper.GetProcContent(procName);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("/* Procedure:" + procName + "   Create By: 自动创建        */\r\n");
        stringBuffer.append("/*==============================================================*/\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("IF ( OBJECT_ID('" + procName + "') IS NOT NULL )\r\n");
        stringBuffer.append("DROP PROCEDURE " + procName + ";\r\n");
        stringBuffer.append("GO\r\n");
        stringBuffer.append("SET ANSI_NULLS ON\r\n");
        stringBuffer.append("GO\r\n");
        stringBuffer.append("SET QUOTED_IDENTIFIER ON\r\n");
        stringBuffer.append("GO\r\n");
        stringBuffer.append(procContent);
        stringBuffer.append("\r\n");
        stringBuffer.append("GO\r\n");
        System.out.println(stringBuffer);
    }

    public void start(Integer objectId, String procName, File f, BufferedWriter bw) {
        //String procName = "pm_comm_get_message";
        //读取存储过程
        String procContent = sqlScriptMapper.GetProcContent(objectId);
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
        try {
            bw.write(stringBuffer.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void GeneraterProc(String targetPath) {
        String procPrefix = "pm_";
        List<Map<String, Object>> prcoList = sqlScriptMapper.GetProcList(procPrefix);
        //目标文件路径
        targetPath = targetPath + System.currentTimeMillis() + ".sql";
        //新建文件
        File f = new File(targetPath);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            BufferedWriter finalBw = bw;
            prcoList.forEach((item) -> {
                if (item.get("name").toString().indexOf(".") < 0 && item.get("name").toString().indexOf("_bak") < 0) {
                    start(Integer.valueOf(item.get("object_id").toString()), item.get("name").toString(), f, finalBw);
                    //System.out.println(item.get("name").toString());
                }

            });
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

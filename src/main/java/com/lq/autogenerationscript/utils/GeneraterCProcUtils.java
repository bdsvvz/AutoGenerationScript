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
import java.util.*;

/**
 * 自动生成数据库存储过程脚本C# 模板（
 *
 * @Author: liQing
 * @Date: 2022-09-22 15:48
 * @Version 1.0
 */
@Slf4j
@Component
public class GeneraterCProcUtils {

    @Resource
    SqlScriptMapper sqlScriptMapper;

    public static GeneraterCProcUtils generaterProcUtils;

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
        String procContent = "create or replace " + sqlScriptMapper.getOracleProcContent(procName);

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("using Ics.Database.Update.Base.Attribute;\n" +
                "using Ics.Database.Update.Base.DBHelper;\n" +
                "using Ics.Database.Update.Base.Interface;\n" +
                "using System;\n" +
                "using System.Collections.Generic;\n" +
                "using System.Linq;\n" +
                "using System.Text;\n" +
                "using System.Threading.Tasks;\n" +
                "\n" +
                "namespace Ics.Database.Update.MHis6.Procedure\n" +
                "{");

        stringBuffer.append("[Procedure(\"" + procName + "\", \"" + "" + "\", \"蒋富林，李昭，车强\", \"2022-10-31 13:38:13\")]\n" +
                "    public class " + procName + " : IProcedure\n" +
                "    {\n" +
                "        public string ID => \"{" + getUuid() + "}\";\n" +
                "\n" +
                "        private readonly string createSql = string.Empty;\n" +
                "        public string CreateSql => createSql;\n" +
                "\n" +
                "        public " + procName + "()\n" +
                "        {\n" +
                "            createSql = HISDBHelper.HisDBType switch\n" +
                "            {");
        stringBuffer.append("HISDBType.ORACLE => @\"");
        stringBuffer.append(getProcRelease(procContent, procName));
        stringBuffer.append("\",");

        stringBuffer.append("  _ => @\"\",\n" +
                "            };\n" +
                "        }\n" +
                "    }\n" +
                "}");
        path = path + procName + ".cs";
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
     * 转换存储过程内容
     *
     * @param content
     * @return
     */
    public String getProcRelease(String content, String procName) {
        content = content.replaceAll("\"", "\"\"");
        return content;
    }

    /**
     * 获取存储过程描述
     *
     * @param content 存储过程内容
     * @return
     */
    public String getProcRemark(String content) {
        content = content.substring(content.indexOf("描述：") + 3);
        content = content.substring(0, content.indexOf("\n"));
        return content;
    }

    /**
     * 获取随机码
     *
     * @return
     */
    public String getUuid() {
        return UUID.randomUUID().toString().toUpperCase();
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


    public Set<String> getProcs() {
        String procStr = "PROC_OP_LIMIT_STORED;\n" +
                "PROC_RECHARGE_CHECK;\n" +
                "PROC_OP_BEFORE_RECHARGE;\n" +
                "PROC_OP_FEE_NO_DEDUCTION;\n" +
                "PROC_OP_GET_REGISTERTYPE;\n" +
                "PROC_OP_GET_CLINIC;\n" +
                "PROC_OP_GET_CLINIC_NEW;\n" +
                "PROC_OP_GET_REGISTERDEPT;\n" +
                "PROC_OP_TRANSFERDATA;\n" +
                "PROC_OP_CALC_BALANCE;\n" +
                "PROC_CARD_CHECK_INFO;\n" +
                "PROC_OP_BEFORE_REGISTER;\n" +
                "PROC_OP_REGISTER_LIMIT_NEW;\n" +
                "PROC_OP_REGISTER_LIMIT;\n" +
                "PROC_OP_AFTER_CHARGE;\n" +
                "PROC_OP_QUEUENO_LOCK;\n" +
                "PROC_VIS_CHANGE_BALANCE;\n" +
                "PROC_OP_REGISTER_GET_TIMEID;\n" +
                "PROC_OP_REGISTER_GETORDERNO;\n" +
                "PROC_APPTORREG_GET_QUEUENO;\n" +
                "PROC_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_NEW;\n" +
                "PROC_BILL_QUERY;\n" +
                "PROC_RECHARGE;\n" +
                "PROC_OP_GET_CHARGEDETAIL;\n" +
                "PROC_OP_GET_CHARGESUMMARY;\n" +
                "PROC_OP_REGISTER_GET_ROOM;\n" +
                "PROC_PAYMENT_UPDATE_STATUS;\n" +
                "PROC_LOG;\n" +
                "PROC_OP_AFTER_CHARGE;\n" +
                "PROC_VIS_CHANGE_BALANCE;\n" +
                "PROC_OP_CHANGE_AV_STORE_DRUG;\n" +
                "PROC_OP_CHANGE_AV_STORE_MAT;\n" +
                "PROC_SPD_CHARGE_REDUCE_STORE;\n" +
                "PROC_SPD_CHARGE_REDUCE_REVOKE;\n" +
                "PROC_SPD_REFUND_ADD_STORE;\n" +
                "PROC_OP_GET_CHARGEDICT;\n" +
                "PROC_SPD_GET_MATERIAL_INFO;\n" +
                "PROC_OP_GET_TEMPLET;\n" +
                "PROC_OP_GET_GROUP_TEMPLET;\n" +
                "PROC_DRUG_GET_EARLIEST_EXPIRED;\n" +
                "PROC_MATERIAL_GET_EARLIEST_EXP;\n" +
                "PROC_OP_PRINT_DATA_RECORD;\n" +
                "PROC_OP_BEFORE_REGISTER;\n" +
                "PROC_OP_REGISTER_LIMIT_NEW;\n" +
                "PROC_OP_REGISTER_LIMIT;\n" +
                "PROC_OP_PRINT_SUMRCPT_CHECK;\n" +
                "PROC_OP_PRINT_SUMRCPT_GETCHGDT;\n" +
                "PROC_OP_CHARGE_BEFORESAVE;\n" +
                "PROC_OP_GET_CHARGEDETAIL;\n" +
                "PROC_OP_GET_CHARGESUMMARY;\n" +
                "PROC_OP_GET_CHARGELIST;\n" +
                "PROC_OP_TRANSFERDATA;\n" +
                "PROC_OP_CALC_BALANCE;\n" +
                "PROC_OP_GET_LATEST_VISIT_INFO;\n" +
                "PROC_CARD_CHECK_INFO;\n" +
                "PROC_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_UPDATE_STATUS;\n" +
                "PROC_OP_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_NEW;\n" +
                "PROC_LOG;\n" +
                "PROC_OP_BEFORE_RECHARGE;\n" +
                "PROC_OP_TREATE_DELETE;\n" +
                "PROC_BILL_QUERY;\n" +
                "PROC_RECHARGE;\n" +
                "PROC_OP_GET_CLINIC;\n" +
                "PROC_SI_LIMIT_ITEM;\n" +
                "PROC_OP_AFTER_CHARGE;\n" +
                "PROC_VIS_CHANGE_BALANCE;\n" +
                "PROC_OP_CHANGE_AV_STORE_DRUG;\n" +
                "PROC_OP_CHANGE_AV_STORE_MAT;\n" +
                "PROC_SPD_CHARGE_REDUCE_STORE;\n" +
                "PROC_SPD_CHARGE_REDUCE_REVOKE;\n" +
                "PROC_SPD_REFUND_ADD_STORE;\n" +
                "PROC_OP_GET_CHARGEDICT;\n" +
                "PROC_SPD_GET_MATERIAL_INFO;\n" +
                "PROC_OP_GET_TEMPLET;\n" +
                "PROC_OP_GET_GROUP_TEMPLET;\n" +
                "PROC_DRUG_GET_EARLIEST_EXPIRED;\n" +
                "PROC_MATERIAL_GET_EARLIEST_EXP;\n" +
                "PROC_OP_PRINT_DATA_RECORD;\n" +
                "PROC_OP_BEFORE_REGISTER;\n" +
                "PROC_OP_REGISTER_LIMIT_NEW;\n" +
                "PROC_OP_REGISTER_LIMIT;\n" +
                "PROC_OP_PRINT_SUMRCPT_CHECK;\n" +
                "PROC_OP_PRINT_SUMRCPT_GETCHGDT;\n" +
                "PROC_OP_CHARGE_BEFORESAVE;\n" +
                "PROC_OP_GET_CHARGEDETAIL;\n" +
                "PROC_OP_GET_CHARGESUMMARY;\n" +
                "PROC_OP_GET_CHARGELIST;\n" +
                "PROC_OP_TRANSFERDATA;\n" +
                "PROC_OP_CALC_BALANCE;\n" +
                "PROC_OP_GET_LATEST_VISIT_INFO;\n" +
                "PROC_CARD_CHECK_INFO;\n" +
                "PROC_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_UPDATE_STATUS;\n" +
                "PROC_OP_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_NEW;\n" +
                "PROC_LOG;\n" +
                "PROC_OP_BEFORE_RECHARGE;\n" +
                "PROC_OP_TREATE_DELETE;\n" +
                "PROC_BILL_QUERY;\n" +
                "PROC_RECHARGE;\n" +
                "PROC_OP_GET_CLINIC;\n" +
                "PROC_SI_LIMIT_ITEM;\n" +
                "PROC_OP_GET_STORE_DEPTDRUG;\n" +
                "PROC_OP_AFTER_CHARGE;\n" +
                "PROC_VIS_CHANGE_BALANCE;\n" +
                "PROC_OP_CHANGE_AV_STORE_DRUG;\n" +
                "PROC_OP_CHANGE_AV_STORE_MAT;\n" +
                "PROC_SPD_CHARGE_REDUCE_STORE;\n" +
                "PROC_SPD_CHARGE_REDUCE_REVOKE;\n" +
                "PROC_SPD_REFUND_ADD_STORE;\n" +
                "PROC_OP_GET_CHARGEDICT;\n" +
                "PROC_SPD_GET_MATERIAL_INFO;\n" +
                "PROC_OP_GET_TEMPLET;\n" +
                "PROC_OP_GET_GROUP_TEMPLET;\n" +
                "PROC_DRUG_GET_EARLIEST_EXPIRED;\n" +
                "PROC_MATERIAL_GET_EARLIEST_EXP;\n" +
                "PROC_OP_PRINT_DATA_RECORD;\n" +
                "PROC_OP_BEFORE_REGISTER;\n" +
                "PROC_OP_REGISTER_LIMIT_NEW;\n" +
                "PROC_OP_REGISTER_LIMIT;\n" +
                "PROC_OP_PRINT_SUMRCPT_CHECK;\n" +
                "PROC_OP_PRINT_SUMRCPT_GETCHGDT;\n" +
                "PROC_OP_CHARGE_BEFORESAVE;\n" +
                "PROC_OP_GET_CHARGEDETAIL;\n" +
                "PROC_OP_GET_CHARGESUMMARY;\n" +
                "PROC_OP_GET_CHARGELIST;\n" +
                "PROC_OP_TRANSFERDATA;\n" +
                "PROC_OP_CALC_BALANCE;\n" +
                "PROC_OP_GET_LATEST_VISIT_INFO;\n" +
                "PROC_CARD_CHECK_INFO;\n" +
                "PROC_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_UPDATE_STATUS;\n" +
                "PROC_OP_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_NEW;\n" +
                "PROC_LOG;\n" +
                "PROC_OP_BEFORE_RECHARGE;\n" +
                "PROC_OP_TREATE_DELETE;\n" +
                "PROC_BILL_QUERY;\n" +
                "PROC_RECHARGE;\n" +
                "PROC_OP_GET_CLINIC;\n" +
                "PROC_SI_LIMIT_ITEM;\n" +
                "PROC_OP_CHANGE_AV_STORE_DRUG;\n" +
                "PROC_OP_CHANGE_AV_STORE_MAT;\n" +
                "PROC_OP_RETURN_CASH;\n" +
                "PROC_VIS_CHANGE_BALANCE;\n" +
                "PROC_OP_AFTER_REFUND;\n" +
                "PROC_OP_REFUND_BEFORESAVE;\n" +
                "proc_op_after_charge;\n" +
                "proc_vis_change_balance;\n" +
                "proc_op_change_av_store_drug;\n" +
                "proc_op_change_av_store_mat;\n" +
                "PROC_SPD_CHARGE_REDUCE_STORE;\n" +
                "PROC_SPD_CHARGE_REDUCE_REVOKE;\n" +
                "PROC_SPD_REFUND_ADD_STORE;\n" +
                "proc_op_get_chargedict;\n" +
                "PROC_SPD_GET_MATERIAL_INFO;\n" +
                "proc_op_get_templet;\n" +
                "proc_op_get_group_templet;\n" +
                "proc_drug_get_earliest_expired;\n" +
                "proc_material_get_earliest_exp;\n" +
                "proc_op_print_data_record;\n" +
                "proc_op_before_register;\n" +
                "proc_op_register_limit_new;\n" +
                "proc_op_register_limit;\n" +
                "proc_op_print_sumrcpt_check;\n" +
                "proc_op_print_sumrcpt_getchgdt;\n" +
                "PROC_OP_CHARGE_BEFORESAVE;\n" +
                "proc_op_get_chargedetail;\n" +
                "proc_op_get_chargesummary;\n" +
                "proc_op_get_chargelist;\n" +
                "proc_op_transferdata;\n" +
                "proc_op_calc_balance;\n" +
                "proc_op_get_latest_visit_info;\n" +
                "PROC_CARD_CHECK_INFO;\n" +
                "PROC_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_UPDATE_STATUS;\n" +
                "PROC_OP_AFTER_RECHARGE;\n" +
                "PROC_PAYMENT_NEW;\n" +
                "PROC_LOG;\n" +
                "proc_op_before_recharge;\n" +
                "PROC_OP_TREATE_DELETE;\n" +
                "PROC_BILL_QUERY;\n" +
                "PROC_RECHARGE;\n" +
                "proc_op_get_clinic;\n" +
                "proc_si_limit_item;\n" +
                "proc_infusion_recipe_execute;\n" +
                "proc_infusion_recipe_delete;\n" +
                "proc_infusion_skin_test_exec;\n" +
                "proc_op_print_sumrcpt_check;\n" +
                "proc_op_print_sumrcpt_getchgdt;\n" +
                "proc_op_get_chargedetail;\n" +
                "proc_op_get_chargesummary;\n" +
                "proc_op_register_get_room;\n" +
                "proc_op_get_chargelist;\n" +
                "proc_op_return_cash;\n" +
                "proc_op_get_postsettle_charge;\n" +
                "proc_op_get_postsettle_charge_ljde300;\n" +
                "proc_op_checkout;proc_si_personal_icd_list_new;\n" +
                "proc_si_personal_icd_list;\n" +
                "proc_op_get_skin_test_result;\n" +
                "proc_op_get_deptdrug_fordoctor;\n" +
                "proc_op_recipe_newrecord;\n" +
                "proc_op_get_drug_warn;\n" +
                "proc_op_change_av_store_drug_d;\n" +
                "proc_op_change_av_store_mat_d;\n" +
                "PROC_OP_AFTER_SPLITRECIPE;\n" +
                "proc_op_recipe_before_save;\n" +
                "PROC_OP_RECIPE_AFTER_AFFIRM;\n" +
                "proc_op_recipe_affirm_cancel;\n" +
                "proc_op_estimate_money;\n" +
                "proc_op_change_av_store_temp_d;\n" +
                "proc_op_ch_av_store_drug_d_new;\n" +
                "proc_op_ch_av_store_mat_d_new;\n" +
                "proc_op_recipe_create_check;\n" +
                "PROC_SPD_CHARGE_REDUCE_STORE;\n" +
                "PROC_SPD_CHARGE_REDUCE_REVOKE;\n" +
                "PROC_OP_GET_RECIPE_ICD;\n" +
                "proc_recipe_diagnosis;\n" +
                "proc_si_limit_item;\n" +
                "PROC_OP_RE_INP_CHECK;\n" +
                "PROC_OP_GET_VITAL_SIGNS;\n" +
                "proc_op_get_insp_templet_dept;\n" +
                "PROC_OP_GET_POSITION_INFO;\n" +
                "proc_si_personal_icd_list_new;\n" +
                "proc_si_personal_icd_list;\n" +
                "proc_op_inspection_after_audit;\n" +
                "proc_charge_merge;\n" +
                "PROC_OP_RECIPE_AUTO_NEW_CHARGE;\n" +
                "proc_op_inspection_before_audit;\n" +
                "PROC_OP_SPECIAL_TEMPLET_CHECK;\n" +
                "proc_check_templet_right;\n" +
                "proc_op_recipe_before_save;\n" +
                "PROC_OP_RECIPE_AFTER_AFFIRM;\n" +
                "proc_op_recipe_affirm_cancel;\n" +
                "proc_op_estimate_money;\n" +
                "proc_op_change_av_store_drug_d;\n" +
                "proc_op_change_av_store_mat_d;\n" +
                "proc_op_change_av_store_temp_d;\n" +
                "proc_op_recipe_newrecord;\n" +
                "proc_op_ch_av_store_drug_d_new;\n" +
                "proc_op_ch_av_store_mat_d_new;\n" +
                "proc_op_recipe_create_check;\n" +
                "PROC_SPD_CHARGE_REDUCE_STORE;\n" +
                "PROC_SPD_CHARGE_REDUCE_REVOKE;\n" +
                "PROC_OP_GET_RECIPE_ICD;\n" +
                "proc_recipe_diagnosis;\n" +
                "PROC_OP_TREAT_AFTER_AFFIRM;\n" +
                "PROC_OP_TREATE_DELETE;\n" +
                "proc_ob_change_bed;\n" +
                "proc_ob_reg_cancel;\n" +
                "proc_ob_update_observego;\n" +
                "proc_ob_get_all_visit_info;\n" +
                "proc_ob_get_bed;\n" +
                "proc_ob_leave";
        procStr = procStr.replaceAll("\n", "").toUpperCase();
        List<String> procList = new ArrayList<>();
        Set<String> procSet = new HashSet<>();
        procList = Arrays.asList(procStr.split(";"));
        procList.forEach((item) -> {
            procSet.add(item);
        });
        return procSet;
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

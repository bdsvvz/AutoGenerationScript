package com.lq.autogenerationscript.utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 获取his的表名以及存储过程名称
 *
 * @Author: liQing
 * @Date: 2022-10-20 9:56
 * @Version 1.0
 */
public class GetHisTableAndProcUtils {

    /**
     * 正则匹配以PROC_开头字符串
     */
    private static String REGEX_PROC = " proc_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以P_开头字符串
     */
    private static String REGEX_P = " p_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DBO_BIZ开头表名
     */
    private static String REGEX_DBO_BIZ = " dbo.biz_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以BIZ开头表名
     */
    private static String REGEX_BIZ = " biz_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DBO_DICT开头表名
     */
    private static String REGEX_DBO_DICT = " dbo.dict_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DICT开头表名
     */
    private static String REGEX_DICT = " dict_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DBO_EMR开头表名
     */
    private static String REGEX_DBO_EMR = " dbo.emr_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以EMR开头表名
     */
    private static String REGEX_EMR = " emr_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DBO_ENR开头表名
     */
    private static String REGEX_DBO_ENR = " dbo.enr_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以ENR开头表名
     */
    private static String REGEX_ENR = " enr_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DBO_DRUG开头表名
     */
    private static String REGEX_DBO_DRUG = " dbo.drug_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DRUG开头表名
     */
    private static String REGEX_DRUG = " drug_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DBO_DN开头表名
     */
    private static String REGEX_DBO_DN = " dbo.dn_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以DN开头表名
     */
    private static String REGEX_DN = " dn_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以view开头的视图
     */
    private static String REGEX_VIEW = " view_([\\s\\S]*?)(\\S+)";
    /**
     * 正则匹配以dbo.view开头的视图
     */
    private static String REGEX_DBO_VIEW = " dbo.view_([\\s\\S]*?)(\\S+)";
    /**
     * 剔除存储过程中文以及特殊字符
     */
    private static String REGEX_CHINESE = "[\u4e00-\u9fa5\\-/+?!！'*@#:%\\[\\]‘\\\\${}^|~\\n\\r\\t]";


    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
//        InputStream is = new FileInputStream(filePath);
        InputStreamReader is = new InputStreamReader(new FileInputStream(filePath), "GB2312");
        // 用来保存每行读取的内容
        String line;
        BufferedReader reader = new BufferedReader(is);
        // 读取第一行
        line = reader.readLine();
        // 如果 line 为空说明读完了
        while (line != null) {
            // 将读到的内容添加到 buffer 中
            buffer.append(line);
            // 添加换行符
            buffer.append("\n");
            // 读取下一行
            line = reader.readLine();
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     *
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     */
    public static String readFile(String filePath) throws IOException {
        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);
        return sb.toString();
    }

    /**
     * 正则提取
     *
     * @param text  原文
     * @param regex 正则模式
     * @return 匹配的正则文字数组
     */
    public static Set<String> regularExtraction(String text, String regex, String regex1) {
        //1.创建匹配模式
        Set<String> strs = new HashSet<>();
        //匹配一个或多个数字字符
        Pattern pattern = Pattern.compile(regex);
        //2.选择匹配对象
        Matcher matcher = pattern.matcher(text);
        //与谁匹配？与参数字符串str匹配
        //matcher.find()用于查找是否有这个字符，有的话返回true
        String string;
        while (matcher.find()) {
            string = matcher.group().replaceAll(REGEX_CHINESE, "");
            if (regex1 != null) {
                string = string.replaceAll(regex1, "");
            }
            int startIndex;
            startIndex = string.indexOf("(");
            if (startIndex > 0) {
                string = string.substring(0, startIndex);
            }
            startIndex = string.indexOf(".");
            if (startIndex > 0) {
                string = string.substring(0, startIndex);
            }
            strs.add(string);
        }
        return strs;
    }

    /**
     * 获取路径下所有文件
     *
     * @param path 路径
     */
    public static List<Path> getAllFile(String path) {
        List<Path> fileNames = null;
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            fileNames = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }


    /**
     * 打印输出
     *
     * @param sets
     * @param title
     */
    public static void printOut(Set<String> sets, String title) {
        System.out.println("---------------------开始输出" + title + "-----------------------");
        for (String s : sets) {
            System.out.println(s);
        }
        System.out.println("---------------------结束输出" + title + "-----------------------");
    }

    public static void main(String[] args) throws IOException {
        //读取目录下的每个文件或者文件夹，并读取文件的内容写到目标文字中去
        File[] fyList = new File("F:\\Android\\移动医护脚本整理\\移动医护脚本\\阜阳二院\\script\\proc").listFiles();
        File[] ljList = new File("F:\\Android\\移动医护脚本整理\\移动医护脚本\\牡丹江\\script\\proc").listFiles();
        File[] qjList = new File("F:\\Android\\移动医护脚本整理\\移动医护脚本\\全椒县人民医院\\script\\proc").listFiles();
        List<File> list = new ArrayList<>();
        List<Path> paths = GetHisTableAndProcUtils.getAllFile("F:\\Android\\src\\mhis6server\\src\\main\\java\\com\\icreate\\mhis6server\\mapper");
        for (Path p : paths) {
            if (p.getFileName().toString().indexOf(".xml") > 0) {
                File f = new File(p.getParent() + File.separator + p.getFileName());
                list.add(f);
            }
        }
        list.addAll(Arrays.asList(fyList));
        list.addAll(Arrays.asList(ljList));
        list.addAll(Arrays.asList(qjList));
        String content;
        Set<String> viewSets = new HashSet<>();
        Set<String> procSets = new HashSet<>();
        Set<String> tableSets = new HashSet<>();
        for (File f : list) {
            content = readFile(f.getPath());
            // 匹配HIS存储过程(proc_和p_)
            procSets.addAll(regularExtraction(content, REGEX_PROC, null));
            procSets.addAll(regularExtraction(content, REGEX_P, null));
            // 匹配HIS表(biz)
            tableSets.addAll(regularExtraction(content, REGEX_BIZ, null));
            tableSets.addAll(regularExtraction(content, REGEX_DBO_BIZ, "dbo."));
            // 匹配HIS表(dict)
            tableSets.addAll(regularExtraction(content, REGEX_DICT, null));
            tableSets.addAll(regularExtraction(content, REGEX_DBO_DICT, "dbo."));
            // 匹配HIS表(emr)
            tableSets.addAll(regularExtraction(content, REGEX_EMR, null));
            tableSets.addAll(regularExtraction(content, REGEX_DBO_EMR, "dbo."));
            // 匹配HIS表(enr)
            tableSets.addAll(regularExtraction(content, REGEX_ENR, null));
            tableSets.addAll(regularExtraction(content, REGEX_DBO_ENR, "dbo."));
            // 匹配HIS表(drug)
            tableSets.addAll(regularExtraction(content, REGEX_DRUG, null));
            tableSets.addAll(regularExtraction(content, REGEX_DBO_DRUG, "dbo."));
            // 匹配HIS表(dn)
            tableSets.addAll(regularExtraction(content, REGEX_DN, null));
            tableSets.addAll(regularExtraction(content, REGEX_DBO_DN, "dbo."));
            //匹配HIS视图
            viewSets.addAll(regularExtraction(content, REGEX_DBO_VIEW, "dbo."));
            viewSets.addAll(regularExtraction(content, REGEX_VIEW, null));
        }
        //打印输出
        printOut(procSets, "存储过程");
        printOut(viewSets, "视图");
        printOut(tableSets, "表");
    }
}

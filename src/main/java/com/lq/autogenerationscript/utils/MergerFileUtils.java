package com.lq.autogenerationscript.utils;

import org.springframework.util.StringUtils;

import java.io.*;

import java.nio.ByteBuffer;

import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: liQing
 * @Date: 2022-10-11 13:46
 * @Version 1.0
 */
public class MergerFileUtils {

    public static boolean mergeFiles(String[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1 || StringUtils.isEmpty(resultPath)) {
            return false;
        }
        if (fpaths.length == 1) {
            return new File(fpaths[0]).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.length];
        for (int i = 0; i < fpaths.length; i++) {
            files[i] = new File(fpaths[i]);
            if (StringUtils.isEmpty(fpaths[i]) || !files[i].exists()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);

        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < fpaths.length; i++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }


        return true;
    }

    public static void main(String[] args) throws IOException {
//定义输出目录
        String FileOut = "C:\\Users\\LQ\\Desktop\\merge.sql";
        BufferedWriter bw = new BufferedWriter(new FileWriter(FileOut));
//读取目录下的每个文件或者文件夹，并读取文件的内容写到目标文字中去
        File[] list = new File("F:\\Android\\移动医护脚本整理\\移动医护脚本\\相同存储过程脚本").listFiles();
        List<String> strs = new ArrayList<>();
        for (File f : list) {
            strs.add(f.getPath());
        }
        if (mergeFiles(strs.toArray(new String[strs.size()]), FileOut)) {
            System.out.println("合并完成");
        } else {
            System.out.println("合并失败");
        }
    }
}

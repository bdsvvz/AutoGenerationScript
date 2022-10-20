package com.lq.autogenerationscript;

import com.lq.autogenerationscript.utils.GetHisTableAndProcUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @Author: liQing
 * @Date: 2022-10-20 14:23
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTest {

    @Test
    public void Test() {
        List<Path> paths = GetHisTableAndProcUtils.getAllFile("F:\\Android\\src\\mhis6server\\src\\main\\java\\com\\icreate\\mhis6server\\mapper");
        for (Path p : paths) {
            System.out.println(p.getParent() + File.separator + p.getFileName());
        }
    }
}

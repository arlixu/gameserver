package com.source3g.platform.utils;

import com.source3g.platform.dto.PlayerConf;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by huhuaiyong on 2017/9/11.
 * 参赛者工具类
 */
public class PlayerUtils {
    /**
     * 从参赛者配置文件获取配置的端口
     * @param file
     * @return
     */
    public static PlayerConf initFromFile(File file){
        String baseUrl = "http://127.0.0.1:8082";
        String[] part = null;
        try {
            Properties pro = new Properties();
            FileInputStream in = new FileInputStream(file);
            pro.load(in);
            in.close();
            baseUrl = pro.getProperty("baseUrl");
            part = pro.getProperty("part").trim().split(",");
        }catch (IOException e){

        }

        return PlayerConf.builder().baseUrl(baseUrl).part(part).build();
    }
}

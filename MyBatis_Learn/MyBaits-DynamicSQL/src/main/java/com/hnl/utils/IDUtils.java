package com.hnl.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

//生成随机ID的工具类
public class IDUtils {
    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}

package com.usth;

import com.usth.utils.DateTimeUtil;
import com.usth.utils.MD5Util;
import com.usth.utils.UUIDUtil;

import java.util.Date;

public class Test {

    @org.junit.Test
    public void Test01() {
//        String expireTime = "2021-10-27 10:10:10";
//        String nowTime = DateTimeUtil.getSysTime();
//        int res = expireTime.compareTo(nowTime); <0
//        int res = nowTime.compareTo(expireTime); >0
//        System.out.println(res);
//        String ip = "192.168.2.1";
//        String allowIp = "192.168.2.1,192.168.1.1";
//        if(allowIp.contains(ip)){
//            System.out.println("允许访问");
//        }else {
//            System.out.println("ip地址受限");
//        }
        String res = MD5Util.getMD5("#@￥sdfA$%-[234SFsf1''");
        System.out.println(res);
    }
}

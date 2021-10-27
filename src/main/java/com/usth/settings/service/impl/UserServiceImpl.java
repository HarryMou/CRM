package com.usth.settings.service.impl;

import com.usth.exception.LoginException;
import com.usth.settings.dao.UserDao;
import com.usth.settings.domain.User;
import com.usth.settings.service.UserService;
import com.usth.utils.DateTimeUtil;
import jdk.internal.org.objectweb.asm.Handle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);
        if(null==user){
            throw new LoginException("用户名或密码错误!");
        }
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime)<0){
            throw new LoginException("账号已失效，请联系管理员");
        }
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new LoginException("账号已被锁定，请联系管理员");
        }
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new LoginException("ip地址受限，请联系管理员");
        }
        return user;
    }
}

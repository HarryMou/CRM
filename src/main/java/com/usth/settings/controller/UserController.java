package com.usth.settings.controller;

import com.usth.exception.LoginException;
import com.usth.settings.domain.User;
import com.usth.settings.service.UserService;
import com.usth.utils.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/settings/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/login.do")
    @ResponseBody
    private Map<String,Object> login(String loginAct, String loginPwd, HttpServletRequest request) throws LoginException {
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();
        User user = userService.login(loginAct,loginPwd,ip);
        if(user!=null){
            request.getSession().setAttribute("user",user);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("success",true);
        return map;
    }
}

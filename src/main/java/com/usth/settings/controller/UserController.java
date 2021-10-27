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

@Controller
@RequestMapping("/settings/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/login.do")
    @ResponseBody
    public ModelAndView login(String loginAct, String loginPwd, HttpServletRequest request) throws LoginException {
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();
        User user = userService.login(loginAct,loginPwd,ip);
        ModelAndView mv = new ModelAndView();
        mv.addObject("loginAct",user.getLoginAct());
        mv.setViewName("workbench/index.html");
        return mv;
    }
}

package com.usth.settings.service;


import com.usth.exception.LoginException;
import com.usth.settings.domain.User;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;
}

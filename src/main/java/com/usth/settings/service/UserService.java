package com.usth.settings.service;


import com.usth.exception.LoginException;
import com.usth.settings.domain.User;

import java.util.List;

public interface UserService {

    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}

package com.usth.settings.dao;

import com.usth.settings.domain.User;

import java.util.Map;

public interface UserDao {

    User login(Map<String, Object> map);
}

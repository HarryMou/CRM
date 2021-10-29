package com.usth.settings.dao;

import com.usth.settings.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


public interface UserDao {

    User login(Map<String, Object> map);

    List<User> getUserList();
}

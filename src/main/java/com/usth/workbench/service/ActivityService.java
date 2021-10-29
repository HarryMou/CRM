package com.usth.workbench.service;

import com.usth.settings.domain.User;
import com.usth.vo.PaginationVO;
import com.usth.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    boolean saveActivity(Activity activity);

    PaginationVO<Activity> pageList(Map<String, Object> map);
}
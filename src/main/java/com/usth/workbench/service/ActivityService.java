package com.usth.workbench.service;

import com.usth.settings.domain.User;
import com.usth.vo.PaginationVO;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    boolean saveActivity(Activity activity);

    PaginationVO<Activity> pageList(Map<String, Object> map);

    boolean deleteActivity(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean updateActivity(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAId(String activityId);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark activityRemark);

    boolean updateRemark(ActivityRemark activityRemark);

    List<Activity> getActivityListByClueId(String clueId);
}

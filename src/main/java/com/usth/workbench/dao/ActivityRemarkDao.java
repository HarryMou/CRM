package com.usth.workbench.dao;

import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAId(String activityId);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark activityRemark);

    int updateRemark(ActivityRemark activityRemark);
}

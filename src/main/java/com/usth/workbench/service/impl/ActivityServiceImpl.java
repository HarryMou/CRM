package com.usth.workbench.service.impl;

import com.usth.settings.dao.UserDao;
import com.usth.settings.domain.User;
import com.usth.vo.PaginationVO;
import com.usth.workbench.dao.ActivityDao;
import com.usth.workbench.dao.ActivityRemarkDao;
import com.usth.workbench.dao.ClueActivityRelationDao;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.ActivityRemark;
import com.usth.workbench.service.ActivityService;
import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityDao activityDao;
    @Resource
    private ActivityRemarkDao activityRemarkDao;
    @Resource
    private ClueActivityRelationDao clueActivityRelationDao;
    @Resource
    private UserDao userDao;
//    private ApplicationContext ac = new ClassPathXmlApplicationContext("settings/ApplicationContext.xml");

    @Override
    public boolean saveActivity(Activity activity) {
        boolean flag =true;
        int count = activityDao.saveActivity(activity);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得totatl，datalist,封装到vo中
        int total = activityDao.getTotalByCondition(map);
        List<Activity> datalist = activityDao.getActivityByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(datalist);
        return vo;
    }

    @Override
    public boolean deleteActivity(String[] ids) {
        boolean flag =true;
        //先查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);
        if(count1!=count2){
            flag = false;
        }
        //查询市场活动关联的线索的数量
        int count4 = clueActivityRelationDao.getCountByAids(ids);
        //删除关联信息，返回受影响的条数
        int count5 = clueActivityRelationDao.deleteByAids(ids);
        if(count4!=count5){
            flag=false;
        }
        //删除市场活动
        int count3 = activityDao.deleteActivity(ids);
        if(count3!=ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取userList
        List<User> userList = userDao.getUserList();
        //取activity
        Activity activity = activityDao.getActivityById(id);
        //返回map
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("activity",activity);
        return map;
    }

    @Override
    public boolean updateActivity(Activity activity) {
        boolean flag = true;

        int count = activityDao.updateActivity(activity);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        return activityDao.detail(id);
    }

    @Override
    public List<ActivityRemark> getRemarkListByAId(String activityId){
        return activityRemarkDao.getRemarkListByAId(activityId);
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteRemark(id);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(activityRemark);
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(activityRemark);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activities = activityDao.getActivityListByClueId(clueId);
        return activities;
    }

    @Override
    public List<Activity> getActivityListByName(Map<String, String> map) {
        List<Activity> list = activityDao.getActivityListByName(map);
        return list;
    }

    @Override
    public List<Activity> getActivityListByNameConvert(String aname) {
        List<Activity> activityList = activityDao.getActivityListByNameConvert(aname);
        return activityList;
    }

}

package com.usth.workbench.service.impl;

import com.usth.settings.domain.User;
import com.usth.vo.PaginationVO;
import com.usth.workbench.dao.ActivityDao;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.service.ActivityService;
import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityDao activityDao;
    private ApplicationContext ac = new ClassPathXmlApplicationContext("settings/ApplicationContext.xml");

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
        PaginationVO<Activity> vo = (PaginationVO<Activity>) ac.getBean("myVO");
        vo.setTotal(total);
        vo.setDataList(datalist);
        return vo;
    }
}

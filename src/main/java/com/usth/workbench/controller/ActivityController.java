package com.usth.workbench.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.settings.domain.User;
import com.usth.settings.service.UserService;
import com.usth.settings.service.impl.UserServiceImpl;
import com.usth.utils.DateTimeUtil;
import com.usth.utils.ForJson;
import com.usth.utils.UUIDUtil;
import com.usth.vo.PaginationVO;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;
    @Resource
    private UserService userService;


    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList(){
       return userService.getUserList();
    }

    @RequestMapping("/saveActivity.do")
    private void saveActivity(Activity activity, HttpServletRequest request, HttpServletResponse response){
        String id = UUIDUtil.getUUID();
        //创建时间，系统当前时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人，登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        activity.setId(id);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);
        boolean flag = activityService.saveActivity(activity);
        String json = "{\"success\":"+flag+"}";
        try {
            PrintWriter pw = response.getWriter();
            pw.print(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private Map<String,Object> pageList(Activity activity,String pageNo,String pageSize){
        int pageno = Integer.valueOf(pageNo);
        //每页展现的记录
        int pagesize = Integer.valueOf(pageSize);
        //计算出掠过的记录
        int skipCount = (pageno-1)*pagesize;

        Map<String,Object> map = new HashMap<>();
        map.put("name",activity.getName());
        map.put("owner",activity.getOwner());
        map.put("startDate",activity.getStartDate());
        map.put("endDate",activity.getEndDate());
        map.put("skipCount",skipCount);
        map.put("pageSize",pagesize);
        //业务层拿到了以上两项信息后，如果做返回
        /*
            map
               map.put("total":total)
               map.put("datalist",datalist)

            vo
                PaginationVO<T>
                    private int total
                    private List<T> datalist
            将来分页查询，每个模块都有，所以我们选择使用一个通用vo，操作起来比较方便
         */
        PaginationVO<Activity> vo = activityService.pageList(map);
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("total",vo.getTotal());
        resMap.put("dataList",vo.getDataList());
        return resMap;
    }

    @RequestMapping("/deleteActivity.do")
    private void deleteActivity(String[] ids, HttpServletResponse response){
        boolean flag = activityService.deleteActivity(ids);
        String json = "{\"success\":" + flag + "}";
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.print(json);
    }

    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    private Map<String,Object> getUserListAndActivity(String id){
        return activityService.getUserListAndActivity(id);
    }

    @RequestMapping("/updateActivity.do")
    private void updateActivity(Activity activity,HttpServletRequest request,HttpServletResponse response){
        //修改时间，系统当前时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);
        boolean flag = activityService.updateActivity(activity);
        String json = "{\"success\":"+flag+"}";
        try {
            PrintWriter pw = response.getWriter();
            pw.print(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(String id){
        Activity activity = activityService.detail(id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("activity",activity);
        mv.setViewName("/workbench/activity/detail.jsp");
        return mv;
    }
}

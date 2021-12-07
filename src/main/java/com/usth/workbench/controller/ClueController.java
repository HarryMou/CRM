package com.usth.workbench.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.usth.settings.domain.User;
import com.usth.settings.service.UserService;
import com.usth.utils.DateTimeUtil;
import com.usth.utils.UUIDUtil;
import com.usth.vo.PaginationVO;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.Clue;
import com.usth.workbench.domain.ClueRemark;
import com.usth.workbench.domain.Tran;
import com.usth.workbench.service.ActivityService;
import com.usth.workbench.service.ClueService;
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
@RequestMapping("/workbench/clue")
public class ClueController {

    @Resource
    private UserService userService;
    @Resource
    private ClueService clueService;
    @Resource
    private ActivityService activityService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        return userService.getUserList();
    }

    @RequestMapping("/saveClue.do")
    public void saveClue(Clue clue, HttpServletRequest request, HttpServletResponse response){
        String id = UUIDUtil.getUUID();
        clue.setId(id);
        String createTime = DateTimeUtil.getSysTime();
        clue.setCreateTime(createTime);
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        clue.setCreateBy(createBy);
        boolean flag = clueService.saveClue(clue);
        String json = "{\"success\":"+flag+"}";
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.print(json);
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private Map<String,Object> pageList(Clue clue, String pageNo, String pageSize){
        int pageno = Integer.valueOf(pageNo);
        //每页展现的记录
        int pagesize = Integer.valueOf(pageSize);
        //计算出掠过的记录
        int skipCount = (pageno-1)*pagesize;

        Map<String,Object> map = new HashMap<>();
        map.put("fullname",clue.getFullname());
        map.put("company",clue.getCompany());
        map.put("source",clue.getSource());
        map.put("state",clue.getState());
        map.put("owner",clue.getOwner());
        map.put("phone",clue.getPhone());
        map.put("mphone",clue.getMphone());
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
        PaginationVO<Activity> vo = clueService.pageList(map);
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("total",vo.getTotal());
        resMap.put("dataList",vo.getDataList());
        return resMap;
    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(String id){
        Clue clue = clueService.detail(id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("clue",clue);
        mv.setViewName("/workbench/clue/detail.jsp");
        return mv;
    }

    @RequestMapping("/getActivityListByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByClueId(String clueId){
        List<Activity> activities = activityService.getActivityListByClueId(clueId);
        return activities;
    }

    @RequestMapping("/unbund.do")
    private void unbund(String id,HttpServletResponse response){
        boolean flag = clueService.unbund(id);
        String json = "{\"success\":"+flag+"}";
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.print(json);
    }

    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    private List<Activity> getActivityListByName(String clueid, String aname){
        Map<String,String> map = new HashMap<>();
        map.put("clueid",clueid);
        map.put("aname",aname);
        List<Activity> activityList = activityService.getActivityListByName(map);
        return activityList;
    }

    @RequestMapping("/bund.do")
    private void bund(String[] aid, String cid, HttpServletResponse response) {
        boolean flag = clueService.bund(aid, cid);
        String json = "{\"success\":" + flag + "}";
        PrintWriter pw = null;
        try {
            pw = response.getWriter();

        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.print(json);
    }

    @RequestMapping("/getActivityListByNameConvert.do")
    @ResponseBody
    private List<Activity> getActivityListByNameConvert(String aname){
        List<Activity> activityList = activityService.getActivityListByNameConvert(aname);
        return activityList;
    }

    @RequestMapping("/convert.do")
    private String conver(String clueId, String flag, Tran tran,HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        boolean flag1 = clueService.convert(clueId, tran, createBy, flag);
        if (flag1){
            return "/workbench/clue/index.jsp";
        }
        return "/workbench/clue/index.jsp";
    }

    @RequestMapping("/saveRemark.do")
    @ResponseBody
    private Map<String,Object> saveRemark(String noteContent, String clueId,HttpServletRequest request){
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";
        ClueRemark clueRemark = new ClueRemark();
        clueRemark.setNoteContent(noteContent);
        clueRemark.setEditFlag(editFlag);
        clueRemark.setId(id);
        clueRemark.setClueId(clueId);
        clueRemark.setCreateBy(createBy);
        clueRemark.setCreateTime(createTime);
        Map<String,Object> map = new HashMap<>();
        boolean flag = clueService.saveRemark(clueRemark);
        map.put("success",flag);
        map.put("remarkInfo",clueRemark);
        return map;
    }

    @RequestMapping("/getRemarkListByCId.do")
    @ResponseBody
    private List<ClueRemark> getRemarkListByCId(String clueId){
        return clueService.getRemarkListByCId(clueId);
    }

    @RequestMapping("/deleteClues.do")
    private void deleteClues(String[] ids,HttpServletResponse response){
        boolean flag = clueService.deleteClues(ids);
        String json = "{\"success\":" + flag + "}";
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.print(json);
    }

    @RequestMapping("/getUserListAndClue.do")
    @ResponseBody
    private Map<String,Object> getUserListAndClue(String clueId){
        return clueService.getUserListAndClue(clueId);
    }

    @RequestMapping("/updateClue.do")
    private void updateClue(Clue clue,HttpServletResponse response,HttpServletRequest request){
        //修改时间，系统当前时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        clue.setEditTime(editTime);
        clue.setEditBy(editBy);
        boolean flag = clueService.updateClue(clue);
        String json = "{\"success\":"+flag+"}";
        try {
            PrintWriter pw = response.getWriter();
            pw.print(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

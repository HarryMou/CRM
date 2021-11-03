package com.usth.workbench.service.impl;

import com.usth.vo.PaginationVO;
import com.usth.workbench.dao.ClueDao;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.Clue;
import com.usth.workbench.service.ClueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Resource
    private ClueDao clueDao;

    @Override
    public boolean saveClue(Clue clue) {
        boolean flag =true;
        int count = clueDao.saveClue(clue);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        int total = clueDao.getTotalByCondition(map);
        List<Activity> datalist = clueDao.getClueByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(datalist);
        return vo;
    }

    @Override
    public Clue detail(String id) {
        return clueDao.detail(id);
    }


}

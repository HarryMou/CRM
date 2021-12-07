package com.usth.workbench.service;

import com.usth.vo.PaginationVO;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.Clue;
import com.usth.workbench.domain.ClueRemark;
import com.usth.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean saveClue(Clue clue);

    PaginationVO<Activity> pageList(Map<String, Object> map);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String[] aid, String cid);

    boolean convert(String clueId, Tran tran, String createBy,String flag);

    boolean saveRemark(ClueRemark clueRemark);

    List<ClueRemark> getRemarkListByCId(String clueId);

    boolean deleteClues(String[] ids);

    Map<String, Object> getUserListAndClue(String clueId);

    boolean updateClue(Clue clue);
}

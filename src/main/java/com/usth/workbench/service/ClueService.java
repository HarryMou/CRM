package com.usth.workbench.service;

import com.usth.vo.PaginationVO;
import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.Clue;

import java.util.Map;

public interface ClueService {
    boolean saveClue(Clue clue);

    PaginationVO<Activity> pageList(Map<String, Object> map);

    Clue detail(String id);
}

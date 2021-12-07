package com.usth.workbench.dao;


import com.usth.workbench.domain.Activity;
import com.usth.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int saveClue(Clue clue);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getClueByCondition(Map<String, Object> map);

    Clue detail(String id);

    Clue getClueById(String clueId);

    int delete(Clue clue);

    int deleteClues(String[] ids);

    int updateClue(Clue clue);
}

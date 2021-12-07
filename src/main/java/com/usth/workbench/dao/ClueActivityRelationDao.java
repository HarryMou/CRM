package com.usth.workbench.dao;


import com.usth.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {

    int bund(ClueActivityRelation car);

    int unbund(String id);

    List<ClueActivityRelation> getListByClueId(String clueId);

    int delete(ClueActivityRelation clueActivityRelation);

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    int getCountByCids(String[] ids);

    int deleteByCids(String[] ids);
}

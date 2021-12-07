package com.usth.workbench.dao;

import com.usth.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> getListByClueId(String clueId);

    int delete(ClueRemark clueRemark);

    int saveRemark(ClueRemark clueRemark);

    int getCountByCids(String[] ids);

    int deleteByCids(String[] ids);
}

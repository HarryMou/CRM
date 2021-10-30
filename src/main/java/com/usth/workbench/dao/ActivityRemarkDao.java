package com.usth.workbench.dao;

import com.usth.workbench.domain.Activity;

public interface ActivityRemarkDao {

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

}

package com.usth.settings.service.impl;

import com.usth.settings.dao.DicTypeDao;
import com.usth.settings.dao.DicValueDao;
import com.usth.settings.domain.DicType;
import com.usth.settings.domain.DicValue;
import com.usth.settings.service.DicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {

    @Resource
    private DicTypeDao dicTypeDao;
    @Resource
    private DicValueDao dicValueDao;

    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map = new HashMap<>();
        //将字典类型列表取出
        List<DicType> dicTypeList = dicTypeDao.getTypeList();

        for(DicType dt : dicTypeList){
            String code = dt.getCode();
            //根据每一个字典类型取得字典值列表
            List<DicValue> dvList = dicValueDao.getListByCode(code);
            map.put(code+"List",dvList);
        }
        return map;
    }
}

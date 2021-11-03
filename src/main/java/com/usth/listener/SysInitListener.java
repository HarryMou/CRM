package com.usth.listener;

import com.usth.settings.domain.DicValue;
import com.usth.settings.service.DicService;
import com.usth.settings.service.impl.DicServiceImpl;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SysInitListener implements ServletContextListener {

    private DicService dicService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        dicService = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(DicService.class);
        ServletContext application = sce.getServletContext();
        //取数据字典
        Map<String, List<DicValue>> map = dicService.getAll();
        //Map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for(String key :set){
            application.setAttribute(key,map.get(key));
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}

package com.usth.settings.handler;

import com.usth.exception.LoginException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(LoginException.class)
    public void loginException(Exception e, HttpServletResponse response){
//        ModelAndView mv = new ModelAndView();
        //将抛出的异常信息转为json字符串输出到前端
        String json = "{\"msg\":\"" + e.getMessage() +"\"}";
        PrintWriter pw = null;
        try {
            response.setContentType("application/json");
            pw = response.getWriter();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pw.print(json);

    }

    @ExceptionHandler
    public ModelAndView otherException(Exception e){
        ModelAndView mv = new ModelAndView();
        mv.addObject("msg",e.getMessage());
        mv.setViewName("/login.jsp");
        return mv;
    }
}

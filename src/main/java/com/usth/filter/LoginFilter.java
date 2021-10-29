package com.usth.filter;


import com.usth.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        User user = (User) request.getSession().getAttribute("user");
        if(null!=user) {
            filterChain.doFilter(request, response);
        }else if("/login.jsp".equals(request.getServletPath())){
            filterChain.doFilter(request,response);
        }else {
            response.sendRedirect(request.getContextPath()+"/login.jsp");
        }
    }

}

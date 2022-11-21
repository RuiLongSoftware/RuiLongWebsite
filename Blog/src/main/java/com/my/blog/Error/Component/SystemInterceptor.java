package com.my.blog.Error.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

/**
 * @author Jimsss
 */
@Slf4j
public class SystemInterceptor implements HandlerInterceptor {
    public String successText = "success";
    public boolean on = true;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (on){
            Cookie[] cookies = request.getCookies();
            String name = null;
            String password = null;
            if(cookies != null) {
                for (Cookie cookie : cookies) {
                    name = cookie.getName();
                    password = cookie.getValue();
                }
            }
            if (name == null){
                request.getRequestDispatcher("/login").forward(request,response);
                return false;
            }else {
                String findMessage = findUserCookie(name, password);
                if (findMessage.equals(successText)) {
                    log.info("用户已登录");
                    return true;
                } else {
                    request.getRequestDispatcher("/login").forward(request, response);
                    return false;
                }
            }
        }else {
            return true;
        }
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        log.info("已执行拦截器的postHandle方法");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        log.info("已执行拦截器的afterCompletion方法");
    }

    public String findUserCookie(String userName, String userPassword) {
        Connection connection;
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web", "root", "PASSWORD");
            preparedStatement = connection.prepareStatement("SELECT name,password FROM user where name=? and password=?");
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userPassword);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                resultSet.close();
                preparedStatement.close();
                connection.close();
                log.info("已成功查找到用户" + userName + "的Cookie数据");
                return "success";
            } else {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                log.info("没有查找到用户" + userName + "的Cookie数据");
                return "noSuccess";
            }
        }catch (ClassNotFoundException e){
            log.error("查找用户错误!错误代码:" + e);
            return "error";
        }catch (SQLException e){
            log.error("查找用户错误!错误代码:" + e);
            return "error";
        }
    }
}

package com.my.blog.Controller;

import com.my.blog.Entity.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.*;

/**
 * @author Jimsss
 */
@Controller
@Slf4j
public class HomePageController {
    public String successText = "success";
    @GetMapping("/")
    public String indexPage(Model model, HttpServletRequest request) throws IOException {
        Cookie[] cookies = request.getCookies();
        String userName = null;
        String password = null;
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                userName = cookie.getName();
                password = cookie.getValue();
            }
        }
        if (userName == null){
            log.info("用户未登录!");
            model.addAttribute("un", "未登录");
            return "content/indexPage";
        }else {
            if (findUserCookie(userName, password).equals(successText)){
                log.info(userName + "已登录查看主页面");
                int count = getAutoIncrement();
                Content[] contents = new Content[count];
                int id = 0;
                for (int i = 0; i < count; i++) {
                    id++;
                    Content content = new Content();
                    content.setContent(getContent(id));
                    content.setContentHref(getContentHref(id));
                    contents[i] = content;
                }
                model.addAttribute("un", userName);
                model.addAttribute("contentMessage", contents);
                return "content/loginPage";
            }
            log.info(userName + "尝试篡改Cookie数据!");
            model.addAttribute("un", "未登录");
            return "content/indexPage";
        }
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
            if (resultSet.next()) {
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
        } catch (ClassNotFoundException e) {
            log.error("查找用户错误!错误代码:" + e);
            return "error";
        } catch (SQLException e) {
            log.error("查找用户错误!错误代码:" + e);
            return "error";
        }
    }
    public String getContent(int id){
        Connection connection;
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web", "root", "PASSWORD");
            preparedStatement = connection.prepareStatement("SELECT name FROM content WHERE id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSet.getString("name");
            }
            connection.close();
            resultSet.close();
            preparedStatement.close();
        } catch (ClassNotFoundException e) {
            log.error("查找内容错误!错误代码:" + e);
            return "";
        } catch (SQLException e) {
            log.error("查找内容错误!错误代码:" + e);
            return "";
        }
        return "";
    }
    public String getContentHref(int id){
        Connection connection;
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web", "root", "PASSWORD");
            preparedStatement = connection.prepareStatement("SELECT href FROM content WHERE id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSet.getString("href");
            }
            connection.close();
            resultSet.close();
            preparedStatement.close();
        } catch (ClassNotFoundException e) {
            log.error("查找内容错误!错误代码:" + e);
            return "";
        } catch (SQLException e) {
            log.error("查找内容错误!错误代码:" + e);
            return "";
        }
        return "";
    }
    public int getAutoIncrement() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\WebBlogServer\\files\\contentCount.txt"));
        String count;
        count = bufferedReader.readLine();
        bufferedReader.close();
        return new Integer(count);
    }
}

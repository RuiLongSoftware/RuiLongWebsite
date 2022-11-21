package com.my.blog.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
/**
 * @author Jimsss
 */
@Slf4j
@Component
public class UserLoginRegister {
    @Value("${spring.datasource.url}")
    public String url;
    @Value("${spring.datasource.username}")
    public String mySqlUserName;
    @Value("${spring.datasource.password}")
    public String mySqlPassword;
    public String driver = "com.mysql.cj.jdbc.Driver";
    public String findUser = "SELECT name,password FROM user where name=? and password=?";
    public String findUserReg = "SELECT name FROM user where name=?";
    public String createUser = "INSERT INTO user (name,password,mail) values(?,?,?)";
    public String delUser = "DELETE FROM user WHERE name=?";
    public String adminName = "Admin";
    public Connection connection;
    public ResultSet resultSet;
    public PreparedStatement preparedStatement;
    public String login(String userName, String userPassword){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, mySqlUserName, mySqlPassword);
            preparedStatement = connection.prepareStatement(findUser);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userPassword);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                resultSet.close();
                preparedStatement.close();
                connection.close();
                if (adminName.equals(userName)){
                    log.info("管理员登录成功!");
                }else {
                    log.info("登录成功!用户名:" + userName + ",密码:" + userPassword);
                }
                return "success";
            }else {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                if (adminName.equals(userName)){
                    log.info("管理员登录不成功,疑似冒充!");
                }else {
                    log.info("登录不成功!用户名:" + userName + ",密码:" + userPassword);
                }
                return "noSuccess";
            }
        }catch (ClassNotFoundException e){
            log.error("登录错误!错误代码:" + e);
            return "error";
        }catch (SQLException e){
            log.error("登录错误!错误代码:" + e);
            return "error";
        }
    }
    public String register(String userName, String userPassword,String userMail){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, mySqlUserName, mySqlPassword);
            preparedStatement = connection.prepareStatement(findUserReg);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                preparedStatement.close();
                resultSet.close();
                connection.close();
                log.info("创建用户失败!因为已经有同名用户:" + userName);
                return "noSuccess";
            }else {
                preparedStatement = connection.prepareStatement(createUser);
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, userPassword);
                preparedStatement.setString(3, userMail);
                preparedStatement.execute();
                preparedStatement.close();
                resultSet.close();
                connection.close();
                if (adminName.equals(userName)){
                    log.info("管理员初始化注册成功!");
                }else {
                    log.info("创建用户成功!用户名:" + userName + ",密码:" + userPassword);
                }
                return "success";
            }
        }catch (ClassNotFoundException e){
            log.error("创建用户错误!错误代码:" + e);
            return "error";
        }catch (SQLException e){
            log.error("创建用户错误!错误代码:" + e);
            return "error";
        }
    }
    public String deleteUser(String userName){
        if (userName.isEmpty()){
            return "noSuccess";
        }else {
            log.info(userName + "尝试删除用户");
            if (adminName.equals(userName)){
                log.info("管理员用户无法删除!");
                return "noSuccess";
            }else {
                try {
                    Class.forName(driver);
                    connection = DriverManager.getConnection(url, mySqlUserName, mySqlPassword);
                    preparedStatement = connection.prepareStatement(delUser);
                    preparedStatement.setString(1, userName);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();
                    preparedStatement.close();
                    connection.close();
                    log.info("已删除用户" + userName + "的账户");
                    return "success";
                }catch (ClassNotFoundException e){
                    log.info("删除用户错误!错误代码:" + e);
                    return "error";
                }catch (SQLException e) {
                    log.info("删除用户错误!错误代码:" + e);
                    return "error";
                }
            }
        }
    }
}

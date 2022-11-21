package com.my.blog.Controller.Admin;

import com.my.blog.Entity.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
/*import org.springframework.web.bind.annotation.RequestMethod;*/

import java.io.*;
import java.sql.*;

@Controller
@Component
@Slf4j
public class AdminController {
    @Value("${admin.password}")
    String getPassword;

    @RequestMapping("/admin")
    public String admin(){
        return "content/admin/login";
    }

    @RequestMapping("/adminLogin")
    public String loginIn(String password, Model model) throws IOException {
        if (password.equals(getPassword)){
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
            model.addAttribute("contentMessage", contents);
            return "content/admin/homePage";
        }
        return "content/admin/loginError";
    }
    /*
    @RequestMapping(value = "/deleteBlog", method = RequestMethod.POST)
    public String deleteBlog(int blogId){
        boolean deleteBlog = deleteBlogContent(blogId);
        boolean setAI = setAutoIncrement(blogId - 1);
        boolean writeFile = writeTxt("C:\\WebBlogServer\\files\\contentCount.txt", blogId);
        if (!deleteBlog){
            return "content/admin/deleteBlogError";
        }else if (!writeFile){
            return "content/admin/deleteBlogError";
        }else if (!setAI){
            return "content/admin/deleteBlogError";
        }
        return "content/admin/deleteBlogSuccess";
    }
     */
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
    /*
    public boolean setAutoIncrement(int setAI){
        return true;
    }
    public boolean writeTxt(String path, int i){
        PrintStream printStream;
        try {
            printStream = new PrintStream(path);
            printStream.print(i);
            return true;
        } catch (FileNotFoundException e) {
            log.error("未找到文件");
        }
        return false;
    }
    public boolean deleteBlogContent(int id){
        Connection connection;
        PreparedStatement preparedStatement;
        if (id == 0){
            return false;
        }else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web", "root", "PASSWORD");
                preparedStatement = connection.prepareStatement("DELETE FROM content WHERE id=?");
                preparedStatement.setInt(1, id);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                preparedStatement.close();
                connection.close();
                log.info("已删除博客" + id);
                return true;
            }catch (ClassNotFoundException e){
                log.info("删除博客错误!错误代码:" + e);
                return false;
            }catch (SQLException e) {
                log.info("删除博客错误!错误代码:" + e);
                return false;
            }
        }
    }
     */
}

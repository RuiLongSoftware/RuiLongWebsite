package com.my.blog.Controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.*;

@Controller
@Slf4j
public class UploadFile {
    public String successText = "success";

    @RequestMapping("/newBlog")
    public Object newBlog(@RequestParam("file")MultipartFile file, String title){
        return saveFile(file, title);
    }

    public Object saveFile(MultipartFile file, String name){
        if (file.isEmpty()){
            return "content/admin/noFile";
        }
        try {
            int count = getAutoIncrement() + 1;
            boolean writeTxt = writeTxt("C:\\WebBlogServer\\files\\contentCount.txt", count);
            if (!writeTxt){
                return "content/admin/noFile";
            }
            String fileName = "B" + count + ".pdf";
            String filePath = "C:\\WebBlogServer\\files\\content\\";
            File localFile = new File(filePath + fileName);
            file.transferTo(localFile);
            String newContent = newContent(name, "/content/" + fileName);
            if (!newContent.equals(successText)){
                return "content/admin/noFile";
            }
            return "content/admin/uploadSuccess";
        } catch (IOException e) {
            return "content/admin/noFile";
        }
    }
    public int getAutoIncrement() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\WebBlogServer\\files\\contentCount.txt"));
        String count;
        count = bufferedReader.readLine();
        bufferedReader.close();
        return new Integer(count);
    }
    public String newContent(String name, String href){
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web", "root", "PASSWORD");
            preparedStatement = connection.prepareStatement("INSERT INTO content (name,href) values(?,?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, href);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
            return "success";
        }catch (ClassNotFoundException e){
            log.error("创建博客内容错误!错误代码:" + e);
            return "error";
        }catch (SQLException e){
            log.error("创建博客内容错误!错误代码:" + e);
            return "error";
        }
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
}

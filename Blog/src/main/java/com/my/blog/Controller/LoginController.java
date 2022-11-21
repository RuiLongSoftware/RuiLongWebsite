package com.my.blog.Controller;

import com.my.blog.Util.SendMail;
import com.my.blog.Util.UserLoginRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jimsss
 */
@Slf4j
@Controller
public class LoginController {
    public String successText = "success";
    public String noSuccessText = "noSuccess";
    public int cookieTime = 2592000;
    public String sendMailTitle = "您在RL博客注册了账号";
    @Autowired
    UserLoginRegister userLoginRegister;

    @RequestMapping("/login")
    public String log(){
        return "lr/login";
    }

    @RequestMapping(value = "/loginIn",method = RequestMethod.POST)
    public String login(String name, String password, HttpServletResponse response, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String  isNull = null;
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                isNull = cookie.getName();
            }
        }
        if (isNull != null){
            Cookie cookie = new Cookie(isNull, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        if (name.isEmpty()){
            return "lr/loginError";
        }else {
            String loginMessage;
            loginMessage = userLoginRegister.login(name, password);
            if(successText.equals(loginMessage)){
                if (name.equals(userLoginRegister.adminName)){
                    log.info("管理员登录成功!");
                }else {
                    log.info(name + "登录成功!");
                }
                Cookie cookie = new Cookie(name, password);
                cookie.setPath("/");
                cookie.setMaxAge(cookieTime);
                response.addCookie(cookie);
                return "lr/loginSuccess";
            }else if (noSuccessText.equals(loginMessage)){
                log.info(name + "登录不成功!账号密码错误");
                return "lr/loginError";
            }else {
                log.error("Blog系统:代码错误");
                return "lr/codeError";
            }
        }
    }

    @RequestMapping("/register")
    public String reg(){
        return "lr/register";
    }

    @RequestMapping(value = "/registerIn",method = RequestMethod.POST)
    public String register(String name, String password, String mail, HttpServletResponse response, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String  isNull = null;
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                isNull = cookie.getName();
            }
        }
        if (isNull != null){
            Cookie cookie = new Cookie(isNull, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        if (name.isEmpty()){
            return "lr/registerError";
        }else {
            String registerMessage;
            registerMessage = userLoginRegister.register(name, password, mail);
            if (successText.equals(registerMessage)) {
                if (name.equals(userLoginRegister.adminName)){
                    log.info("管理员注册成功");
                }else {
                    log.info(name + "注册成功!");
                }
                Cookie cookie = new Cookie(name, password);
                cookie.setPath("/");
                cookie.setMaxAge(cookieTime);
                response.addCookie(cookie);
                if (!mail.isEmpty()) {
                    SendMail sendMail = new SendMail();
                    sendMail.sendEmail(mail, sendMailTitle, "您的账号密码为" + name + "," + password);
                }
                return "lr/registerSuccess";
            } else if (noSuccessText.equals(registerMessage)){
                log.info(name + "注册不成功!有同名用户!");
                return "lr/registerError";
            }else {
                log.error("Blog系统:代码错误");
                return "lr/codeError";
            }
        }
    }

    @RequestMapping("/delUser")
    public String del(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String name = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                name = cookie.getName();
            }
        }
        if (name == null){
            return "lr/delUserError";
        }else {
            String deleteUserMessage;
            deleteUserMessage = userLoginRegister.deleteUser(name);
            if (successText.equals(deleteUserMessage)) {
                log.info(name + "删除成功");
                Cookie cookie = new Cookie(name, null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                return "lr/delUserSuccess";
            } else if (noSuccessText.equals(deleteUserMessage)) {
                log.info("删除用户错误!");
                return "lr/delUserError";
            } else {
                log.error("Blog系统:代码错误");
                return "lr/codeError";
            }
        }
    }

    @RequestMapping("/delCookie")
    public String delCookie(HttpServletResponse response, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String name = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                name = cookie.getName();
            }
        }
        if (name == null){
            return "lr/delCookieError";
        }else {
            Cookie cookie = new Cookie(name, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "lr/delCookieSuccess";
        }
    }
}
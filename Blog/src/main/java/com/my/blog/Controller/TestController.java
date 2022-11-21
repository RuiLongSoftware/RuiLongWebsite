package com.my.blog.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Jimsss
 */
@Controller
public class TestController {
    @RequestMapping("/testPage")
    public String test1(){
        return "test/testContent";
    }
}

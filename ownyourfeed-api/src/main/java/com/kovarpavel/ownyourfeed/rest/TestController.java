package com.kovarpavel.ownyourfeed.rest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String testApi() {
       String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return "hello " + username;
    }

}

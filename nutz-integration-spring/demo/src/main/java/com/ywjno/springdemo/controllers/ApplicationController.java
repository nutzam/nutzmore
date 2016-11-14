package com.ywjno.springdemo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/")
    String index() {
        logger.info("ApplicationController#index");
        return "index";
    }
}

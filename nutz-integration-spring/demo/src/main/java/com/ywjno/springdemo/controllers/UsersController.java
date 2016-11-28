package com.ywjno.springdemo.controllers;

import java.util.List;

import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ywjno.springdemo.beans.User;

@Controller
@RequestMapping("/user")
public class UsersController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Dao nutDao;

    @RequestMapping("/index")
    String index(Model model) {
        logger.info("UsersController#index");
        List<User> users = nutDao.query(User.class, null);
        model.addAttribute("users", users);
        return "user/index";
    }
}

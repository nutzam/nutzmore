package com.ywjno.springdemo;

import javax.annotation.PostConstruct;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.lang.random.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ywjno.springdemo.beans.User;

@Component
public class MainSetup {
    @Autowired
    private Dao nutDao;

    @PostConstruct
    private void init() {
        Daos.FORCE_WRAP_COLUMN_NAME = true;
        Daos.createTablesInPackage(nutDao, User.class, false);
        Daos.migration(nutDao, User.class.getPackage().getName(), true, false);

        User user = new User();
        user.setName(R.UU32());
        user.setAge(R.random(8, 80));
        nutDao.insert(user);
    }
}

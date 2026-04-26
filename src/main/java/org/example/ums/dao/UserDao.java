package org.example.ums.dao;

import org.example.ums.entity.User;

public class UserDao extends AbstractJpaDao<User, Integer> {

    public UserDao() {
        super(User.class);
    }
}


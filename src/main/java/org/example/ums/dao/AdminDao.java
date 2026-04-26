package org.example.ums.dao;

import org.example.ums.entity.Admin;

public class AdminDao extends AbstractJpaDao<Admin, Integer> {

    public AdminDao() {
        super(Admin.class);
    }
}


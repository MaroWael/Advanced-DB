package org.example.ums.entity;

import org.example.ums.entity.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

    public Admin() {
        setRole(Role.ADMIN);
    }

    public Admin(String name, String email, String password) {
        super(name, email, password, Role.ADMIN);
    }
}


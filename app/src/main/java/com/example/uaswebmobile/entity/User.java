package com.example.uaswebmobile.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String username;
    public String email;
    public String password;
    public String role; // "job_seeker" or "employer"
    public String name;
    public String location;
    public String headline;
    public String expectedSalary;
    public String cvLink;
    
    public User() {}
    
    @Ignore
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = "";
        this.location = "";
        this.headline = "";
        this.expectedSalary = "";
        this.cvLink = "";
    }
}


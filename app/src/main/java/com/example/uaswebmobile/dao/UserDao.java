package com.example.uaswebmobile.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.uaswebmobile.entity.User;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);
    
    @Update
    void updateUser(User user);
    
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User login(String username, String password);
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
}


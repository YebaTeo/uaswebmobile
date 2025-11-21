package com.example.uaswebmobile.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int userId;
    public String title;
    public String message;
    public String type; // "success", "info", "warning", "error"
    public String timestamp;
    public boolean isRead;
    
    @Ignore
    public Notification() {}
    
    public Notification(int userId, String title, String message, String type, String timestamp) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.isRead = false;
    }
}


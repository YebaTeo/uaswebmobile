package com.example.uaswebmobile.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks")
public class Bookmark {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int jobId;
    public int jobSeekerId;
    
    public Bookmark() {}
    
    @Ignore
    public Bookmark(int jobId, int jobSeekerId) {
        this.jobId = jobId;
        this.jobSeekerId = jobSeekerId;
    }
}


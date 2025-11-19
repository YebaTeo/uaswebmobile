package com.example.uaswebmobile.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "applications")
public class Application {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int jobId;
    public int jobSeekerId;
    public String status; // "submitted", "reviewed", "shortlisted", "rejected"
    public String tanggalLamaran;
    
    public Application() {}
    
    @Ignore
    public Application(int jobId, int jobSeekerId, String status, String tanggalLamaran) {
        this.jobId = jobId;
        this.jobSeekerId = jobSeekerId;
        this.status = status;
        this.tanggalLamaran = tanggalLamaran;
    }
}


package com.example.uaswebmobile.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.uaswebmobile.entity.Application;
import java.util.List;

@Dao
public interface ApplicationDao {
    @Insert
    long insertApplication(Application application);
    
    @Update
    void updateApplication(Application application);
    
    @Query("SELECT * FROM applications WHERE jobId = :jobId")
    List<Application> getApplicationsByJob(int jobId);
    
    @Query("SELECT * FROM applications WHERE jobSeekerId = :jobSeekerId")
    List<Application> getApplicationsByJobSeeker(int jobSeekerId);
    
    @Query("SELECT * FROM applications WHERE jobId = :jobId AND jobSeekerId = :jobSeekerId")
    Application getApplicationByJobAndSeeker(int jobId, int jobSeekerId);
    
    @Query("SELECT * FROM applications WHERE jobId = :jobId ORDER BY tanggalLamaran DESC")
    List<Application> getApplicationsByJobOrdered(int jobId);
}


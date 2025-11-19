package com.example.uaswebmobile.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.uaswebmobile.entity.Job;
import java.util.List;

@Dao
public interface JobDao {
    @Insert
    long insertJob(Job job);
    
    @Update
    void updateJob(Job job);
    
    @Delete
    void deleteJob(Job job);
    
    @Query("SELECT * FROM jobs WHERE status = 'aktif' ORDER BY tanggalPosting DESC")
    List<Job> getAllActiveJobs();
    
    @Query("SELECT * FROM jobs WHERE employerId = :employerId ORDER BY tanggalPosting DESC")
    List<Job> getJobsByEmployer(int employerId);
    
    @Query("SELECT * FROM jobs WHERE id = :id")
    Job getJobById(int id);
    
    @Query("SELECT * FROM jobs WHERE status = 'aktif' AND " +
           "(judulPekerjaan LIKE '%' || :query || '%' OR " +
           "namaPerusahaan LIKE '%' || :query || '%' OR " +
           "lokasi LIKE '%' || :query || '%')")
    List<Job> searchJobs(String query);
    
    @Query("SELECT * FROM jobs WHERE status = 'aktif' AND " +
           "(:query IS NULL OR :query = '' OR judulPekerjaan LIKE '%' || :query || '%' OR " +
           "namaPerusahaan LIKE '%' || :query || '%' OR " +
           "lokasi LIKE '%' || :query || '%') AND " +
           "(:tipePekerjaan IS NULL OR :tipePekerjaan = '' OR tipePekerjaan = :tipePekerjaan) AND " +
           "(:gajiMin IS NULL OR gajiMax >= :gajiMin)")
    List<Job> searchJobsWithFilter(String query, String tipePekerjaan, Integer gajiMin);
}


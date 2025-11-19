package com.example.uaswebmobile.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "jobs")
public class Job {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String judulPekerjaan;
    public String namaPerusahaan;
    public String lokasi;
    public String tipePekerjaan; // "full-time", "part-time", "freelance"
    public int gajiMin;
    public int gajiMax;
    public String deskripsi;
    public String tanggalPosting;
    public String status; // "aktif" or "nonaktif"
    public int employerId; // ID dari user yang membuat lowongan
    
    public Job() {}
    
    @Ignore
    public Job(String judulPekerjaan, String namaPerusahaan, String lokasi, 
               String tipePekerjaan, int gajiMin, int gajiMax, String deskripsi, 
               String tanggalPosting, String status, int employerId) {
        this.judulPekerjaan = judulPekerjaan;
        this.namaPerusahaan = namaPerusahaan;
        this.lokasi = lokasi;
        this.tipePekerjaan = tipePekerjaan;
        this.gajiMin = gajiMin;
        this.gajiMax = gajiMax;
        this.deskripsi = deskripsi;
        this.tanggalPosting = tanggalPosting;
        this.status = status;
        this.employerId = employerId;
    }
}


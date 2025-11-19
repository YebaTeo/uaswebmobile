package com.example.uaswebmobile.util;

import android.content.Context;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.entity.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseInitializer {
    
    public static void initializeDatabase(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        
        // Check if database is already initialized
        User existingUser = database.userDao().getUserByUsername("jobseeker1");
        if (existingUser != null) {
            return; // Database already initialized
        }
        
        // Create sample Job Seeker accounts
        User jobSeeker1 = new User("jobseeker1", "jobseeker1@email.com", "password123", "job_seeker");
        jobSeeker1.name = "Ahmad Rizki";
        jobSeeker1.location = "Jakarta";
        jobSeeker1.headline = "Software Developer dengan pengalaman 3 tahun";
        jobSeeker1.expectedSalary = "8000000";
        jobSeeker1.cvLink = "https://example.com/cv/ahmad-rizki";
        database.userDao().insertUser(jobSeeker1);
        
        User jobSeeker2 = new User("jobseeker2", "jobseeker2@email.com", "password123", "job_seeker");
        jobSeeker2.name = "Siti Nurhaliza";
        jobSeeker2.location = "Bandung";
        jobSeeker2.headline = "UI/UX Designer";
        jobSeeker2.expectedSalary = "7000000";
        jobSeeker2.cvLink = "https://example.com/cv/siti-nurhaliza";
        database.userDao().insertUser(jobSeeker2);
        
        // Create sample Employer accounts
        User employer1 = new User("employer1", "employer1@company.com", "password123", "employer");
        employer1.name = "PT Digital Karya Nusantara";
        database.userDao().insertUser(employer1);
        
        User employer2 = new User("employer2", "employer2@company.com", "password123", "employer");
        employer2.name = "PT Teknologi Indonesia";
        database.userDao().insertUser(employer2);
        
        // Get employer IDs
        User emp1 = database.userDao().getUserByUsername("employer1");
        User emp2 = database.userDao().getUserByUsername("employer2");
        
        if (emp1 != null && emp2 != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String today = sdf.format(new Date());
            
            // Create sample jobs for employer1
            Job job1 = new Job("Software Developer", "PT Digital Karya Nusantara", 
                    "Jakarta", "full-time", 8000000, 12000000, 
                    "Kami mencari Software Developer yang berpengalaman dalam Java dan Android development. Bertanggung jawab untuk mengembangkan aplikasi mobile Android.", 
                    today, "aktif", emp1.id);
            database.jobDao().insertJob(job1);
            
            Job job2 = new Job("UI/UX Designer", "PT Digital Karya Nusantara", 
                    "Jakarta", "full-time", 7000000, 10000000, 
                    "Mencari UI/UX Designer kreatif dengan pengalaman minimal 2 tahun. Harus menguasai Figma, Adobe XD, dan design thinking.", 
                    today, "aktif", emp1.id);
            database.jobDao().insertJob(job2);
            
            Job job3 = new Job("Backend Developer", "PT Digital Karya Nusantara", 
                    "Bandung", "part-time", 5000000, 8000000, 
                    "Lowongan part-time untuk Backend Developer. Menguasai Node.js, Express, dan database MySQL/PostgreSQL.", 
                    today, "aktif", emp1.id);
            database.jobDao().insertJob(job3);
            
            // Create sample jobs for employer2
            Job job4 = new Job("Frontend Developer", "PT Teknologi Indonesia", 
                    "Surabaya", "full-time", 9000000, 13000000, 
                    "Mencari Frontend Developer yang ahli dalam React, Vue.js, dan modern JavaScript frameworks.", 
                    today, "aktif", emp2.id);
            database.jobDao().insertJob(job4);
            
            Job job5 = new Job("Mobile App Developer", "PT Teknologi Indonesia", 
                    "Yogyakarta", "freelance", 10000000, 15000000, 
                    "Proyek freelance untuk mengembangkan aplikasi mobile iOS dan Android. Durasi 3-6 bulan.", 
                    today, "aktif", emp2.id);
            database.jobDao().insertJob(job5);
        }
    }
}


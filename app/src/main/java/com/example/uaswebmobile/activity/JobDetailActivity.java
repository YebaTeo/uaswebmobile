package com.example.uaswebmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Bookmark;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.entity.Notification;
import com.example.uaswebmobile.entity.User;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JobDetailActivity extends AppCompatActivity {
    private TextView tvJobTitle, tvCompany, tvLocation, tvType, tvSalary, tvDescription, tvDate;
    private TextView tvQualification, tvStatus, tvCategory;
    private Button btnApply, btnBookmark;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private Job job;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        int jobId = getIntent().getIntExtra("job_id", -1);
        if (jobId == -1) {
            NotificationHelper.showError(this, "Error", "Job tidak ditemukan");
            finish();
            return;
        }

        job = database.jobDao().getJobById(jobId);
        if (job == null) {
            NotificationHelper.showError(this, "Error", "Job tidak ditemukan");
            finish();
            return;
        }

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvCompany = findViewById(R.id.tvCompany);
        tvLocation = findViewById(R.id.tvLocation);
        tvType = findViewById(R.id.tvType);
        tvSalary = findViewById(R.id.tvSalary);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvQualification = findViewById(R.id.tvQualification);
        tvStatus = findViewById(R.id.tvStatus);
        tvCategory = findViewById(R.id.tvCategory);
        btnApply = findViewById(R.id.btnApply);
        btnBookmark = findViewById(R.id.btnBookmark);

        displayJobDetails();
        checkBookmarkStatus();
        checkApplicationStatus();

        btnBookmark.setOnClickListener(v -> toggleBookmark());
        btnApply.setOnClickListener(v -> applyJob());
    }

    private void displayJobDetails() {
        tvJobTitle.setText(job.judulPekerjaan);
        tvCompany.setText(job.namaPerusahaan);
        tvLocation.setText(job.lokasi);
        
        // Format job type
        String jobTypeFormatted = job.tipePekerjaan;
        if (jobTypeFormatted != null && !jobTypeFormatted.isEmpty()) {
            jobTypeFormatted = jobTypeFormatted.substring(0, 1).toUpperCase() + 
                             jobTypeFormatted.substring(1).toLowerCase();
        }
        tvType.setText(jobTypeFormatted);
        
        // Format salary
        tvSalary.setText(formatCurrency(job.gajiMin) + " - " + formatCurrency(job.gajiMax));
        
        // Format description with bullet points if needed
        String description = job.deskripsi != null ? job.deskripsi : "Tidak ada deskripsi";
        tvDescription.setText(formatTextWithBullets(description));
        
        // Format date - show relative time if possible
        tvDate.setText(formatDate(job.tanggalPosting));
        
        // Qualification (using description as placeholder, can be enhanced)
        String qualification = "• Pendidikan minimal sesuai dengan persyaratan\n" +
                              "• Pengalaman kerja yang relevan\n" +
                              "• Kemampuan komunikasi yang baik\n" +
                              "• Mampu bekerja dalam tim\n" +
                              "• Disiplin dan bertanggung jawab";
        tvQualification.setText(qualification);
        
        // Status
        String status = job.status != null ? job.status : "Aktif";
        tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase());
        
        // Category (placeholder)
        tvCategory.setText("Manufaktur, Transportasi & Logistik");
    }
    
    private String formatTextWithBullets(String text) {
        if (text == null || text.isEmpty()) {
            return "Tidak ada deskripsi";
        }
        // If text contains line breaks, add bullet points
        if (text.contains("\n")) {
            String[] lines = text.split("\n");
            StringBuilder formatted = new StringBuilder();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    if (!line.trim().startsWith("•")) {
                        formatted.append("• ").append(line.trim());
                    } else {
                        formatted.append(line.trim());
                    }
                    formatted.append("\n");
                }
            }
            return formatted.toString().trim();
        }
        return text;
    }
    
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Diposting baru-baru ini";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            if (date != null) {
                long diff = System.currentTimeMillis() - date.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                
                if (days == 0) {
                    return "Diposting hari ini";
                } else if (days == 1) {
                    return "Diposting 1 hari yang lalu";
                } else if (days < 7) {
                    return "Diposting " + days + " hari yang lalu";
                } else if (days < 30) {
                    long weeks = days / 7;
                    return "Diposting " + weeks + " minggu yang lalu";
                } else {
                    return "Diposting: " + dateString;
                }
            }
        } catch (Exception e) {
            // If parsing fails, return original
        }
        return "Diposting: " + dateString;
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    private void checkBookmarkStatus() {
        int userId = sharedPrefManager.getUserId();
        Bookmark bookmark = database.bookmarkDao().getBookmarkByJobAndSeeker(job.id, userId);
        isBookmarked = bookmark != null;
        btnBookmark.setText(isBookmarked ? "Tersimpan" : "Simpan");
    }

    private void checkApplicationStatus() {
        int userId = sharedPrefManager.getUserId();
        Application application = database.applicationDao().getApplicationByJobAndSeeker(job.id, userId);
        if (application != null) {
            btnApply.setEnabled(false);
            btnApply.setText("Sudah Dilamar");
        }
    }

    private void toggleBookmark() {
        int userId = sharedPrefManager.getUserId();
        if (isBookmarked) {
            Bookmark bookmark = database.bookmarkDao().getBookmarkByJobAndSeeker(job.id, userId);
            if (bookmark != null) {
                database.bookmarkDao().deleteBookmark(bookmark);
                NotificationHelper.showSuccess(this, "Berhasil", "Bookmark dihapus");
            }
        } else {
            Bookmark bookmark = new Bookmark(job.id, userId);
            database.bookmarkDao().insertBookmark(bookmark);
            NotificationHelper.showSuccess(this, "Berhasil", "Job di-bookmark");
        }
        checkBookmarkStatus();
    }

    private void applyJob() {
        int userId = sharedPrefManager.getUserId();

        // Check if already applied
        Application existing = database.applicationDao().getApplicationByJobAndSeeker(job.id, userId);
        if (existing != null) {
            NotificationHelper.showWarning(this, "Peringatan", "Anda sudah melamar pekerjaan ini");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String tanggalLamaran = sdf.format(new Date());

        Application application = new Application(job.id, userId, "submitted", tanggalLamaran);
        long result = database.applicationDao().insertApplication(application);

        if (result > 0) {
            sendApplicationNotifications(application);
            NotificationHelper.showSuccess(this, "Berhasil", "Lamaran berhasil dikirim!");
            btnApply.setEnabled(false);
            btnApply.setText("Sudah Dilamar");
        } else {
            NotificationHelper.showError(this, "Gagal", "Gagal mengirim lamaran. Silakan coba lagi.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendApplicationNotifications(Application application) {
        String timestamp = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"))
                .format(new Date());

        User seeker = database.userDao().getUserById(application.jobSeekerId);
        String seekerName = seeker != null && seeker.name != null && !seeker.name.isEmpty()
                ? seeker.name
                : sharedPrefManager.getUsername();

        Notification seekerNotification = new Notification(application.jobSeekerId,
                "Lamaran terkirim",
                "Lamaran kamu ke " + job.judulPekerjaan + " sudah kami terima.",
                "success",
                timestamp);
        database.notificationDao().insertNotification(seekerNotification);

        Notification employerNotification = new Notification(job.employerId,
                "Lamaran baru masuk",
                "Kandidat " + seekerName + " melamar posisi " + job.judulPekerjaan + ".",
                "info",
                timestamp);
        database.notificationDao().insertNotification(employerNotification);
    }
}

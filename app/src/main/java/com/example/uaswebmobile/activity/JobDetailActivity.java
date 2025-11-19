package com.example.uaswebmobile.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Bookmark;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JobDetailActivity extends AppCompatActivity {
    private TextView tvJobTitle, tvCompany, tvLocation, tvType, tvSalary, tvDescription, tvDate;
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
            Toast.makeText(this, "Job tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        job = database.jobDao().getJobById(jobId);
        if (job == null) {
            Toast.makeText(this, "Job tidak ditemukan", Toast.LENGTH_SHORT).show();
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
        tvType.setText(job.tipePekerjaan);
        tvSalary.setText(formatCurrency(job.gajiMin) + " - " + formatCurrency(job.gajiMax));
        tvDescription.setText(job.deskripsi);
        tvDate.setText("Diposting: " + job.tanggalPosting);

        // Set background and text color based on job type
        if ("full-time".equalsIgnoreCase(job.tipePekerjaan)) {
            tvType.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_job_type_full_time));
        } else if ("part-time".equalsIgnoreCase(job.tipePekerjaan)) {
            tvType.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_job_type_part_time));
        } else {
            tvType.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_job_type_default));
        }
        tvType.setTextColor(Color.WHITE);
        tvType.setPadding(16, 8, 16, 8);
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    private void checkBookmarkStatus() {
        int userId = sharedPrefManager.getUserId();
        Bookmark bookmark = database.bookmarkDao().getBookmarkByJobAndSeeker(job.id, userId);
        isBookmarked = bookmark != null;
        btnBookmark.setText(isBookmarked ? "Hapus Bookmark" : "Bookmark");
    }

    private void checkApplicationStatus() {
        int userId = sharedPrefManager.getUserId();
        Application application = database.applicationDao().getApplicationByJobAndSeeker(job.id, userId);
        if (application != null) {
            btnApply.setEnabled(false);
            btnApply.setText("Sudah Dilamar - " + application.status);
        }
    }

    private void toggleBookmark() {
        int userId = sharedPrefManager.getUserId();
        if (isBookmarked) {
            Bookmark bookmark = database.bookmarkDao().getBookmarkByJobAndSeeker(job.id, userId);
            if (bookmark != null) {
                database.bookmarkDao().deleteBookmark(bookmark);
                Toast.makeText(this, "Bookmark dihapus", Toast.LENGTH_SHORT).show();
            }
        } else {
            Bookmark bookmark = new Bookmark(job.id, userId);
            database.bookmarkDao().insertBookmark(bookmark);
            Toast.makeText(this, "Job di-bookmark", Toast.LENGTH_SHORT).show();
        }
        checkBookmarkStatus();
    }

    private void applyJob() {
        int userId = sharedPrefManager.getUserId();

        // Check if already applied
        Application existing = database.applicationDao().getApplicationByJobAndSeeker(job.id, userId);
        if (existing != null) {
            Toast.makeText(this, "Anda sudah melamar pekerjaan ini", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String tanggalLamaran = sdf.format(new Date());

        Application application = new Application(job.id, userId, "submitted", tanggalLamaran);
        long result = database.applicationDao().insertApplication(application);

        if (result > 0) {
            Toast.makeText(this, "Lamaran berhasil dikirim!", Toast.LENGTH_SHORT).show();
            btnApply.setEnabled(false);
            btnApply.setText("Sudah Dilamar - submitted");
        } else {
            Toast.makeText(this, "Gagal mengirim lamaran", Toast.LENGTH_SHORT).show();
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
}

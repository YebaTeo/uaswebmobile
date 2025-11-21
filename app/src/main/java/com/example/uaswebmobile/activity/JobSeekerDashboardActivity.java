package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;

public class JobSeekerDashboardActivity extends AppCompatActivity {
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private TextView tvTotalJobs, tvAppliedJobs, tvBookmarkedJobs, tvPendingApplications;
    private CardView cvSearchJobs, cvMyApplications, cvBookmarks, cvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"job_seeker".equals(sharedPrefManager.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        loadStatistics();
        setupClickListeners();
    }

    private void initViews() {
        tvTotalJobs = findViewById(R.id.tvTotalJobs);
        tvAppliedJobs = findViewById(R.id.tvAppliedJobs);
        tvBookmarkedJobs = findViewById(R.id.tvBookmarkedJobs);
        tvPendingApplications = findViewById(R.id.tvPendingApplications);
        cvSearchJobs = findViewById(R.id.cvSearchJobs);
        cvMyApplications = findViewById(R.id.cvMyApplications);
        cvBookmarks = findViewById(R.id.cvBookmarks);
        cvProfile = findViewById(R.id.cvProfile);
    }

    private void loadStatistics() {
        int userId = sharedPrefManager.getUserId();
        
        int totalJobs = database.jobDao().getAllActiveJobs().size();
        int appliedJobs = database.applicationDao().getApplicationsByUser(userId).size();
        int bookmarkedJobs = database.bookmarkDao().getBookmarksByUser(userId).size();
        int pendingApps = database.applicationDao().getApplicationsByUserAndStatus(userId, "submitted").size();

        tvTotalJobs.setText(String.valueOf(totalJobs));
        tvAppliedJobs.setText(String.valueOf(appliedJobs));
        tvBookmarkedJobs.setText(String.valueOf(bookmarkedJobs));
        tvPendingApplications.setText(String.valueOf(pendingApps));
    }

    private void setupClickListeners() {
        cvSearchJobs.setOnClickListener(v -> {
            startActivity(new Intent(this, JobListActivity.class));
        });

        cvMyApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, MyApplicationsActivity.class));
        });

        cvBookmarks.setOnClickListener(v -> {
            startActivity(new Intent(this, BookmarkActivity.class));
        });

        cvProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, JobSeekerProfileActivity.class));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_job_seeker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menuDashboard) {
            return true;
        } else if (id == R.id.menuProfile) {
            startActivity(new Intent(this, JobSeekerProfileActivity.class));
            return true;
        } else if (id == R.id.menuApplications) {
            startActivity(new Intent(this, MyApplicationsActivity.class));
            return true;
        } else if (id == R.id.menuBookmarks) {
            startActivity(new Intent(this, BookmarkActivity.class));
            return true;
        } else if (id == R.id.menuStatistics) {
            startActivity(new Intent(this, StatisticsActivity.class));
            return true;
        } else if (id == R.id.menuNotifications) {
            startActivity(new Intent(this, NotificationsActivity.class));
            return true;
        } else if (id == R.id.menuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menuLogout) {
            NotificationHelper.showConfirm(this, "Konfirmasi Logout", 
                "Apakah Anda yakin ingin logout?", () -> {
                    sharedPrefManager.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
    }
}


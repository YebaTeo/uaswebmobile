package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.util.List;

public class EmployerDashboardActivity extends AppCompatActivity {
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private TextView tvTotalJobs, tvActiveJobs, tvTotalApplications, tvPendingApplications;
    private CardView cvManageJobs, cvApplications, cvAddJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"employer".equals(sharedPrefManager.getRole())) {
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
        tvActiveJobs = findViewById(R.id.tvActiveJobs);
        tvTotalApplications = findViewById(R.id.tvTotalApplications);
        tvPendingApplications = findViewById(R.id.tvPendingApplications);
        cvManageJobs = findViewById(R.id.cvManageJobs);
        cvApplications = findViewById(R.id.cvApplications);
        cvAddJob = findViewById(R.id.cvAddJob);
    }

    private void loadStatistics() {
        int employerId = sharedPrefManager.getUserId();
        
        List<Job> jobs = database.jobDao().getJobsByEmployer(employerId);
        int totalJobs = jobs.size();
        int activeJobs = 0;
        for (Job job : jobs) {
            if ("aktif".equals(job.status)) {
                activeJobs++;
            }
        }
        
        int totalApplications = 0;
        int pendingApplications = 0;
        for (Job job : jobs) {
            List<Application> applications = database.applicationDao().getApplicationsByJob(job.id);
            totalApplications += applications.size();
            for (Application application : applications) {
                if ("submitted".equalsIgnoreCase(application.status) ||
                        "pending".equalsIgnoreCase(application.status)) {
                    pendingApplications++;
                }
            }
        }

        tvTotalJobs.setText(String.valueOf(totalJobs));
        tvActiveJobs.setText(String.valueOf(activeJobs));
        tvTotalApplications.setText(String.valueOf(totalApplications));
        tvPendingApplications.setText(String.valueOf(pendingApplications));
    }

    private void setupClickListeners() {
        cvManageJobs.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployerJobManagementActivity.class));
        });

        cvApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, ApplicationManagementActivity.class));
        });

        cvAddJob.setOnClickListener(v -> {
            startActivity(new Intent(this, AddJobActivity.class));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuApplications) {
            startActivity(new Intent(this, ApplicationManagementActivity.class));
            return true;
        } else if (id == R.id.menuAnalytics) {
            startActivity(new Intent(this, AnalyticsActivity.class));
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
        } else if (id == android.R.id.home) {
            onBackPressed();
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


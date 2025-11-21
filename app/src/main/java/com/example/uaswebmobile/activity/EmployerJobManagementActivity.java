package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.adapter.JobAdapter;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class EmployerJobManagementActivity extends AppCompatActivity {
    private RecyclerView rvJobs;
    private JobAdapter adapter;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private FloatingActionButton fabAddJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_job_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kelola Lowongan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"employer".equals(sharedPrefManager.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvJobs = findViewById(R.id.rvJobs);
        fabAddJob = findViewById(R.id.fabAddJob);

        adapter = new JobAdapter(null, job -> {
            Intent intent = new Intent(this, EditJobActivity.class);
            intent.putExtra("job_id", job.id);
            startActivity(intent);
        });

        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        rvJobs.setAdapter(adapter);

        fabAddJob.setOnClickListener(v -> {
            startActivity(new Intent(this, AddJobActivity.class));
        });

        loadJobs();
    }

    private void loadJobs() {
        int employerId = sharedPrefManager.getUserId();
        List<Job> jobs = database.jobDao().getJobsByEmployer(employerId);
        adapter.updateList(jobs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuDashboard) {
            startActivity(new Intent(this, EmployerDashboardActivity.class));
            return true;
        } else if (id == R.id.menuApplications) {
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
        loadJobs();
    }
}

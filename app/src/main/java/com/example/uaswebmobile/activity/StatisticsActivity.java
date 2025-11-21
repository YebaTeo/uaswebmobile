package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;

public class StatisticsActivity extends AppCompatActivity {
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Statistik");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"job_seeker".equals(sharedPrefManager.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadStatistics();
    }

    private void loadStatistics() {
        int userId = sharedPrefManager.getUserId();
        // Load and display statistics
        // This can be expanded with charts/graphs
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
            finish();
            return true;
        }
        return handleJobSeekerMenuSelection(id) || super.onOptionsItemSelected(item);
    }

    private boolean handleJobSeekerMenuSelection(int itemId) {
        if (itemId == R.id.menuDashboard) {
            startActivity(new Intent(this, JobSeekerDashboardActivity.class));
            return true;
        } else if (itemId == R.id.menuProfile) {
            startActivity(new Intent(this, JobSeekerProfileActivity.class));
            return true;
        } else if (itemId == R.id.menuApplications) {
            startActivity(new Intent(this, MyApplicationsActivity.class));
            return true;
        } else if (itemId == R.id.menuBookmarks) {
            startActivity(new Intent(this, BookmarkActivity.class));
            return true;
        } else if (itemId == R.id.menuStatistics) {
            return true;
        } else if (itemId == R.id.menuNotifications) {
            startActivity(new Intent(this, NotificationsActivity.class));
            return true;
        } else if (itemId == R.id.menuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (itemId == R.id.menuLogout) {
            NotificationHelper.showConfirm(this, "Konfirmasi Logout",
                    "Apakah Anda yakin ingin logout?", () -> {
                        sharedPrefManager.logout();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
            return true;
        }
        return false;
    }
}


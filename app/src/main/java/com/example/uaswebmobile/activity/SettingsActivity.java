package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;

public class SettingsActivity extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pengaturan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String role = sharedPrefManager.getRole();
        if ("employer".equals(role)) {
            getMenuInflater().inflate(R.menu.menu_employer, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_job_seeker, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        boolean handled;
        if ("employer".equals(sharedPrefManager.getRole())) {
            handled = handleEmployerMenu(id);
        } else {
            handled = handleJobSeekerMenu(id);
        }
        return handled || super.onOptionsItemSelected(item);
    }

    private boolean handleJobSeekerMenu(int itemId) {
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
            startActivity(new Intent(this, StatisticsActivity.class));
            return true;
        } else if (itemId == R.id.menuNotifications) {
            startActivity(new Intent(this, NotificationsActivity.class));
            return true;
        } else if (itemId == R.id.menuSettings) {
            return true;
        } else if (itemId == R.id.menuLogout) {
            showLogoutConfirm();
            return true;
        }
        return false;
    }

    private boolean handleEmployerMenu(int itemId) {
        if (itemId == R.id.menuDashboard) {
            startActivity(new Intent(this, EmployerDashboardActivity.class));
            return true;
        } else if (itemId == R.id.menuApplications) {
            startActivity(new Intent(this, ApplicationManagementActivity.class));
            return true;
        } else if (itemId == R.id.menuAnalytics) {
            startActivity(new Intent(this, AnalyticsActivity.class));
            return true;
        } else if (itemId == R.id.menuNotifications) {
            startActivity(new Intent(this, NotificationsActivity.class));
            return true;
        } else if (itemId == R.id.menuSettings) {
            return true;
        } else if (itemId == R.id.menuLogout) {
            showLogoutConfirm();
            return true;
        }
        return false;
    }

    private void showLogoutConfirm() {
        NotificationHelper.showConfirm(this, "Konfirmasi Logout",
                "Apakah Anda yakin ingin logout?", () -> {
                    sharedPrefManager.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
    }
}


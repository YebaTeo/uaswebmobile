package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.adapter.NotificationAdapter;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Notification;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;
    private AppDatabase database;
    private NotificationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View layoutEmptyState;
    private MaterialButton btnMarkAllRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notifikasi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);

        adapter = new NotificationAdapter(notification -> {
            database.notificationDao().markAsRead(notification.id);
            loadNotifications();
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        btnMarkAllRead.setOnClickListener(v -> {
            database.notificationDao().markAllAsRead(sharedPrefManager.getUserId());
            loadNotifications();
        });

        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);

        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        if (sharedPrefManager.getUserId() == -1) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        List<Notification> notifications = database.notificationDao()
                .getNotificationsByUser(sharedPrefManager.getUserId());
        adapter.submitList(notifications);

        boolean isEmpty = notifications == null || notifications.isEmpty();
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        btnMarkAllRead.setEnabled(!isEmpty);
        swipeRefreshLayout.setRefreshing(false);
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
            return true;
        } else if (itemId == R.id.menuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
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
            return true;
        } else if (itemId == R.id.menuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
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


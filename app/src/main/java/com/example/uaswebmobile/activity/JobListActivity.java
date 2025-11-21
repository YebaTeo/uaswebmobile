package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uaswebmobile.util.NotificationHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.adapter.JobAdapter;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;

public class JobListActivity extends AppCompatActivity {
    private RecyclerView rvJobs;
    private EditText etSearch;
    private Spinner spJobType, spSalaryRange;
    private JobAdapter adapter;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private List<Job> allJobs;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Lowongan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvJobs = findViewById(R.id.rvJobs);
        etSearch = findViewById(R.id.etSearch);
        spJobType = findViewById(R.id.spJobType);
        spSalaryRange = findViewById(R.id.spSalaryRange);
        ivProfile = findViewById(R.id.ivProfile);

        ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, JobSeekerProfileActivity.class));
        });

        // Setup spinners
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_types, android.R.layout.simple_spinner_item);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJobType.setAdapter(jobTypeAdapter);

        ArrayAdapter<CharSequence> salaryAdapter = ArrayAdapter.createFromResource(
                this, R.array.salary_ranges, android.R.layout.simple_spinner_item);
        salaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSalaryRange.setAdapter(salaryAdapter);

        spJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterJobs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spSalaryRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterJobs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapter = new JobAdapter(new ArrayList<>(), job -> {
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra("job_id", job.id);
            startActivity(intent);
        });

        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        rvJobs.setAdapter(adapter);

        loadJobs();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadJobs() {
        allJobs = database.jobDao().getAllActiveJobs();
        adapter.updateList(allJobs);
    }

    private void filterJobs() {
        String query = etSearch.getText().toString().trim();
        String jobType = null;
        if (spJobType.getSelectedItemPosition() > 0) {
            jobType = spJobType.getSelectedItem().toString().toLowerCase();
        }
        Integer salaryMin = null;

        if (spSalaryRange.getSelectedItemPosition() > 0) {
            String salaryRange = spSalaryRange.getSelectedItem().toString();
            if (salaryRange.contains(">")) {
                salaryMin = 10000000; // > 10 juta
            } else if (salaryRange.contains("5") && salaryRange.contains("-")) {
                salaryMin = 5000000; // 5-10 juta
            } else if (salaryRange.contains("<")) {
                salaryMin = 0; // < 5 juta
            }
        }

        List<Job> filteredJobs;
        if (query.isEmpty() && jobType == null && salaryMin == null) {
            filteredJobs = allJobs;
        } else {
            filteredJobs = database.jobDao().searchJobsWithFilter(
                    query.isEmpty() ? null : query,
                    jobType,
                    salaryMin
            );
        }

        adapter.updateList(filteredJobs);
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
            startActivity(new Intent(this, JobSeekerDashboardActivity.class));
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
        loadJobs();
    }
}

package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.User;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import java.text.NumberFormat;
import java.util.Locale;

public class JobSeekerProfileActivity extends AppCompatActivity {
    public static final String EXTRA_JOBSEEKER_ID = "JOBSEEKER_ID";
    
    private EditText etName, etLocation, etHeadline, etExpectedSalary, etCvLink;
    private Button btnSave;
    private MaterialButton btnViewCv;
    private TextView tvHeaderName, tvHeaderHeadline, tvHeaderLocation, tvExpectedSalarySummary, tvCvLinkSummary;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;
    private boolean isViewingOtherProfile = false;

    private static final String TAG = "JobSeekerProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting JobSeekerProfileActivity");
        super.onCreate(savedInstanceState);
        
        try {
            // Initialize database and shared preferences first
            database = AppDatabase.getDatabase(this);
            sharedPrefManager = new SharedPrefManager(this);
            
            // Check for required extras
            if (getIntent() == null) {
                Log.e(TAG, "No intent found");
                finish();
                return;
            }
            
            // Log the entire intent
            Log.d(TAG, "Intent action: " + getIntent().getAction());
            Log.d(TAG, "Intent extras: " + (getIntent().getExtras() != null ? getIntent().getExtras().toString() : "null"));
            
            setContentView(R.layout.activity_job_seeker_profile);
            Log.d(TAG, "Content view set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            throw e; // Re-throw to see the crash report
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profil Saya");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // Check if we're viewing another jobseeker's profile
        int jobseekerId = getIntent().getIntExtra(EXTRA_JOBSEEKER_ID, -1);
        Log.d(TAG, "Jobseeker ID from intent: " + jobseekerId);
        Log.d(TAG, "Current user ID: " + sharedPrefManager.getUserId());
        
        if (jobseekerId != -1 && jobseekerId != sharedPrefManager.getUserId()) {
            isViewingOtherProfile = true;
            try {
                currentUser = database.userDao().getUserById(jobseekerId);
                Log.d(TAG, "Loaded user: " + (currentUser != null ? currentUser.name : "null"));
                if (currentUser == null) {
                    // Jobseeker not found, go back
                    Log.e(TAG, "Jobseeker not found with ID: " + jobseekerId);
                    finish();
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading user: " + e.getMessage(), e);
                finish();
                return;
            }
            // Disable editing for other profiles
            setupViewMode();
        } else {
            // Viewing own profile
            try {
                currentUser = database.userDao().getUserById(sharedPrefManager.getUserId());
                Log.d(TAG, "Loaded user: " + (currentUser != null ? currentUser.name : "null"));
                if (currentUser == null) {
                    sharedPrefManager.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading user: " + e.getMessage(), e);
                sharedPrefManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
        }

        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvHeaderHeadline = findViewById(R.id.tvHeaderHeadline);
        tvHeaderLocation = findViewById(R.id.tvHeaderLocation);
        tvExpectedSalarySummary = findViewById(R.id.tvExpectedSalarySummary);
        tvCvLinkSummary = findViewById(R.id.tvCvLinkSummary);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etHeadline = findViewById(R.id.etHeadline);
        etExpectedSalary = findViewById(R.id.etExpectedSalary);
        etCvLink = findViewById(R.id.etCvLink);
        btnSave = findViewById(R.id.btnSave);
        btnViewCv = findViewById(R.id.btnViewCv);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
        btnViewCv.setOnClickListener(v -> openCvLink());
    }

    private void loadProfile() {
        if (currentUser != null) {
            etName.setText(currentUser.name != null ? currentUser.name : "");
            etLocation.setText(currentUser.location != null ? currentUser.location : "");
            etHeadline.setText(currentUser.headline != null ? currentUser.headline : "");
            etExpectedSalary.setText(currentUser.expectedSalary != null ? currentUser.expectedSalary : "");
            etCvLink.setText(currentUser.cvLink != null ? currentUser.cvLink : "");
            bindProfileSummary();
        }
    }

    private void saveProfile() {
        if (isViewingOtherProfile) {
            return;
        }
        currentUser.name = etName.getText().toString().trim();
        currentUser.location = etLocation.getText().toString().trim();
        currentUser.headline = etHeadline.getText().toString().trim();
        currentUser.expectedSalary = etExpectedSalary.getText().toString().trim();
        currentUser.cvLink = etCvLink.getText().toString().trim();

        database.userDao().updateUser(currentUser);
        NotificationHelper.showSuccess(this, "Berhasil", "Profil berhasil diperbarui");
        bindProfileSummary();
    }

    private void setupViewMode() {
        try {
            Log.d(TAG, "Setting up view mode");
            // Initialize views if not already done
            if (etName == null) {
                initializeViews();
            }
            
            // Disable all input fields
            etName.setEnabled(false);
            etLocation.setEnabled(false);
            etHeadline.setEnabled(false);
            etExpectedSalary.setEnabled(false);
            etCvLink.setEnabled(false);
            
            // Hide save button
            if (btnSave != null) {
                btnSave.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "Save button is null");
            }
            
            // Update toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Profil Pencari Kerja");
            } else {
                Log.e(TAG, "SupportActionBar is null");
            }
            
            // Update the UI to reflect view-only mode
            bindProfileSummary();
        } catch (Exception e) {
            Log.e(TAG, "Error in setupViewMode: " + e.getMessage(), e);
        }
    }
    
    private void initializeViews() {
        try {
            Log.d(TAG, "Initializing views");
            etName = findViewById(R.id.etName);
            etLocation = findViewById(R.id.etLocation);
            etHeadline = findViewById(R.id.etHeadline);
            etExpectedSalary = findViewById(R.id.etExpectedSalary);
            etCvLink = findViewById(R.id.etCvLink);
            btnSave = findViewById(R.id.btnSave);
            btnViewCv = findViewById(R.id.btnViewCv);
            tvHeaderName = findViewById(R.id.tvHeaderName);
            tvHeaderHeadline = findViewById(R.id.tvHeaderHeadline);
            tvHeaderLocation = findViewById(R.id.tvHeaderLocation);
            tvExpectedSalarySummary = findViewById(R.id.tvExpectedSalarySummary);
            tvCvLinkSummary = findViewById(R.id.tvCvLinkSummary);
            
            // Set click listeners
            if (btnSave != null) {
                btnSave.setOnClickListener(v -> saveProfile());
            }
            if (btnViewCv != null) {
                btnViewCv.setOnClickListener(v -> openCvLink());
            }
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize views", e);
        }
    }

    private void bindProfileSummary() {
        if (currentUser == null) return;

        String displayName = !TextUtils.isEmpty(currentUser.name) ? currentUser.name : sharedPrefManager.getUsername();
        tvHeaderName.setText(displayName != null ? displayName : "Job Seeker");

        String headline = !TextUtils.isEmpty(currentUser.headline) ? currentUser.headline : "Lengkapi headline profesional Anda";
        tvHeaderHeadline.setText(headline);

        String location = !TextUtils.isEmpty(currentUser.location) ? currentUser.location : "Lokasi belum ditentukan";
        tvHeaderLocation.setText(location);

        tvExpectedSalarySummary.setText(formatSalary(currentUser.expectedSalary));

        boolean hasCv = !TextUtils.isEmpty(currentUser.cvLink);
        tvCvLinkSummary.setText(hasCv ? currentUser.cvLink : "Belum tersedia");
        btnViewCv.setEnabled(hasCv);
        btnViewCv.setAlpha(hasCv ? 1f : 0.5f);
    }

    private String formatSalary(String salaryValue) {
        if (TextUtils.isEmpty(salaryValue)) {
            return "Belum ditentukan";
        }
        try {
            String normalized = salaryValue.replace(".", "").replace(",", "");
            double amount = Double.parseDouble(normalized);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            return format.format(amount).replace(",00", "");
        } catch (NumberFormatException e) {
            return salaryValue;
        }
    }

    private void openCvLink() {
        if (currentUser == null || TextUtils.isEmpty(currentUser.cvLink)) {
            NotificationHelper.showWarning(this, "Peringatan", "Link CV belum tersedia");
            return;
        }
        String link = currentUser.cvLink.trim();
        if (!link.startsWith("http")) {
            link = "https://" + link;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        } catch (Exception e) {
            NotificationHelper.showError(this, "Gagal", "Tidak dapat membuka tautan CV");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            sharedPrefManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.User;
import com.example.uaswebmobile.util.SharedPrefManager;

public class JobSeekerProfileActivity extends AppCompatActivity {
    private EditText etName, etLocation, etHeadline, etExpectedSalary, etCvLink;
    private Button btnSave;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profil Saya");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        int userId = sharedPrefManager.getUserId();
        currentUser = database.userDao().getUserById(userId);

        if (currentUser == null) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etHeadline = findViewById(R.id.etHeadline);
        etExpectedSalary = findViewById(R.id.etExpectedSalary);
        etCvLink = findViewById(R.id.etCvLink);
        btnSave = findViewById(R.id.btnSave);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        etName.setText(currentUser.name != null ? currentUser.name : "");
        etLocation.setText(currentUser.location != null ? currentUser.location : "");
        etHeadline.setText(currentUser.headline != null ? currentUser.headline : "");
        etExpectedSalary.setText(currentUser.expectedSalary != null ? currentUser.expectedSalary : "");
        etCvLink.setText(currentUser.cvLink != null ? currentUser.cvLink : "");
    }

    private void saveProfile() {
        currentUser.name = etName.getText().toString().trim();
        currentUser.location = etLocation.getText().toString().trim();
        currentUser.headline = etHeadline.getText().toString().trim();
        currentUser.expectedSalary = etExpectedSalary.getText().toString().trim();
        currentUser.cvLink = etCvLink.getText().toString().trim();

        database.userDao().updateUser(currentUser);
        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
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

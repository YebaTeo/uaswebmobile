package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.SharedPrefManager;

public class EditJobActivity extends AppCompatActivity {
    private EditText etJobTitle, etCompany, etLocation, etDescription, etSalaryMin, etSalaryMax;
    private Spinner spJobType, spStatus;
    private Button btnSave, btnDelete;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Lowongan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (job == null || job.employerId != sharedPrefManager.getUserId()) {
            Toast.makeText(this, "Job tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etJobTitle = findViewById(R.id.etJobTitle);
        etCompany = findViewById(R.id.etCompany);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etSalaryMin = findViewById(R.id.etSalaryMin);
        etSalaryMax = findViewById(R.id.etSalaryMax);
        spJobType = findViewById(R.id.spJobType);
        spStatus = findViewById(R.id.spStatus);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup spinners
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_types, android.R.layout.simple_spinner_item);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJobType.setAdapter(jobTypeAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        loadJobData();

        Button btnViewApplications = findViewById(R.id.btnViewApplications);

        btnSave.setOnClickListener(v -> updateJob());
        btnDelete.setOnClickListener(v -> deleteJob());

        if (btnViewApplications != null) {
            btnViewApplications.setOnClickListener(v -> {
                Intent intent = new Intent(this, ApplicationManagementActivity.class);
                intent.putExtra("job_id", job.id);
                startActivity(intent);
            });
        }
    }

    private void loadJobData() {
        etJobTitle.setText(job.judulPekerjaan);
        etCompany.setText(job.namaPerusahaan);
        etLocation.setText(job.lokasi);
        etDescription.setText(job.deskripsi);
        etSalaryMin.setText(String.valueOf(job.gajiMin));
        etSalaryMax.setText(String.valueOf(job.gajiMax));

        // Set spinner values
        String[] jobTypes = getResources().getStringArray(R.array.job_types);
        for (int i = 1; i < jobTypes.length; i++) { // Start from 1 to skip "Pilih Tipe"
            if (jobTypes[i].equalsIgnoreCase(job.tipePekerjaan)) {
                spJobType.setSelection(i);
                break;
            }
        }

        String[] statuses = getResources().getStringArray(R.array.job_status);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(job.status)) {
                spStatus.setSelection(i);
                break;
            }
        }
    }

    private void updateJob() {
        String jobTitle = etJobTitle.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String salaryMinStr = etSalaryMin.getText().toString().trim();
        String salaryMaxStr = etSalaryMax.getText().toString().trim();
        String jobType = spJobType.getSelectedItem().toString();
        String status = spStatus.getSelectedItem().toString();

        if (jobTitle.isEmpty() || company.isEmpty() || location.isEmpty() ||
            description.isEmpty() || salaryMinStr.isEmpty() || salaryMaxStr.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int salaryMin = Integer.parseInt(salaryMinStr);
            int salaryMax = Integer.parseInt(salaryMaxStr);

            if (salaryMin > salaryMax) {
                Toast.makeText(this, "Gaji minimum tidak boleh lebih besar dari maksimum", Toast.LENGTH_SHORT).show();
                return;
            }

            job.judulPekerjaan = jobTitle;
            job.namaPerusahaan = company;
            job.lokasi = location;
            job.deskripsi = description;
            job.gajiMin = salaryMin;
            job.gajiMax = salaryMax;
            job.tipePekerjaan = jobType;
            job.status = status;

            database.jobDao().updateJob(job);
            Toast.makeText(this, "Lowongan berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format gaji tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteJob() {
        database.jobDao().deleteJob(job);
        Toast.makeText(this, "Lowongan berhasil dihapus", Toast.LENGTH_SHORT).show();
        finish();
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

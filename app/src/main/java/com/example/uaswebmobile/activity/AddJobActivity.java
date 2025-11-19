package com.example.uaswebmobile.activity;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddJobActivity extends AppCompatActivity {
    private EditText etJobTitle, etCompany, etLocation, etDescription, etSalaryMin, etSalaryMax;
    private Spinner spJobType, spStatus;
    private Button btnSave;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tambah Lowongan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        etJobTitle = findViewById(R.id.etJobTitle);
        etCompany = findViewById(R.id.etCompany);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etSalaryMin = findViewById(R.id.etSalaryMin);
        etSalaryMax = findViewById(R.id.etSalaryMax);
        spJobType = findViewById(R.id.spJobType);
        spStatus = findViewById(R.id.spStatus);
        btnSave = findViewById(R.id.btnSave);

        // Setup spinners
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_types, android.R.layout.simple_spinner_item);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJobType.setAdapter(jobTypeAdapter);
        spJobType.setSelection(1); // Default to first job type (skip "Pilih Tipe")

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        btnSave.setOnClickListener(v -> saveJob());
    }

    private void saveJob() {
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

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String tanggalPosting = sdf.format(new Date());

            int employerId = sharedPrefManager.getUserId();
            Job job = new Job(jobTitle, company, location, jobType, salaryMin, salaryMax,
                            description, tanggalPosting, status, employerId);

            long result = database.jobDao().insertJob(job);

            if (result > 0) {
                Toast.makeText(this, "Lowongan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal menambahkan lowongan", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format gaji tidak valid", Toast.LENGTH_SHORT).show();
        }
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

package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.entity.Notification;
import com.example.uaswebmobile.entity.User;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.util.SharedPrefManager;
import com.example.uaswebmobile.activity.JobSeekerProfileActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationManagementActivity extends AppCompatActivity {
    private RecyclerView rvApplications;
    private Spinner spJobs;
    private View layoutEmptyState;
    private TextView tvEmptyState;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private int selectedJobId = -1;
    private ApplicationAdapter adapter;
    private List<Job> employerJobs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kelola Lamaran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"employer".equals(sharedPrefManager.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvApplications = findViewById(R.id.rvApplications);
        spJobs = findViewById(R.id.spJobs);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        adapter = new ApplicationAdapter();
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(adapter);

        setupJobFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupJobFilter();
    }

    private void setupJobFilter() {
        int previousSelection = selectedJobId;
        int employerId = sharedPrefManager.getUserId();
        employerJobs = database.jobDao().getJobsByEmployer(employerId);

        if (employerJobs == null || employerJobs.isEmpty()) {
            showEmptyState("Anda belum memiliki lowongan untuk mengelola lamaran");
            spJobs.setEnabled(false);
            return;
        }
        spJobs.setEnabled(true);

        List<String> jobTitles = new ArrayList<>();
        for (Job job : employerJobs) {
            String title = job.judulPekerjaan + " • " + job.namaPerusahaan;
            jobTitles.add(title);
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, jobTitles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJobs.setAdapter(spinnerAdapter);

        int requestedJobId = getIntent().getIntExtra("job_id", -1);
        if (requestedJobId != -1 && findJobIndexById(requestedJobId) == -1) {
            NotificationHelper.showWarning(this, "Peringatan",
                    "Lowongan tidak ditemukan atau tidak dimiliki oleh akun Anda");
            requestedJobId = -1;
        }

        int targetJobId = previousSelection != -1 ? previousSelection : requestedJobId;
        if (targetJobId != -1) {
            int index = findJobIndexById(targetJobId);
            if (index >= 0) {
                spJobs.setSelection(index);
                selectedJobId = targetJobId;
            }
        }

        if (selectedJobId == -1) {
            selectedJobId = employerJobs.get(0).id;
        }

        spJobs.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedJobId = employerJobs.get(position).id;
                loadApplications();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        loadApplications();
    }

    private int findJobIndexById(int jobId) {
        for (int i = 0; i < employerJobs.size(); i++) {
            if (employerJobs.get(i).id == jobId) {
                return i;
            }
        }
        return -1;
    }

    private void loadApplications() {
        if (selectedJobId == -1) {
            showEmptyState("Pilih lowongan untuk melihat daftar pelamar");
            return;
        }

        Job job = database.jobDao().getJobById(selectedJobId);
        if (job == null || job.employerId != sharedPrefManager.getUserId()) {
            showEmptyState("Anda tidak memiliki akses ke lowongan ini");
            return;
        }

        List<Application> applications = database.applicationDao().getApplicationsByJobOrdered(selectedJobId);
        adapter.updateList(applications);

        if (applications == null || applications.isEmpty()) {
            showEmptyState("Belum ada pelamar untuk lowongan ini");
        } else {
            hideEmptyState();
        }
    }

    private void showEmptyState(String message) {
        if (tvEmptyState != null) {
            tvEmptyState.setText(message);
        }
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
        if (rvApplications != null) {
            rvApplications.setVisibility(View.GONE);
        }
    }

    private void hideEmptyState() {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.GONE);
        }
        if (rvApplications != null) {
            rvApplications.setVisibility(View.VISIBLE);
        }
    }

    class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {
        private List<Application> applications = new ArrayList<>();

        public void updateList(List<Application> applications) {
            this.applications = applications != null ? applications : new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_application, parent, false);
            return new ApplicationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ApplicationViewHolder holder, int position) {
            Application application = applications.get(position);
            User jobSeeker = database.userDao().getUserById(application.jobSeekerId);
            holder.bind(application, jobSeeker);
        }

        @Override
        public int getItemCount() {
            return applications.size();
        }

        class ApplicationViewHolder extends RecyclerView.ViewHolder {
            private TextView tvName, tvEmail, tvDate, tvStatus;
            private Spinner spStatus;
            private Button btnUpdateStatus;

            public ApplicationViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                spStatus = itemView.findViewById(R.id.spStatus);
                btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);

                // Setup spinner adapter
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        itemView.getContext(), R.array.application_status,
                        android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spStatus.setAdapter(adapter);

                // Set click listener for jobseeker name
                tvName.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Application application = applications.get(position);
                        if (application != null) {
                            Log.d("JobSeekerProfile", "Launching profile for jobseeker ID: " + application.jobSeekerId);
                            try {
                                Intent intent = new Intent(itemView.getContext(), JobSeekerProfileActivity.class);
                                intent.putExtra(JobSeekerProfileActivity.EXTRA_JOBSEEKER_ID, application.jobSeekerId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.d("JobSeekerProfile", "Starting activity with intent: " + intent.toString());
                                itemView.getContext().startActivity(intent);
                            } catch (Exception e) {
                                Log.e("JobSeekerProfile", "Error starting activity", e);
                            }
                        }
                    }
                });
            }

            public void bind(Application application, User jobSeeker) {
                if (jobSeeker != null) {
                    tvName.setText(jobSeeker.name != null && !jobSeeker.name.isEmpty() ? jobSeeker.name : jobSeeker.username);
                    tvEmail.setText(jobSeeker.email != null ? jobSeeker.email : "");
                }
                String date = application.tanggalLamaran != null ? application.tanggalLamaran : "-";
                tvDate.setText("Tanggal: " + date);
                tvStatus.setText("Status: " + formatReadableStatus(application.status));

                // Set spinner to current status
                String[] statuses = getResources().getStringArray(R.array.application_status);
                for (int i = 0; i < statuses.length; i++) {
                    if (statuses[i].equalsIgnoreCase(application.status)) {
                        spStatus.setSelection(i);
                        break;
                    }
                }

                btnUpdateStatus.setOnClickListener(v -> {
                    String newStatus = spStatus.getSelectedItem().toString();
                    application.status = newStatus;
                    database.applicationDao().updateApplication(application);
                    sendStatusNotification(application, newStatus);
                    NotificationHelper.showSuccess(ApplicationManagementActivity.this, "Berhasil", "Status diperbarui");
                    loadApplications();
                });
            }
        }
    }

    private String formatReadableStatus(String status) {
        if (status == null || status.isEmpty()) {
            return "-";
        }
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase(Locale.getDefault());
    }

    private void sendStatusNotification(Application application, String status) {
        User jobSeeker = database.userDao().getUserById(application.jobSeekerId);
        Job job = database.jobDao().getJobById(application.jobId);
        if (jobSeeker == null || job == null) {
            return;
        }

        String readableStatus = formatReadableStatus(status);
        String message = "Status lamaran Anda untuk " + job.judulPekerjaan + " diperbarui menjadi " + readableStatus;
        Notification notification = new Notification(jobSeeker.id,
                "Pembaruan Lamaran", message, "info", getCurrentTimestamp());
        database.notificationDao().insertNotification(notification);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        return sdf.format(new Date());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

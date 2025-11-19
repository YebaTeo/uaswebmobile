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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.entity.User;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.util.List;

public class ApplicationManagementActivity extends AppCompatActivity {
    private RecyclerView rvApplications;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private int jobId;
    private ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_management);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Daftar Pelamar");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        jobId = getIntent().getIntExtra("job_id", -1);
        if (jobId == -1) {
            Toast.makeText(this, "Job tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Job job = database.jobDao().getJobById(jobId);
        if (job == null || job.employerId != sharedPrefManager.getUserId()) {
            Toast.makeText(this, "Akses ditolak", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvApplications = findViewById(R.id.rvApplications);
        adapter = new ApplicationAdapter();
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(adapter);

        loadApplications();
    }

    private void loadApplications() {
        List<Application> applications = database.applicationDao().getApplicationsByJobOrdered(jobId);
        adapter.updateList(applications);
    }

    class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {
        private List<Application> applications;

        public void updateList(List<Application> applications) {
            this.applications = applications;
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
            return applications != null ? applications.size() : 0;
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
            }

            public void bind(Application application, User jobSeeker) {
                if (jobSeeker != null) {
                    tvName.setText(jobSeeker.name != null && !jobSeeker.name.isEmpty() ? jobSeeker.name : jobSeeker.username);
                    tvEmail.setText(jobSeeker.email != null ? jobSeeker.email : "");
                }
                tvDate.setText("Tanggal: " + application.tanggalLamaran);
                tvStatus.setText("Status: " + application.status);

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
                    Toast.makeText(ApplicationManagementActivity.this, "Status diperbarui", Toast.LENGTH_SHORT).show();
                    loadApplications();
                });
            }
        }
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

package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Application;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {
    private RecyclerView rvApplications;
    private View layoutEmptyState;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    private ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lamaran Saya");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"job_seeker".equals(sharedPrefManager.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvApplications = findViewById(R.id.rvApplications);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        adapter = new ApplicationAdapter();
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(adapter);

        loadApplications();
    }

    private void loadApplications() {
        int userId = sharedPrefManager.getUserId();
        List<Application> applications = database.applicationDao().getApplicationsByUser(userId);
        adapter.updateList(applications);

        boolean isEmpty = applications == null || applications.isEmpty();
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        rvApplications.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
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
                    .inflate(R.layout.item_my_application, parent, false);
            return new ApplicationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ApplicationViewHolder holder, int position) {
            Application application = applications.get(position);
            Job job = database.jobDao().getJobById(application.jobId);
            holder.bind(application, job);
        }

        @Override
        public int getItemCount() {
            return applications.size();
        }

        class ApplicationViewHolder extends RecyclerView.ViewHolder {
            private TextView tvJobTitle, tvCompany, tvDate, tvStatus;

            public ApplicationViewHolder(View itemView) {
                super(itemView);
                tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
                tvCompany = itemView.findViewById(R.id.tvCompany);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }

            public void bind(Application application, Job job) {
                if (job != null) {
                    tvJobTitle.setText(job.judulPekerjaan);
                    tvCompany.setText(job.namaPerusahaan);
                } else {
                    tvJobTitle.setText("Lowongan tidak ditemukan");
                    tvCompany.setText("");
                }
                tvDate.setText("Tanggal: " + application.tanggalLamaran);
                
                String status = application.status != null ? application.status : "submitted";
                String statusText = status.substring(0, 1).toUpperCase() + status.substring(1);
                tvStatus.setText("Status: " + statusText);
                
                itemView.setOnClickListener(v -> {
                    if (job != null) {
                        Intent intent = new Intent(MyApplicationsActivity.this, JobDetailActivity.class);
                        intent.putExtra("job_id", job.id);
                        startActivity(intent);
                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }
}


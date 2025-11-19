package com.example.uaswebmobile.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.entity.Job;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<Job> jobList;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public JobAdapter(List<Job> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobList != null ? jobList.size() : 0;
    }

    public void updateList(List<Job> newList) {
        this.jobList = newList;
        notifyDataSetChanged();
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle, tvCompany, tvLocation, tvType, tvSalary, tvDate;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvType = itemView.findViewById(R.id.tvType);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(jobList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Job job) {
            tvJobTitle.setText(job.judulPekerjaan);
            tvCompany.setText(job.namaPerusahaan);
            tvLocation.setText(job.lokasi);
            tvType.setText(job.tipePekerjaan);

            // Set background and text color based on job type
            if ("full-time".equalsIgnoreCase(job.tipePekerjaan)) {
                tvType.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_job_type_full_time));
            } else if ("part-time".equalsIgnoreCase(job.tipePekerjaan)) {
                tvType.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_job_type_part_time));
            } else {
                tvType.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_job_type_default));
            }
            tvType.setTextColor(Color.WHITE);
            tvType.setPadding(16, 8, 16, 8);

            String salary = formatCurrency(job.gajiMin) + " - " + formatCurrency(job.gajiMax);
            tvSalary.setText(salary);
            tvDate.setText(job.tanggalPosting);
        }
    }
}

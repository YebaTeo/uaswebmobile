package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.adapter.JobAdapter;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.Job;
import com.example.uaswebmobile.util.SharedPrefManager;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
    private RecyclerView rvBookmarks;
    private JobAdapter adapter;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bookmark Saya");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);

        rvBookmarks = findViewById(R.id.rvBookmarks);

        adapter = new JobAdapter(null, job -> {
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra("job_id", job.id);
            startActivity(intent);
        });

        rvBookmarks.setLayoutManager(new LinearLayoutManager(this));
        rvBookmarks.setAdapter(adapter);

        loadBookmarks();
    }

    private void loadBookmarks() {
        int userId = sharedPrefManager.getUserId();
        List<Job> bookmarkedJobs = database.bookmarkDao().getBookmarkedJobs(userId);
        adapter.updateList(bookmarkedJobs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookmarks();
    }
}

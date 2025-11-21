package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.util.SharedPrefManager;

public class AnalyticsActivity extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Analitik");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPrefManager = new SharedPrefManager(this);

        if (!sharedPrefManager.isLoggedIn() || !"employer".equals(sharedPrefManager.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employer, menu);
        return true;
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


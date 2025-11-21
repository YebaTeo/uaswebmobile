package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.uaswebmobile.util.NotificationHelper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.User;
import com.example.uaswebmobile.util.DatabaseInitializer;
import com.example.uaswebmobile.util.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase database;
    private SharedPrefManager sharedPrefManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        database = AppDatabase.getDatabase(this);
        sharedPrefManager = new SharedPrefManager(this);
        
        // Initialize database with sample data (only runs once)
        DatabaseInitializer.initializeDatabase(this);
        
        // Check if already logged in
        if (sharedPrefManager.isLoggedIn()) {
            redirectToMain();
            return;
        }
        
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                NotificationHelper.showWarning(this, "Peringatan", "Username dan password harus diisi");
                return;
            }
            
            User user = database.userDao().login(username, password);
            if (user != null) {
                sharedPrefManager.saveUser(user.id, user.username, user.role);
                NotificationHelper.showSuccess(this, "Berhasil", "Login berhasil! Selamat datang kembali.");
                redirectToMain();
            } else {
                NotificationHelper.showError(this, "Gagal Login", "Username atau password salah. Silakan coba lagi.");
            }
        });
        
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    
    private void redirectToMain() {
        String role = sharedPrefManager.getRole();
        Intent intent;
        if ("employer".equals(role)) {
            intent = new Intent(this, EmployerDashboardActivity.class);
        } else {
            intent = new Intent(this, JobSeekerDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}


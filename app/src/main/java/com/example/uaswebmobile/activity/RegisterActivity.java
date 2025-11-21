package com.example.uaswebmobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.util.NotificationHelper;
import com.example.uaswebmobile.database.AppDatabase;
import com.example.uaswebmobile.entity.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgRole;
    private RadioButton rbJobSeeker, rbEmployer;
    private Button btnRegister;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getDatabase(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgRole = findViewById(R.id.rgRole);
        rbJobSeeker = findViewById(R.id.rbJobSeeker);
        rbEmployer = findViewById(R.id.rbEmployer);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            int selectedRoleId = rgRole.getCheckedRadioButtonId();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                NotificationHelper.showWarning(this, "Peringatan", "Semua field harus diisi");
                return;
            }

            if (!password.equals(confirmPassword)) {
                NotificationHelper.showError(this, "Error", "Password tidak cocok. Silakan coba lagi.");
                return;
            }

            if (selectedRoleId == -1) {
                NotificationHelper.showWarning(this, "Peringatan", "Pilih role terlebih dahulu");
                return;
            }

            // Check if username already exists
            if (database.userDao().getUserByUsername(username) != null) {
                NotificationHelper.showError(this, "Error", "Username sudah digunakan. Silakan pilih username lain.");
                return;
            }

            // Check if email already exists
            if (database.userDao().getUserByEmail(email) != null) {
                NotificationHelper.showError(this, "Error", "Email sudah digunakan. Silakan gunakan email lain.");
                return;
            }

            String role = selectedRoleId == rbJobSeeker.getId() ? "job_seeker" : "employer";

            User user = new User(username, email, password, role);
            long userId = database.userDao().insertUser(user);

            if (userId > 0) {
                NotificationHelper.showSuccess(this, "Berhasil", "Registrasi berhasil! Silakan login untuk melanjutkan.");
                finish();
            } else {
                NotificationHelper.showError(this, "Gagal", "Registrasi gagal. Silakan coba lagi.");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

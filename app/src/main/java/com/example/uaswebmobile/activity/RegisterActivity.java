package com.example.uaswebmobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uaswebmobile.R;
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

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedRoleId == -1) {
                Toast.makeText(this, "Pilih role terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if username already exists
            if (database.userDao().getUserByUsername(username) != null) {
                Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            if (database.userDao().getUserByEmail(email) != null) {
                Toast.makeText(this, "Email sudah digunakan", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = selectedRoleId == rbJobSeeker.getId() ? "job_seeker" : "employer";

            User user = new User(username, email, password, role);
            long userId = database.userDao().insertUser(user);

            if (userId > 0) {
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

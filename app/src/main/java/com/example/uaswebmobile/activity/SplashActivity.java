package com.example.uaswebmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uaswebmobile.R;
import com.example.uaswebmobile.util.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvTagline = findViewById(R.id.tvTagline);
        
        // Fade in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        ivLogo.startAnimation(fadeIn);
        tvAppName.startAnimation(slideUp);
        tvTagline.startAnimation(slideUp);
        
        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
            
            Intent intent;
            if (sharedPrefManager.isLoggedIn()) {
                String role = sharedPrefManager.getRole();
                if ("employer".equals(role)) {
                    intent = new Intent(this, EmployerDashboardActivity.class);
                } else {
                    intent = new Intent(this, JobSeekerDashboardActivity.class);
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}


package com.example.uaswebmobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uaswebmobile.activity.LoginActivity;
import com.example.uaswebmobile.activity.SplashActivity;
import com.example.uaswebmobile.util.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Redirect to SplashActivity
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
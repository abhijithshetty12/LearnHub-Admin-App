package com.example.learnhubadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class AdminLogin extends AppCompatActivity {
    TextInputEditText adminName , adminPassword;
    Button adminloginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        adminName = findViewById(R.id.admin_name);
        adminPassword = findViewById(R.id.admin_password);
        adminloginbtn = findViewById(R.id.admin_login);
        adminloginbtn.setOnClickListener(v -> checkAdminLogin());
    }

    private void checkAdminLogin() {
        String name = adminName.getText().toString().trim();
        String password = adminPassword.getText().toString().trim();
        if (name.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill  all the field", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.equals("Admin") && password.equals("Admin@123")){
            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminLogin.this, AdminPanel.class));
        }else {
            Toast.makeText(this,"Invalid Username and Password", Toast.LENGTH_SHORT).show();
        }
    }
}
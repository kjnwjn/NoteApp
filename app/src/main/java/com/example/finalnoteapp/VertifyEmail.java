package com.example.finalnoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.finalnoteapp.databinding.ActivityRegisterBinding;
import com.example.finalnoteapp.databinding.ActivityVertifyEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VertifyEmail extends AppCompatActivity {

    private ActivityVertifyEmailBinding binding;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVertifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void initView() {
        binding.btnVerify.setOnClickListener(view -> verify());
        binding.skip.setOnClickListener(view -> gotoMain());
        progressDialog = new ProgressDialog(this);
    }

    private void verify() {
        progressDialog.show();
        auth = FirebaseAuth.getInstance();
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener((OnCompleteListener<Void>) unused -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Please check your email and verify now!", Toast.LENGTH_SHORT).show();
            gotoLogin();
        });

    }
    private void gotoLogin() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finishAffinity();
    }
    private void gotoMain() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finishAffinity();
    }
}
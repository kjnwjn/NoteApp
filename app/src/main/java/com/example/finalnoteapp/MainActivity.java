package com.example.finalnoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
        });
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        if(!auth.getCurrentUser().isEmailVerified() ){
            binding.verify.setVisibility(View.VISIBLE);
        }
       binding.verify.setOnClickListener(view -> {
               progressDialog.show();
               auth = FirebaseAuth.getInstance();
               auth.getCurrentUser().sendEmailVerification().addOnCompleteListener((OnCompleteListener<Void>) unused -> {
                   progressDialog.dismiss();
                   Toast.makeText(this, "Verify Email successfully!", Toast.LENGTH_SHORT).show();
                   binding.verify.setVisibility(View.GONE);
               });
       });

    }


}
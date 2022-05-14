package com.example.finalnoteapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalnoteapp.databinding.ActivityConfirmotpBinding;
import com.example.finalnoteapp.databinding.ActivityMainBinding;

public class ConfirmOtpActivity extends AppCompatActivity {

    private ActivityConfirmotpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmotpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}

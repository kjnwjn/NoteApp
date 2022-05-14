package com.example.finalnoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.finalnoteapp.databinding.ActivityFlashBinding;
import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FlashActivity extends AppCompatActivity {

    private ActivityFlashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.activity_flash);
        Handler handler = new Handler();
        handler.postDelayed(() -> nextActivity(),2000);
    }
    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);

        }else{
            Intent i = new Intent(this,MainActivity.class);
            i.putExtra("email",user.getEmail());
            startActivity(i);
        }
        finish();
    }
}
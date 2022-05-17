package com.example.finalnoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.finalnoteapp.databinding.ActivityForgotPasswordBinding;
import com.example.finalnoteapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ProgressDialog pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.btnConfirm.setOnClickListener(view -> forgotPass());
        pro = new ProgressDialog(this);
    }


    private void forgotPass() {
        if(isValidEmail(binding.email.getText().toString().trim())){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = binding.email.getText().toString();
            pro.show();
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pro.dismiss();
                            if (task.isSuccessful()) {
                                Log.e("TAG", "Email sent.");
                                Intent i = new Intent(ForgotPassword.this,LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }


                    });
        }

    }

    public boolean isValidEmail(CharSequence target) {
         if(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()){
             return true;
         }else{
             binding.email.setError("Invalid Email");
             return false;
         }
    }

}
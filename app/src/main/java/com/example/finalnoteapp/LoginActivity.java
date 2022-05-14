package com.example.finalnoteapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalnoteapp.databinding.ActivityLoginBinding;
import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.btnLogin.setOnClickListener(view -> {
            signin();
        });
        binding.btnRegister.setOnClickListener(view -> {
            Intent i = new Intent(this,RegisterActivity.class);
            startActivity(i);
        });
        binding.forgotPassword.setOnClickListener(view -> {
            Intent i = new Intent(this,ForgotPassword.class);
            startActivity(i);
        });
        progressDialog = new ProgressDialog(this);
    }

    private void signin() {

        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        if(validation(email,password)){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(i);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }


                    });
        }

    }

    private boolean validation(String email, String password) {
        if(isValidEmail(email) == false | email == null){
            binding.email.setError("Invalid email");
            return false;
        }else if(password.length() < 6 | password.length() == 0){
            binding.password.setError("Password must be at least 6 character");
            return false;
        }else{
            return true;
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()){
            return true;
        }else{
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}

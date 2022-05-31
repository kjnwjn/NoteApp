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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finalnoteapp.adapter.NoteAdapter;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.example.finalnoteapp.databinding.ActivityRegisterBinding;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;
    private String confirmPassword;


    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();

    }

    private void initView() {

        binding.btnRegister.setOnClickListener(view -> signUp());
        progressDialog = new ProgressDialog(this);
        binding.loginPage.setOnClickListener(view -> redirectToLoginPage());

    }

    private void redirectToLoginPage() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finishAffinity();
    }

    private void signUp() {

        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        if(validation(email,password)){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = user.getUid();

                                initUser(userId);
                                Toast.makeText(RegisterActivity.this, "Register successfully",
                                        Toast.LENGTH_SHORT).show();
                                gotoVerifyEmail();
                            } else {
                                Log.e("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }

    }

    private void initUser(String userId) {
//        DatabaseReference userListRef = mDatabase.getRef().child("User");
//        userListRef.setValue(userId, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                Log.e("tag","create user successfully");
//            }
//        });

//        DatabaseReference userActive = mDatabase.getRef().child("User").child(userId).child("active");
//        userActive.setValue(false, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                Log.e("tag","set active user successfully");
//            }
//        });

    }

    private void gotoVerifyEmail() {
        Intent i = new Intent(this,VertifyEmail.class);
        startActivity(i);
        finishAffinity();
    }
    private boolean validation(String email, String password) {
        confirmPassword = binding.ConfirmPassword.getText().toString().trim();
        if(isValidEmail(email) == false | email == null){
            binding.email.setError("Invalid email");
            return false;
        }else if(password.length() < 6){
            binding.password.setError("Password must be at least 6 character");
            return false;
        }else if(!password.equals(confirmPassword)){
            binding.ConfirmPassword.setError("Password and Password confirmation does not match!");
            return false;
        }else{
            return true;
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()){
            return true;
        }else{
            return false;
        }
    }



}

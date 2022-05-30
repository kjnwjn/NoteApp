package com.example.finalnoteapp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalnoteapp.R;
import com.example.finalnoteapp.databinding.FragmentChangePasswordBinding;
import com.example.finalnoteapp.databinding.FragmentNewReminderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordFragment extends Fragment {
    private FirebaseUser user;
    private FragmentChangePasswordBinding binding;
    private String oldPass;
    private String newPass;
    private String confirmPass;
    private View view;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        initView();
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation() == true){
                    updatepasswod();
                }
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();


        return view;
    }
    private void initView() {
        progressDialog = new ProgressDialog(getContext());
    }
    private boolean validation() {
        confirmPass = binding.ConfirmNewPass.getText().toString().trim();
        oldPass = binding.oldPass.getText().toString().trim();
        newPass = binding.newPass.getText().toString().trim();
        if(confirmPass.equals("") | oldPass.equals("") | newPass.equals("")){
            Toast.makeText(getContext(), "Information not allow to be empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(confirmPass.length() < 6 | oldPass.length() < 6 | newPass.length() < 6){
            Toast.makeText(getContext(), "Password must be at least 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!newPass.equals(confirmPass)){
            Toast.makeText(getContext(), "Confirm password is not correct", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    private void updatepasswod() {



        String email = user.getEmail();
        progressDialog.show();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){

                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(!task.isSuccessful()){
                                Toast.makeText(getContext(), "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(getContext(), "Password Successfully Modified", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "Password is not correct. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}

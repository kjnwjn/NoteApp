package com.example.finalnoteapp.fragment;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        initView(view);
        updatepasswod();

        return view;
    }
    private void initView(View view) {
        oldPass = binding.oldPass.getText().toString().trim();
        newPass = binding.newPass.getText().toString().trim();
        confirmPass = binding.ConfirmNewPass.getText().toString().trim();
        binding.btnSubmit.setOnClickListener(view1 -> updatepasswod());
    }
    private boolean validation(EditText password, EditText newPassword,String confirmPass) {
        return true;
    }

    public boolean isValidEmail(CharSequence target) {
        if(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()){
            return true;
        }else{
            return false;
        }
    }

    private void updatepasswod() {

//        user = FirebaseAuth.getInstance().getCurrentUser();
//        String email = user.getEmail();
//        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);
//
//        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(!task.isSuccessful()){
//                                Toast.makeText(getContext(), "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
//
//                            }else {
//                                Toast.makeText(getContext(), "Password Successfully Modified", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    });
//                }else {
//                    Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }




}

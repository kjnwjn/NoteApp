package com.example.finalnoteapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalnoteapp.AutoDeleteActivity;
import com.example.finalnoteapp.NoteActivity;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.databinding.FragmentChangePasswordBinding;
import com.example.finalnoteapp.databinding.FragmentReminderBinding;
import com.example.finalnoteapp.databinding.FragmentSettingBinding;


public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initView();

        return view;
    }

    private void initView() {
        binding.delTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AutoDeleteActivity.class);
                startActivity(intent);
            }
        });
    }



}
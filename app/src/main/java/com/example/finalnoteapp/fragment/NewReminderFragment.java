package com.example.finalnoteapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalnoteapp.R;
import com.example.finalnoteapp.databinding.FragmentNewReminderBinding;
import com.example.finalnoteapp.databinding.FragmentReminderBinding;


public class NewReminderFragment extends Fragment {

    private FragmentNewReminderBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewReminderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
}
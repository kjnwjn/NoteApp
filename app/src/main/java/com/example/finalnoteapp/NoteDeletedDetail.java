package com.example.finalnoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityEditNoteBinding;
import com.example.finalnoteapp.databinding.ActivityNoteDeletedDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NoteDeletedDetail extends AppCompatActivity {
    private ActivityNoteDeletedDetailBinding binding;

    private Note note;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDeletedDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        setSupportActionBar(binding.toolbarNoteDeletedActivity);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        note = intent.getParcelableExtra("note");
        binding.noteTitle.getEditText().setText(note.getTitle());
        binding.noteDeletedTextContent.getEditText().setText(note.getText());
    }

    private void initViews() {
    }
}
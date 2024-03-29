package com.example.finalnoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityEditNoteBinding;
import com.example.finalnoteapp.databinding.ActivityNoteDeletedDetailBinding;
import com.example.finalnoteapp.fragment.TrashbinFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

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
        setSupportActionBar(binding.toolbarNoteDeletedActivityActivity);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        note = intent.getParcelableExtra("note");
        binding.noteDeletedtitle.setText(note.getTitle());
        binding.noteDeletedtextContent.setText(note.getText());
        binding.noteDateInTrash.setText("Ngày thêm vào trash: "+note.getDateInTrash());
    }

    private void initViews() {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deleted_note_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e("TAG","onOptionsItemSelectedCalled");
        switch (item.getItemId()){
            case R.id.deleteForever:
                deleteforever();
                break;
            case R.id.restoreNoteInToolbar:
                restoreNote();
                break;
            case R.id.backToTrashbin:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void restoreNote() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        mDatabase.child("User").child(userId).child("NoteList").child(note.getNoteID()).child("inTrash").setValue(false);
        mDatabase.child("User").child(userId).child("NoteList").child(note.getNoteID()).child("dateInTrash").removeValue();
        finish();
    }

    private void deleteforever() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        mDatabase.child("User").child(userId).child("NoteList").child(note.getNoteID()).removeValue();
        finish();
    }
}
package com.example.finalnoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalnoteapp.data.Tag;
import com.example.finalnoteapp.databinding.ActivityEditNoteBinding;
import com.example.finalnoteapp.databinding.ActivityEditTagBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTagActivity extends AppCompatActivity {

    private ActivityEditTagBinding binding;
    private Tag tag;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        setSupportActionBar(binding.toolbarBarNoteTagActivity);
        Intent intent = getIntent();
        tag = intent.getParcelableExtra("tag");
        binding.tagTitle.getEditText().setText(tag.getTagName());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initViews() {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_tag_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e("TAG","onOptionsItemSelectedCalled");
        switch (item.getItemId()){
            case R.id.deleteTag:
                deleteTag();
                break;
            case R.id.saveTag:
                saveChange();
                break;
            case R.id.backToTagSpace:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void saveChange() {
        String tagName = binding.tagTitle.getEditText().getText().toString().trim();
        Log.e("tagName","tagName: "+ tagName);
        String userId = user.getUid();
        mDatabase.child("User").child(userId).child("TagList").child(tag.getTagID()).child("tagName").setValue(tagName);
        finish();
    }

    private void deleteTag() {
        String userId = user.getUid();
        mDatabase.child("User").child(userId).child("TagList").child(tag.getTagID()).removeValue();
        finish();
    }
}
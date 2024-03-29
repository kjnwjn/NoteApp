package com.example.finalnoteapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.finalnoteapp.databinding.ActivityNoteBinding;
import com.example.finalnoteapp.databinding.ActivityTagBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class TagActivity extends AppCompatActivity {


    private TextInputLayout inputTagName;
    private ActivityTagBinding binding;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();

        setSupportActionBar(binding.toolbarNoteActivity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void initViews() {
        inputTagName = binding.inputTagName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//            getMenuInflater().inflate(R.menu.menu_user_details,menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e("TAG","onOptionsItemSelectedCalled");
        switch (item.getItemId()){
            case R.id.saveItem:
                saveData();
                break;
            case R.id.remind:
                remind();
                break;
            case R.id.pin:
                pin();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        String tagName = inputTagName.getEditText().getText().toString().trim();

        if(tagName.isEmpty()){
            tagName = "Untitle tag";
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid(); //lấy UID của user hiện tại
        String tagID = mDatabase.push().getKey(); //tạo id
        DatabaseReference databaseReference = mDatabase.child("User").child(userId).child("TagList");
        String finalTagName = tagName;
        databaseReference.orderByChild("tagName").equalTo(tagName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Toast.makeText(TagActivity.this, "Tên nhãn này đã tồn tại", Toast.LENGTH_SHORT).show();
                }else {
                    databaseReference.child(tagID).child("tagName").setValue(finalTagName);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //databaseReference.child("tagName").setValue(tagName);

        //finish();
    }

    private void pin() {

    }

    private void remind() {

    }
}
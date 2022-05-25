package com.example.finalnoteapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.finalnoteapp.NoteActivity;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.TagActivity;
import com.example.finalnoteapp.adapter.NoteAdapter;
import com.example.finalnoteapp.adapter.TagAdapter;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.data.Tag;
import com.example.finalnoteapp.databinding.FragmentNewReminderBinding;
import com.example.finalnoteapp.databinding.FragmentReminderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NewReminderFragment extends Fragment {

    private FragmentNewReminderBinding binding;
    public static RecyclerView recyclerView;
    public static List<Tag> tags;
    public TagAdapter tagAdapter;
    public static GridLayoutManager mGridLayoutManager;
    private FloatingActionButton btnAdd;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference tagListRef = mDatabase.child("User").child(userId).child("TagList");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewReminderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.tagRecycler);

        initView(view);
        initNotes();

        return view;
    }

    private void initView(View view) {
        btnAdd = view.findViewById(binding.fltbtn.getId());
        btnAdd.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), TagActivity.class);
            startActivityForResult(intent,1);
//            onClickRequestPermission();
//            openGallery();
        });
    }

    private void initNotes(){
//        notes = new ArrayList<>();
        tagListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tags = new ArrayList<>();
                for (DataSnapshot childSnapshot:
                        snapshot.getChildren()) {
                    String text = String.valueOf(childSnapshot.child("tagName").getValue());
                    String tagID = String.valueOf(childSnapshot.getKey());
                    tags.add(new Tag(tagID ,text));

                }
//                Log.d("tag", String.valueOf(snapshot.getValue()));
                Collections.reverse(tags);
                tagAdapter = new TagAdapter(NewReminderFragment.this, tags);
                mGridLayoutManager = new GridLayoutManager(getContext(),1);
                recyclerView.setLayoutManager(mGridLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(tagAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
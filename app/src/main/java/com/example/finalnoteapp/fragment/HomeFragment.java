package com.example.finalnoteapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalnoteapp.MainActivity;
import com.example.finalnoteapp.NoteActivity;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.adapter.NoteAdapter;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityFlashBinding;
import com.example.finalnoteapp.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static  RecyclerView recyclerView;
    public static NoteAdapter noteAdapter;
    private FragmentHomeBinding binding;
    public static GridLayoutManager mGridLayoutManager;
    public static LinearLayoutManager mLinearLayoutManager;
    public static List<Note> notes;
    private ImageView app_image_upload;
    private FloatingActionButton btnAdd;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = view.findViewById(R.id.noteRecycler);

        initView(view);
        initNotes();
        noteAdapter = new NoteAdapter(this,notes);
        mGridLayoutManager = new GridLayoutManager(getContext(),2);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        setTypeDisplayRecyclerView(Note.TYPE_GRID);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(noteAdapter);
        return view;
    }

    private void initView(View view) {
        btnAdd = view.findViewById(binding.fltbtn.getId());
        btnAdd.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), NoteActivity.class);
            startActivityForResult(intent,1);
//            onClickRequestPermission();
//            openGallery();
        });
    }






    public static void setTypeDisplayRecyclerView(int typeDisplay){
        if(notes == null || notes.isEmpty()){
            return;
        }
        MainActivity.mCurrentTypeDisplay = typeDisplay;
        for (Note note : notes){
            note.setTypeDisplay(typeDisplay);
        }
    }
    private void initNotes(){
        notes = new ArrayList<>();
        notes.add(new Note("quan",null,true,null,null,null,"dsaudusa"));
        notes.add(new Note("a",null,true,null,null,null,"dưqedwq"));
        notes.add(new Note("b",null,true,null,null,null,"dsausadsaddusa"));
        notes.add(new Note("c",null,true,null,null,null,"qưqe"));
        notes.add(new Note("d",null,true,null,null,null,"qưewq"));
        notes.add(new Note("e",null,true,null,null,null,"sadd"));

    }
}

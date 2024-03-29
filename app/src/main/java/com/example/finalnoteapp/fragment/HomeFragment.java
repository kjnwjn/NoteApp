package com.example.finalnoteapp.fragment;

import static com.example.finalnoteapp.fragment.TrashbinFragment.noteDeletedAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.finalnoteapp.MainActivity;
import com.example.finalnoteapp.NoteActivity;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.adapter.NoteAdapter;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    public static  RecyclerView recyclerView;
    public static NoteAdapter noteAdapter;
    private FragmentHomeBinding binding;
    public static StaggeredGridLayoutManager mGridLayoutManager;
    public static LinearLayoutManager mLinearLayoutManager;
    public static List<Note> notes;
    private ImageView app_image_upload;
    private FloatingActionButton btnAdd;
    private ImageView deleteBtn;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference noteListRef = mDatabase.child("User").child(userId).child("NoteList");




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = view.findViewById(R.id.noteRecycler);

        initView(view);
        initNotes();

        return view;
    }

    private void initView(View view) {
        btnAdd = view.findViewById(binding.fltbtn.getId());
        btnAdd.setOnClickListener(view1 -> {

            noteListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long numChildren = snapshot.getChildrenCount();
                    Log.d("TAG", String.valueOf(numChildren));


                    if(!user.isEmailVerified() && numChildren > 4){
                        Toast.makeText(getContext(), "Yêu cầu kích hoạt tài khoản", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getContext(), NoteActivity.class);
                        startActivityForResult(intent,1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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

        noteListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes = new ArrayList<>();
                for (DataSnapshot childSnapshot:
                        snapshot.getChildren()) {
                    if (String.valueOf(childSnapshot.child("inTrash").getValue()).equals("false")){
                        String text = String.valueOf(childSnapshot.child("text").getValue());
                        String title = String.valueOf(childSnapshot.child("title").getValue());
                        String remindTime = String.valueOf(childSnapshot.child("remindTime").getValue());
                        String password = String.valueOf(childSnapshot.child("password").getValue());
                        boolean hasPass;
                        if (childSnapshot.child("hasPassword").getValue() != null){
                            hasPass = (boolean) childSnapshot.child("hasPassword").getValue();
                        }else{
                            hasPass = false;
                        }

                        if(remindTime == "null"){
                            remindTime = "";
                        }
                        if(password == "null"){
                            password = "";
                        }
                        String noteId = String.valueOf(childSnapshot.getKey());
                        boolean isPin;
                        if (childSnapshot.child("isPin").getValue() != null){
                            isPin = (boolean) childSnapshot.child("isPin").getValue();
                            if(!isPin){
                                notes.add(new Note(noteId, title,text,null,null,null,null, isPin, hasPass,password, remindTime,false,null));
                            }
                        }else {
                            notes.add(new Note(noteId, title,text,null,null,null,null, false, hasPass,password, remindTime,false,null));
                        }
                    }
                }
                for (DataSnapshot childSnapshot:
                        snapshot.getChildren()) {
                    if (String.valueOf(childSnapshot.child("inTrash").getValue()).equals("false")){
                        String text = String.valueOf(childSnapshot.child("text").getValue());
                        String title = String.valueOf(childSnapshot.child("title").getValue());
                        String remindTime = String.valueOf(childSnapshot.child("remindTime").getValue());
                        String password = String.valueOf(childSnapshot.child("password").getValue());
                        boolean hasPass;
                        if (childSnapshot.child("hasPassword").getValue() != null){
                            hasPass = (boolean) childSnapshot.child("hasPassword").getValue();
                        }else{
                            hasPass = false;
                        }
                        if(remindTime == "null"){
                            remindTime = "";
                        }
                        if(password == "null"){
                            password = "";
                        }
                        String noteId = String.valueOf(childSnapshot.getKey());
                        boolean isPin;
                        if (childSnapshot.child("isPin").getValue() != null){
                            isPin = (boolean) childSnapshot.child("isPin").getValue();
                            if(isPin){
                                notes.add(new Note(noteId, title,text,null,null,null,null, isPin, hasPass,password, remindTime,false,null));
                            }
                        }
                    }
                }


                Collections.reverse(notes);
                noteAdapter = new NoteAdapter(HomeFragment.this,notes);
                mGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mLinearLayoutManager = new LinearLayoutManager(getContext());
                setTypeDisplayRecyclerView(Note.TYPE_GRID);
                recyclerView.setLayoutManager(mGridLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(noteAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

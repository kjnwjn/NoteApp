package com.example.finalnoteapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.finalnoteapp.MainActivity;
import com.example.finalnoteapp.NoteActivity;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.adapter.NoteAdapter;
import com.example.finalnoteapp.adapter.NoteDeletedAdapter;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.FragmentSavingNoteBinding;
import com.example.finalnoteapp.databinding.FragmentSettingBinding;
import com.example.finalnoteapp.databinding.FragmentTrashbinBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrashbinFragment extends Fragment {

    public static RecyclerView recyclerView;
    private FragmentTrashbinBinding binding;
    public static NoteDeletedAdapter noteDeletedAdapter;
    public static GridLayoutManager mGridLayoutManager;
    public static LinearLayoutManager mLinearLayoutManager;
    public static List<Note> notes;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference noteListRef = mDatabase.child("User").child(userId).child("NoteList");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTrashbinBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = view.findViewById(R.id.noteRecycler);

        initNotes();

        return view;
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
//        notes = new ArrayList<>();
        noteListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes = new ArrayList<>();
                for (DataSnapshot childSnapshot:
                        snapshot.getChildren()) {
                    if (String.valueOf(childSnapshot.child("inTrash").getValue()).equals("true")){
                        String text = String.valueOf(childSnapshot.child("text").getValue());
                        String title = String.valueOf(childSnapshot.child("title").getValue());
                        String noteId = String.valueOf(childSnapshot.getKey());
                        String dateInTrash = String.valueOf(childSnapshot.child("dateInTrash").getValue());
                        notes.add(new Note(noteId, title,text,null,null,null,null,false, false,null,null,true,dateInTrash));
                    }

                }
//                Log.d("tag", String.valueOf(snapshot.getValue()));
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note note, Note t1) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        String strDate1 = note.getDateInTrash();
                        String strDate2 = t1.getDateInTrash();
                        try {
                            Date date1 = simpleDateFormat.parse(strDate1);
                            Date date2 = simpleDateFormat.parse(strDate2);
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                Collections.reverse(notes);
                noteDeletedAdapter = new NoteDeletedAdapter(TrashbinFragment.this,notes);
                mGridLayoutManager = new GridLayoutManager(getContext(),2);
                mLinearLayoutManager = new LinearLayoutManager(getContext());
                setTypeDisplayRecyclerView(Note.TYPE_GRID);
                recyclerView.setLayoutManager(mGridLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(noteDeletedAdapter);

                Date currentTime = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String date = dateFormat.format(currentTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                try {
                    Date current = simpleDateFormat.parse(date);
                    for (Note note :
                            notes) {
                        String dTrash = note.getDateInTrash();
                        try {
                            Date dateTrash = simpleDateFormat.parse(dTrash);
                            long diffInMillie = Math.abs(current.getTime() - dateTrash.getTime());
                            long diff = TimeUnit.MINUTES.convert(diffInMillie, TimeUnit.MILLISECONDS);
                            long limit = 30;
                            if (diff > limit){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = user.getUid();
                                mDatabase.child("User").child(userId).child("NoteList").child(note.getNoteID()).removeValue();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
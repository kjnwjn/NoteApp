package com.example.finalnoteapp.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalnoteapp.EditNote;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.example.finalnoteapp.fragment.TrashbinFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {


    private HomeFragment context;
    private List<Note> notes;
    private TrashbinFragment contextTrashbin;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference noteListRef = mDatabase.child("User").child(userId).child("NoteList");



    public NoteAdapter(HomeFragment context, List<Note> notes){
        this.context = context;
        this.notes = notes;
    }

    public NoteAdapter(TrashbinFragment contextTrashbin, List<Note> notes){
        this.contextTrashbin = contextTrashbin;
        this.notes = notes;
    }

    @Override
    public int getItemViewType(int position) {
        Note note = notes.get(position);
        return note.getTypeDisplay();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case Note.TYPE_GRID:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_grid,parent,false);
                break;
            case Note.TYPE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_list,parent,false);
                break;
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Note note =notes.get(position);
        if(note == null){
            return;
        }
        holder.content.setText(note.getText());
        holder.title.setText(note.getTitle());
        holder.noteContent.setOnClickListener(view -> editNote(note));
        holder.deleteBtn.setOnClickListener(view -> deleteNote(position));
        if(note.isPin()){
            holder.note_layout.setBackgroundColor(Color.YELLOW);
        }
    }

    private void deleteNote(Integer position) {
        Note note = notes.get(position);
        String noteID = note.getNoteID();
        DatabaseReference noteListRef = mDatabase.child("User").child(userId).child("NoteList").child(noteID);
        noteListRef.child("inTrash").setValue(true);
    }

    private void editNote(Note note) {
            Intent intent = new Intent(context.getContext(), EditNote.class);
            intent.putExtra("note",note);
            context.startActivityForResult(intent,12);
    }

    @Override
    public int getItemCount() {
        if(notes != null){
            return notes.size();
        }
        return 0;
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView title;
        LinearLayout noteContent;
        ImageView deleteBtn ;
        LinearLayout note_layout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.noteContent);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            note_layout = itemView.findViewById(R.id.note_layout);
        }
    }
}

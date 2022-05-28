package com.example.finalnoteapp.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {


    public HomeFragment context;
    public List<Note> notes;
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

        holder.title.setText(note.getTitle());
        holder.noteContent.setOnClickListener(view -> editNote(note));
        holder.deleteBtn.setOnClickListener(view -> deleteNote(position));
        if(note.isHasPassword()){
            holder.content.setText("***");
            holder.lockIcon.setVisibility(View.VISIBLE);
        }else{
            holder.content.setText(note.getText());
            holder.lockIcon.setVisibility(View.GONE);
        }
        if(note.isPin()){
            holder.note_layout.setBackgroundResource(R.drawable.border_pin);
        }
        if(!note.isPin()){
            holder.note_layout.setBackgroundResource(R.drawable.border);
        }
    }

    private void deleteNote(Integer position) {
        Note note = notes.get(position);
        String noteID = note.getNoteID();
        DatabaseReference noteListRef = mDatabase.child("User").child(userId).child("NoteList").child(noteID);
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String date = dateFormat.format(currentTime);
        noteListRef.child("inTrash").setValue(true);
        noteListRef.child("dateInTrash").setValue(date);

    }

    private void editNote(Note note) {
            if(note.isHasPassword()){
                final EditText inputText = new EditText(context.getActivity());
                inputText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                new AlertDialog.Builder(context.getActivity())
                        .setTitle("Yêu cầu Password").setView(inputText)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = inputText.getText().toString();
                                if(input.equals(note.getPassword())){
                                    Intent intent = new Intent(context.getContext(), EditNote.class);
                                    intent.putExtra("note",note);
                                    context.startActivityForResult(intent,12);
                                }else{
                                    Toast.makeText(context.getActivity(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                return;
            }else {
                Intent intent = new Intent(context.getContext(), EditNote.class);
                intent.putExtra("note",note);
                context.startActivityForResult(intent,12);
            }

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
        ImageView lockIcon;
        LinearLayout note_layout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.noteContent);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            note_layout = itemView.findViewById(R.id.note_layout);
            lockIcon = itemView.findViewById(R.id.lockIcon);
        }
    }
}

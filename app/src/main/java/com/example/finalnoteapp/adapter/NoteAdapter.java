package com.example.finalnoteapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalnoteapp.R;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.fragment.HomeFragment;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {


    private HomeFragment context;
    private List<Note> notes;
    public NoteAdapter(HomeFragment context, List<Note> notes){
        this.context = context;
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
        holder.imageNote.setImageResource(R.drawable.flashlayout);
        holder.title.setText(note.getTitle());
    }

    @Override
    public int getItemCount() {
        if(notes != null){
            return notes.size();
        }
        return 0;
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        ImageView imageNote;
        TextView title;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageNote = itemView.findViewById(R.id.imageNote);
            title = itemView.findViewById(R.id.title);
        }
    }
}

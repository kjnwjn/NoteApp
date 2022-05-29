package com.example.finalnoteapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalnoteapp.EditTagActivity;
import com.example.finalnoteapp.R;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.data.Tag;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.example.finalnoteapp.fragment.NewReminderFragment;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagHolder>{

    private NewReminderFragment context;
    private List<Tag> tags;
    public static final int TAG_CODE = 21;

    public TagAdapter(NewReminderFragment context, List<Tag> tags){
        this.context = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
        Tag tag = tags.get(position);
        if(tag == null){
            return;
        }
        holder.tagName.setText(tag.getTagName());
        holder.tagName.setOnClickListener(view -> gotoEditTag(view,tag));
    }

    private void gotoEditTag(View view,Tag tag) {
        Intent i = new Intent(context.getContext(), EditTagActivity.class);
        i.putExtra("tag",tag);
        context.startActivityForResult(i,TAG_CODE);
    }

    @Override
    public int getItemCount() {
        if(tags != null){
            return tags.size();
        }
        return 0;
    }


    public static class TagHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        public TagHolder(@NonNull View itemView) {
            super(itemView);

            tagName = itemView.findViewById(R.id.tag_name);
        }
    }
}

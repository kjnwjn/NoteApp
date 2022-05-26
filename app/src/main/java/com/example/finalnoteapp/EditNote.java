package com.example.finalnoteapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityEditNoteBinding;
import com.example.finalnoteapp.databinding.ActivityNoteBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class EditNote extends AppCompatActivity {
    public static final int MY_REQUEST_CODE_FOR_EDIT_NOTE = 11;
    private ActivityEditNoteBinding binding;
    private DatabaseReference mDatabase;
    private ImageView app_image_view;
    private TextInputLayout note_title;
    private TextInputLayout note_text_content;
    private TextView time_remind;
    private Note note;

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null){
                            return;
                        }
                        Uri uri = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                            app_image_view.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }


            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        setSupportActionBar(binding.toolbarNoteActivity);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        note = intent.getParcelableExtra("note");
        binding.noteTitle.getEditText().setText(note.getTitle());
        binding.noteTextContent.getEditText().setText(note.getText());
        binding.timeRemind.setText(note.getRemindTime());
        setDeleteRemindVisibility();

    }

    private void initViews() {
        note_title = binding.noteTitle;
        note_text_content = binding.noteTextContent;
        time_remind = binding.timeRemind;
        app_image_view = binding.appImageView;
        ImageView appImageUpload  =findViewById(R.id.app_image_upload);
        appImageUpload.setOnClickListener(view -> onClickRequestPermission());
        binding.btnDeleteRemind.setOnClickListener(view -> {
            if(!time_remind.getText().toString().trim().equals("")){
                time_remind.setText("");
            }
            setDeleteRemindVisibility();
        });
        setDeleteRemindVisibility();
    }

    public void setDeleteRemindVisibility(){
        if(time_remind.getText().toString().trim().equals("")){
            binding.btnDeleteRemind.setVisibility(View.INVISIBLE);
            binding.txtNgayNhac.setVisibility(View.INVISIBLE);
        }else{
            binding.btnDeleteRemind.setVisibility(View.VISIBLE);
            binding.txtNgayNhac.setVisibility(View.VISIBLE);
        }
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
        String noteTitle = note_title.getEditText().getText().toString();
        String noteTextContent = note_text_content.getEditText().getText().toString();
        String remindTime = time_remind.getText().toString();

        if(noteTitle.isEmpty()){
            noteTitle = "Untitle";
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid(); //lấy UID của user hiện tại
        String noteID = note.getNoteID(); //tạo id cho note
        DatabaseReference databaseReference = mDatabase.child("User").child(userId).child("NoteList").child(noteID); //dẫn databaseRef tới note
        databaseReference.child("title").setValue(noteTitle);//set note's title
        databaseReference.child("text").setValue(noteTextContent);//set note's text
        databaseReference.child("remindTime").setValue(remindTime);
        databaseReference.child("inTrash").setValue(false);

        finish();
    }

    private void pin() {
    }

    private void remind() {
        final Calendar currentDate = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        StringBuilder s = new StringBuilder();
        time_remind.setText("Ngày nhắc");
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                s.append(dayOfMonth +"/"  + (monthOfYear+1) + "/" + year + ' ');
                TimePickerDialog dialog1 = new TimePickerDialog(EditNote.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        s.append(hourOfDay +":"  + minute );
                        binding.timeRemind.setText(s);
                        Log.v("TAG", "The choosen one " + date.getTime());
                    }


                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                dialog1.setButton(TimePickerDialog.BUTTON_NEGATIVE, null, dialog1);
                dialog1.show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, null, dialog);
        dialog.show();
        setDeleteRemindVisibility();
    }
    private void onClickRequestPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            String [] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission,MY_REQUEST_CODE_FOR_EDIT_NOTE);
        }
    }
    private void openGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(i.createChooser(i,"Select picture"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("TAG","path: "+ data.getData());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE_FOR_EDIT_NOTE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }

    }


}
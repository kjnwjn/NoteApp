package com.example.finalnoteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.*;
import android.widget.Toolbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.finalnoteapp.databinding.ActivityNoteBinding;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;

    public class NoteActivity extends AppCompatActivity {


        private static final int MY_REQUEST_CODE = 10;
        private int seclectd = -1;
        private TextInputLayout note_title;
        private TextInputLayout note_text_content;
        private ActivityNoteBinding binding;
        private TextView time_remind;
        private ImageView app_image_upload;
        private ImageView app_image_view;
        private Toolbar toolbar;

        private DatabaseReference mDatabase;



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
            binding = ActivityNoteBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            initViews();
            setSupportActionBar(binding.toolbarNoteActivity);

            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        private void initViews() {
            note_title = binding.noteTitle;
            note_text_content = binding.noteTextContent;
            time_remind = binding.timeRemind;
            app_image_upload = binding.navBottomMenu.appImageUpload;
            app_image_view = binding.appImageView;
            app_image_upload.setOnClickListener(view -> onClickRequestPermission());



//            txtDate.getEditText().setOnClickListener(view ->{
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(System.currentTimeMillis());
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//                DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, y, m, d) -> {
//                    txtDate.getEditText().setText(d +"/"  + (m+1) + "/" + y);
//                },year,month,day);
//                dialog.show();
//            });
//            txtTime.getEditText().setOnClickListener(view ->{
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(System.currentTimeMillis());
//                int hour = cal.get(Calendar.HOUR);
//                int minutes = cal.get(Calendar.MINUTE);
//                TimePickerDialog dialog = new TimePickerDialog(this, (timePicker, h, m) -> {
//                    txtTime.getEditText().setText(h +":" + m);
//                }, hour, minutes, true);
//                dialog.show();
//            });
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

        private void pin() {
        }

        private void remind() {
            final Calendar currentDate = Calendar.getInstance();
            Calendar date = Calendar.getInstance();
            StringBuilder s = new StringBuilder();
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    date.set(year, monthOfYear, dayOfMonth);
                    s.append(dayOfMonth +"/"  + (monthOfYear+1) + "/" + year + ' ');
                    new TimePickerDialog(NoteActivity.this, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            date.set(Calendar.MINUTE, minute);
                            s.append(hourOfDay +":"  + minute );
                            binding.timeRemind.setText(s);
                            Log.v("TAG", "The choosen one " + date.getTime());
                        }
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
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
                requestPermissions(permission,MY_REQUEST_CODE);
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
            Log.e("TAG","path: "+ data.getData());
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == MY_REQUEST_CODE){
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }
            }

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
              String noteID = mDatabase.push().getKey(); //tạo id cho note
              DatabaseReference databaseReference = mDatabase.child("User").child(userId).child("NoteList").child(noteID); //dẫn databaseRef tới note
              databaseReference.child("title").setValue(noteTitle);//set note's title
              databaseReference.child("text").setValue(noteTextContent);//set note's text
              databaseReference.child("remindTime").setValue(remindTime);
              databaseReference.child("inTrash").setValue(false);

              finish();

//            String dateResult =  txtDate.getEditText().getText().toString() + txtTime.getEditText().getText().toString();
//
//
//            Events event = new Events(name,place,dateResult);
//
//            if(event == null){
//                Toast.makeText(this,"DO not have any event ",Toast.LENGTH_LONG);
//                return;
//            }else{
//                Intent intent = new Intent();
//                intent.putExtra("data",event);
//                setResult(1,intent);
//            }
//
//            finish();

        }
    }


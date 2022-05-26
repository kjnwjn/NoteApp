package com.example.finalnoteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

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
        public Uri imageUrl;
        private FirebaseStorage storage;
        private StorageReference storageReference;
        private Task<Uri> downloadImageUrl;
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
                            imageUrl = data.getData();
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUrl);
                                app_image_view.setImageBitmap(bitmap);
                                uploadImage();
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
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        private void initViews() {
            note_title = binding.noteTitle;
            note_text_content = binding.noteTextContent;
            time_remind = binding.timeRemind;
            app_image_upload = binding.navBottomMenu.appImageUpload;
            app_image_view = binding.appImageView;
            app_image_upload.setOnClickListener(view -> onClickRequestPermission());
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
            }else{
                binding.btnDeleteRemind.setVisibility(View.VISIBLE);
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
                    TimePickerDialog dialog1 = new TimePickerDialog(NoteActivity.this, new TimePickerDialog.OnTimeSetListener() {

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
                requestPermissions(permission,MY_REQUEST_CODE);
            }
        }
        private void openGallery() {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            mActivityResultLauncher.launch(i.createChooser(i,"Select picture"));
        }


    private void uploadImage() {
        final ProgressDialog pd  = new ProgressDialog(this);
        pd.setTitle("Uploading Image ...");
        pd.show();
        final String randomKey = UUID.randomUUID().toString();
        Log.e("url","key: "+ randomKey);
        StorageReference riversRef = storageReference.child("images/"+ randomKey);
        riversRef.putFile(imageUrl)
                // Register observers to listen for when the download is done or if it fails
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("TAG","msg: "+exception);
                        // Handle unsuccessful uploads
                        pd.dismiss();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                pd.dismiss();
                downloadImageUrl = taskSnapshot.getStorage().getDownloadUrl();
                Log.e("url","url: "+ downloadImageUrl);
                Snackbar.make(binding.editNoteRelativeLayout,"image uploaded !",Snackbar.LENGTH_LONG);
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPecent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress : " + (int) progressPecent + "%");
                    }
                });
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


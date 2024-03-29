package com.example.finalnoteapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityEditNoteBinding;
import com.example.finalnoteapp.databinding.ActivityNoteBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class EditNote extends AppCompatActivity {
    public static final int MY_REQUEST_CODE_FOR_EDIT_NOTE = 11;
    private ActivityEditNoteBinding binding;
    private DatabaseReference mDatabase;
    private ImageView app_image_view;
    private TextInputLayout note_title;
    private TextInputLayout note_text_content;
    private TextView pinState;
    private EditText editPass;
    private ToggleButton setPass;
    private TextView time_remind;
    private Note note;
    public Uri imageUrl;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String downloadImageUrl;
    boolean isPin;
    private FirebaseUser user;
    private String userId;
    private  String noteID ;
    DatabaseReference databaseReference;
    private Calendar date;
    private PendingIntent alarmIntent;
    private AlarmManager alarm;
    private StringBuilder s;

    private int notificationId = 1;
    private VideoView videoView;
    private ProgressBar progressBar;
    private Uri videoUri;
    MediaController mediaController;
    private UploadTask uploadTask;
    private Uri  downloadVideoUrl;

    public static final int MICRO_PHONE_CODE = 50;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileAudioPath;
    private Uri downloadAudioUrl;
    private ProgressDialog progressDialog;


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
                            binding.imageGr.setVisibility(View.VISIBLE);
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
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        setSupportActionBar(binding.toolbarNoteActivity);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Intent intent = getIntent();
        note = intent.getParcelableExtra("note");
        binding.noteTitle.getEditText().setText(note.getTitle());
        binding.noteTextContent.getEditText().setText(note.getText());
        binding.timeRemind.setText(note.getRemindTime());
        binding.setPass.setChecked(note.isHasPassword());
        binding.editPass.setText(note.getPassword());
        isPin = note.isPin();
        setDeleteRemindVisibility();
        setPinStateText();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid(); //lấy UID của user hiện tại
        noteID = note.getNoteID();
        databaseReference = mDatabase.child("User").child(userId).child("NoteList").child(noteID);

        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();
        databaseReference.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageLink = snapshot.getValue(String.class);
                if(imageLink == null){
                    return;
                }
                if(!imageLink.equals("")){
                    Glide.with(getApplicationContext()).load(imageLink).into(binding.appImageView);
                    binding.imageGr.setVisibility(View.VISIBLE);
                    binding.btnDeleteImage.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;

            }
        });
        databaseReference.child("video").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String videoLinkString = snapshot.getValue(String.class);

                if(videoLinkString == null){
                    return;
                }
                Uri video = Uri.parse(videoLinkString);
                if(!videoLinkString.equals("")){
                    mediaController = new MediaController(EditNote.this);
                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(video);
                    videoView.requestFocus();
                    videoView.start();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;

            }
        });
        databaseReference.child("audio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String audioLinkString = snapshot.getValue(String.class);

                if(audioLinkString == null){
                    return;
                }
                if(!audioLinkString.equals("")){
                    fileAudioPath = audioLinkString;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;

            }
        });



        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        if(isMicroPhoneExits()){
            getMicroPhonePermission();
        }
        progressDialog = new ProgressDialog(this);
    }

    private void setRemidTime(String title){
        if(date != null){
            Intent i = new Intent(this,AlarmReceiver.class);
            i.putExtra("notificationId",notificationId);
            i.putExtra("title",title);
            alarmIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
            alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            Long alarmTime = date.getTimeInMillis();

            alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            Toast.makeText(this, "Set alarm time done!", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteRemidTime(){
        if(alarm !=null){
            alarm.cancel(alarmIntent);
            Toast.makeText(this, "Canceled Remind ", Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {
        note_title = binding.noteTitle;
        note_text_content = binding.noteTextContent;
        pinState = binding.pinState;
        editPass = binding.editPass;
        setPass = binding.setPass;
        time_remind = binding.timeRemind;
        app_image_view = binding.appImageView;
        ImageView appImageUpload  = findViewById(R.id.app_image_upload);
        appImageUpload.setOnClickListener(view -> onClickRequestPermission());
        binding.btnDeleteRemind.setOnClickListener(view -> {
            if(!time_remind.getText().toString().trim().equals("")){
                time_remind.setText("");
            }
            setDeleteRemindVisibility();
        });
        setDeleteRemindVisibility();
        setPinStateText();
        binding.btnDeleteImage.setOnClickListener(view -> {
            binding.imageGr.setVisibility(View.GONE);
            binding.btnDeleteImage.setVisibility(View.GONE);
            databaseReference.child("image").setValue("");
        });

        videoView = binding.appVideoView;
        progressBar = binding.progressBarForVideo;
        ImageView appDraw = findViewById(R.id.app_draw);
        appDraw.setOnClickListener(view -> chooseVideo(view));
        binding.btnUploadVideo.setOnClickListener(view -> uploadvideo());
        binding.btnDeleteVideo.setOnClickListener(view -> deleteVideo(view));

        binding.btnPlay.setOnClickListener(view -> playAudio());
        binding.btnStop.setOnClickListener(view -> stopAudio());
        binding.navBottomMenu.appMic.setOnClickListener(view -> recordAudio());
        binding.btnUploadAudio.setOnClickListener(view -> uploadAudioToStorage());

    }

    private void recordAudio() {

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getFileRecordPath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording is started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopAudio() {
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording is stopped", Toast.LENGTH_SHORT).show();
        }else{
            return;
        }

    }

    private void playAudio() {
        try {
            if(fileAudioPath!=null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(fileAudioPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, "Recording is playing", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void uploadAudioToStorage(){
        progressDialog.setTitle("Uploading Audio ...");
        progressDialog.show();
        if(fileAudioPath!= null){

            Uri file = Uri.fromFile(new File(fileAudioPath));
            StorageReference audioRef = storageReference;
            StorageReference riversRef = audioRef.child("audio/"+file.getLastPathSegment());
            uploadTask = riversRef.putFile(file);
            Task<Uri> uniTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    progressDialog.dismiss();
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return riversRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                downloadAudioUrl = task.getResult();
                                Log.e("urlaudio", "msg: "+ downloadAudioUrl.toString());
                                Toast.makeText(EditNote.this, "Data save", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditNote.this, "Data save failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }else{
            progressDialog.dismiss();
            Toast.makeText(EditNote.this, "All field are require", Toast.LENGTH_SHORT).show();
        }


    }
    private String getFileRecordPath(){
        final String randomKey = UUID.randomUUID().toString().replace("-", "");
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File audioPath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(audioPath,"audio"+randomKey+".mp3");
        Log.e("fileaudio","url: "+file.getPath());
        fileAudioPath = file.getPath();
        return fileAudioPath;
    }

    private boolean isMicroPhoneExits(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }else{
            return false;
        }
    }
    private void getMicroPhonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO},MICRO_PHONE_CODE);
        }
    }
    private void deleteVideo(View view) {
        databaseReference.child("video").setValue("");
    }

    public void setPinStateText(){
        if(isPin){
            pinState.setText("Trạng thái ghim: Đã ghim");
        }else{
            pinState.setText("Trạng thái ghim: Không ghim");
        }
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
            case R.id.shareItem:
                shareItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareItem() {
        String noteTitle = note_title.getEditText().getText().toString().trim();
        String noteTextContent = note_text_content.getEditText().getText().toString().trim();


        if(noteTitle.isEmpty()){
            noteTitle = "Untitled";
        }
        if(noteTextContent.isEmpty()){
            noteTextContent = "";
        }
        Intent send = new Intent();
        send.setAction(Intent.ACTION_SEND);
        send.setType("*/*");
        send.putExtra(Intent.EXTRA_TEXT, noteTitle+": "+noteTextContent);
        send.putExtra(Intent.EXTRA_SUBJECT, noteTitle);
        Intent share = Intent.createChooser(send, "Share note");
        startActivity(share);
    }

    private void saveData() {
//        uploadImage();

        String noteTitle = note_title.getEditText().getText().toString().trim();
        String noteTextContent = note_text_content.getEditText().getText().toString().trim();
        String remindTime = time_remind.getText().toString();
        String pass = editPass.getText().toString().trim();

        if(noteTitle.isEmpty()){
            noteTitle = "Untitled";
        }
        if(remindTime.isEmpty() || remindTime == null){
            databaseReference.child("remindTime").setValue("");
        }else{
            databaseReference.child("remindTime").setValue(remindTime);
        }
        databaseReference.child("title").setValue(noteTitle);//set note's title
        databaseReference.child("text").setValue(noteTextContent);//set note's text
        databaseReference.child("remindTime").setValue(remindTime);
        databaseReference.child("inTrash").setValue(false);
        databaseReference.child("isPin").setValue(isPin);
        databaseReference.child("hasPassword").setValue(setPass.isChecked());
        databaseReference.child("password").setValue(pass);


        if(!remindTime.isEmpty()){
            setRemidTime(noteTitle);
        }else{
            deleteRemidTime();
        }
        if(downloadImageUrl != null){
            databaseReference.child("image").setValue(downloadImageUrl);
        }else{
//            databaseReference.child("image").setValue("");
        }
        if(downloadVideoUrl ==null){
//            databaseReference.child("video").setValue("");

        }else{
            databaseReference.child("video").setValue(downloadVideoUrl.toString());
        }
        if(downloadAudioUrl ==null){
//            databaseReference.child("video").setValue("");

        }else{
            databaseReference.child("audio").setValue(downloadAudioUrl.toString());
        }
        finish();
    }

    private void pin() {
        if(isPin){
            isPin = false;
        }else {
            isPin = true;
        }
        setPinStateText();
    }

    private void remind() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
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

    private void chooseVideo(View view){
        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,NoteActivity.PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == NoteActivity.PICK_VIDEO || resultCode == RESULT_OK || data != null || data.getData() != null){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private void uploadImage() {
        final ProgressDialog pd  = new ProgressDialog(this);
        pd.setTitle("Uploading Image ...");
        pd.show();
        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/"+ randomKey);
        Log.e("url","key: "+ randomKey);
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

                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uridownload) {
                        pd.dismiss();
                        downloadImageUrl = uridownload.toString();
                    }
                });
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
    private String getExtension(Uri uri){
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(uri));
    }
    private void uploadvideo(){
        if(videoUri != null){
            progressBar.setVisibility(View.VISIBLE);
            StorageReference videoref = storageReference.child("video");
            StorageReference videoRefVal = videoref.child(System.currentTimeMillis()+ "." + getExtension(videoUri));
            uploadTask = videoRefVal.putFile(videoUri);
            Task<Uri> uniTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return videoRefVal.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                downloadVideoUrl = task.getResult();
                                Log.e("urlvideo", "msg: "+ downloadVideoUrl.toString());
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditNote.this, "Data save", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditNote.this, "Data save failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }else{
            Toast.makeText(EditNote.this, "All field are require", Toast.LENGTH_SHORT).show();

        }
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
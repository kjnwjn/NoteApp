


package com.example.finalnoteapp;

import static com.example.finalnoteapp.fragment.HomeFragment.noteAdapter;
import static com.example.finalnoteapp.fragment.HomeFragment.notes;
import static com.example.finalnoteapp.fragment.HomeFragment.recyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalnoteapp.adapter.NoteAdapter;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.example.finalnoteapp.databinding.LayoutHeaderNavBinding;
import com.example.finalnoteapp.fragment.ChangePasswordFragment;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.example.finalnoteapp.fragment.NewReminderFragment;
import com.example.finalnoteapp.fragment.ReminderFragment;
import com.example.finalnoteapp.fragment.SavingNoteFragment;
import com.example.finalnoteapp.fragment.SettingFragment;
import com.example.finalnoteapp.fragment.TrashbinFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_REMINDER = 1;
    private static final int FRAGMENT_NEW_REMINDER = 2;
    private static final int FRAGMENT_TRANSBIN = 3;
    private static final int FRAGMENT_SETTING = 4;
    private static final int FRAGMENT_SAVING_NOTE = 5;
    private static final int FRAGMENT_CHANGE_PASSWORD = 6;


    private int mCurrentFragment = FRAGMENT_HOME;
    public static int mCurrentTypeDisplay = Note.TYPE_LIST;
    private DrawerLayout mDrawerLayout;
    private ActivityMainBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private NavigationView nav;
    private Menu mMenu;
    private DatabaseReference databaseReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDrawerLayout = binding.drawerLayout;
        auth = FirebaseAuth.getInstance();
        if (!auth.getCurrentUser().isEmailVerified()) {
            binding.appBarMain.contentMain.verify.setVisibility(View.VISIBLE);
        }else{
            binding.appBarMain.contentMain.verify.setVisibility(View.GONE);
        }
        binding.appBarMain.contentMain.verify.setOnClickListener(view -> {
            progressDialog.show();
            auth = FirebaseAuth.getInstance();
            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener((OnCompleteListener<Void>) unused -> {
                    Toast.makeText(this, "Check your email now!", Toast.LENGTH_SHORT).show();
                    binding.appBarMain.contentMain.verify.setVisibility(View.GONE);
                    gotoLogin();
                progressDialog.dismiss();
            });
        });
        showUserInfo();

        toolbar = findViewById(binding.appBarMain.toolbar.getId());
        setSupportActionBar(toolbar);
        replaceFragment(new HomeFragment(),"FRAGMENT_HOME");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                , R.string.app_nav_drawer_open, R.string.app_nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav = findViewById(R.id.navigation_view);
        nav.getMenu().findItem(R.id.app_note).setChecked(true);
        nav.setNavigationItemSelectedListener(this);

        List<Note> tempList = new ArrayList<>();
        EditText editText = toolbar.findViewById(R.id.search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int textLength = charSequence.length();
                tempList.clear();
                if (notes != null){
                    for(Note note: notes){
                        if (textLength <= note.getTitle().length()) {
                            if (note.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                if(!tempList.contains(note)){
                                    tempList.add(note);
                                };
                            }
                        }
                        if (textLength <= note.getText().length()) {
                            if (note.getText().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                if(!tempList.contains(note)){
                                    tempList.add(note);
                                }
                            }
                        }
                        if (textLength == 0){
                            noteAdapter = new NoteAdapter(noteAdapter.context, notes);
                            recyclerView.setAdapter(noteAdapter);
                            return;
                        }
                    }
                    noteAdapter = new NoteAdapter(noteAdapter.context, tempList);
                    recyclerView.setAdapter(noteAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    private void gotoLogin() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finishAffinity();
    }

    private void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        View viewHeader = binding.navigationView.getHeaderView(0);
        String email = user.getEmail();
        TextView emailHeader = LayoutHeaderNavBinding.bind(viewHeader).emailHeader;
        emailHeader.setText(email);
    }


    private void initView() {
        progressDialog = new ProgressDialog(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view:
                onChangeTypeDisplay();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onChangeTypeDisplay() {
        if(mCurrentTypeDisplay == Note.TYPE_GRID){
            HomeFragment.setTypeDisplayRecyclerView(Note.TYPE_LIST);
            mCurrentTypeDisplay =  Note.TYPE_LIST;
            HomeFragment.recyclerView.setLayoutManager(HomeFragment.mLinearLayoutManager);
        }else{
            HomeFragment.setTypeDisplayRecyclerView(Note.TYPE_GRID);
            mCurrentTypeDisplay =  Note.TYPE_GRID;
            HomeFragment.recyclerView.setLayoutManager(HomeFragment.mGridLayoutManager);
        }
        noteAdapter.notifyDataSetChanged();
        setIconToolbar();

    }

    private void setIconToolbar() {
        if(mCurrentTypeDisplay == Note.TYPE_GRID){
            mMenu.getItem(1).setIcon(R.drawable.ic_baseline_grid_view_24);
        }else{
            mMenu.getItem(1).setIcon(R.drawable.ic_view_list);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.app_note:
                if(mCurrentFragment != FRAGMENT_HOME){
                    replaceFragment(new HomeFragment(),"FRAGMENT_HOME");
                    mCurrentFragment = FRAGMENT_HOME;
                }
                break;
            case R.id.app_new_reminder:
                if(mCurrentFragment != FRAGMENT_NEW_REMINDER){
                    replaceFragment(new NewReminderFragment(),"FRAGMENT_NEW_REMINDER");
                    mCurrentFragment = FRAGMENT_NEW_REMINDER;
                }
                break;
            case R.id.app_trashbin:
                Toast.makeText(this,"app_trashbin" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_TRANSBIN){
                    replaceFragment(new TrashbinFragment(),"FRAGMENT_TRANSBIN");
                    mCurrentFragment = FRAGMENT_TRANSBIN;
                }
                break;
            case R.id.app_setting:
                if(mCurrentFragment != FRAGMENT_SETTING){
                    replaceFragment(new SettingFragment(),"FRAGMENT_SETTING");
                    mCurrentFragment = FRAGMENT_SETTING;
                }
                break;
            case R.id.changePassword:
                if(mCurrentFragment != FRAGMENT_CHANGE_PASSWORD){
                    replaceFragment(new ChangePasswordFragment(),"FRAGMENT_CHANGE_PASSWORD");
                    mCurrentFragment = FRAGMENT_CHANGE_PASSWORD;
                }
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                if(mCurrentFragment != FRAGMENT_HOME){
                    replaceFragment(new HomeFragment(),"FRAGMENT_HOME");
//                    item.setChecked(true);
                    mCurrentFragment = FRAGMENT_HOME;
                }
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }


    private void replaceFragment(Fragment fragment,String name){

        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(binding.appBarMain.contentMain.contentFrame.getId(), fragment,name);
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (Exception e){
            Log.e("error","error: "+e);
        }
    }


}

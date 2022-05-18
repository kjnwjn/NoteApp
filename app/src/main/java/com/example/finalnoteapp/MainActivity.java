package com.example.finalnoteapp;

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
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.example.finalnoteapp.fragment.NewReminderFragment;
import com.example.finalnoteapp.fragment.ReminderFragment;
import com.example.finalnoteapp.fragment.SavingNoteFragment;
import com.example.finalnoteapp.fragment.SettingFragment;
import com.example.finalnoteapp.fragment.TrashbinFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_REMINDER = 1;
    private static final int FRAGMENT_NEW_REMINDER = 2;
    private static final int FRAGMENT_TRANSBIN = 3;
    private static final int FRAGMENT_SETTING = 4;
    private static final int FRAGMENT_SAVING_NOTE = 5;


    private int mCurrentFragment = FRAGMENT_HOME;
    private DrawerLayout mDrawerLayout;
    private RecyclerView itemRV;
    private ActivityMainBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();


        auth = FirebaseAuth.getInstance();
        if(!auth.getCurrentUser().isEmailVerified() ){
            binding.verify.setVisibility(View.VISIBLE);
        }
        binding.verify.setOnClickListener(view -> {
            progressDialog.show();
            auth = FirebaseAuth.getInstance();
            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener((OnCompleteListener<Void>) unused -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Verify Email successfully!", Toast.LENGTH_SHORT).show();
                binding.verify.setVisibility(View.GONE);
            });
        });

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                ,R.string.app_nav_drawer_open,R.string.app_nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.navigation_view);
        nav.setNavigationItemSelectedListener(this);


    }



    private void initView() {
        //SearchBar
        itemRV = binding.idRVItem;
        toolbar  = binding.toolbar;
        mDrawerLayout = binding.drawerLayout;
        progressDialog = new ProgressDialog(this);
        //Tool bar


        binding.logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
        });
        replaceFragment(new HomeFragment());
        binding.navigationView.getMenu().findItem(R.id.app_note).setCheckable(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionSearch:
                Toast.makeText(this, "view was changed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.view:
                break;
            case R.id.account:
                break;
            case R.id.logoutItem:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this,LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.app_note:
                Log.e("nav" ,"home");
                if(mCurrentFragment != FRAGMENT_HOME){
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                }
                break;
            case R.id.reminder:
                Log.e("nav" ,"reminder");
                Toast.makeText(this,"reminder" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_REMINDER){
                    replaceFragment(new ReminderFragment());
                    mCurrentFragment = FRAGMENT_REMINDER;
                }
                break;
            case R.id.app_new_reminder:
                Log.e("nav" ,"app_new_reminder");

                Toast.makeText(this,"app_new_reminder" ,Toast.LENGTH_SHORT).show();

                if(mCurrentFragment != FRAGMENT_NEW_REMINDER){
                    replaceFragment(new NewReminderFragment());
                    mCurrentFragment = FRAGMENT_NEW_REMINDER;
                }
                break;
            case R.id.app_savingNote:
                Log.e("nav" ,"app_savingNote");

                Toast.makeText(this,"app_savingNote" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_SAVING_NOTE){
                    replaceFragment(new SavingNoteFragment());
                    mCurrentFragment = FRAGMENT_SAVING_NOTE;
                }
                break;
            case R.id.app_trashbin:
                Log.e("nav" ,"app_trashbin");

                Toast.makeText(this,"app_trashbin" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_TRANSBIN){
                    replaceFragment(new TrashbinFragment());
                    mCurrentFragment = FRAGMENT_TRANSBIN;
                }
                break;
            case R.id.app_setting:
                Log.e("nav" ,"app_setting");

                if(mCurrentFragment != FRAGMENT_SETTING){
                    replaceFragment(new SettingFragment());
                    mCurrentFragment = FRAGMENT_SETTING;
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

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,fragment);
        transaction.commit();
    }


}
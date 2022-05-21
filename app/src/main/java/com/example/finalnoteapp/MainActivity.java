


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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.example.finalnoteapp.fragment.HomeFragment;
import com.example.finalnoteapp.fragment.NewReminderFragment;
import com.example.finalnoteapp.fragment.ReminderFragment;
import com.example.finalnoteapp.fragment.SavingNoteFragment;
import com.example.finalnoteapp.fragment.SettingFragment;
import com.example.finalnoteapp.fragment.TrashbinFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
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
    private NavigationView nav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

        mDrawerLayout = binding.drawerLayout;
        auth = FirebaseAuth.getInstance();
        if (!auth.getCurrentUser().isEmailVerified()) {
            binding.appBarMain.contentMain.verify.setVisibility(View.VISIBLE);
        }
        binding.appBarMain.contentMain.verify.setOnClickListener(view -> {
            progressDialog.show();
            auth = FirebaseAuth.getInstance();
            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener((OnCompleteListener<Void>) unused -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Verify Email successfully!", Toast.LENGTH_SHORT).show();
                binding.appBarMain.contentMain.verify.setVisibility(View.GONE);
            });
        });

        toolbar = findViewById(binding.appBarMain.toolbar.getId());
        setSupportActionBar(toolbar);
        replaceFragment(new HomeFragment());
//        binding.navigationView.getMenu().findItem(R.id.app_note).setCheckable(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                , R.string.app_nav_drawer_open, R.string.app_nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav = findViewById(R.id.navigation_view);
        nav.getMenu().findItem(R.id.app_note).setChecked(true);
        nav.setNavigationItemSelectedListener(this);

    }


    private void initView() {
        progressDialog = new ProgressDialog(this);
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
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSearch:
                Toast.makeText(this, "view was changed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.view:
                break;
            case R.id.account:
                break;
            case R.id.logoutItem:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.app_note:
                if(mCurrentFragment != FRAGMENT_HOME){
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                }
                break;
            case R.id.reminder:
                if(mCurrentFragment != FRAGMENT_REMINDER){
                    replaceFragment(new ReminderFragment());
                    mCurrentFragment = FRAGMENT_REMINDER;
                }
                break;
            case R.id.app_new_reminder:
                if(mCurrentFragment != FRAGMENT_NEW_REMINDER){
                    replaceFragment(new NewReminderFragment());
                    mCurrentFragment = FRAGMENT_NEW_REMINDER;
                }
                break;
            case R.id.app_savingNote:
                Toast.makeText(this,"app_savingNote" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_SAVING_NOTE){
                    replaceFragment(new SavingNoteFragment());
                    mCurrentFragment = FRAGMENT_SAVING_NOTE;
                }
                break;
            case R.id.app_trashbin:
                Toast.makeText(this,"app_trashbin" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_TRANSBIN){
                    replaceFragment(new TrashbinFragment());
                    mCurrentFragment = FRAGMENT_TRANSBIN;
                }
                break;
            case R.id.app_setting:
                if(mCurrentFragment != FRAGMENT_SETTING){
                    replaceFragment(new SettingFragment());
                    mCurrentFragment = FRAGMENT_SETTING;
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
                    replaceFragment(new HomeFragment());
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


    private void replaceFragment(Fragment fragment){

        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(binding.appBarMain.contentMain.contentFrame.getId(), fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (Exception e){
            Log.e("error","error: "+e);
        }
    }


}

//
//package com.example.finalnoteapp;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//
//import com.example.finalnoteapp.fragment.HomeFragment;
//import com.example.finalnoteapp.fragment.NewReminderFragment;
//import com.example.finalnoteapp.fragment.ReminderFragment;
//import com.example.finalnoteapp.fragment.SavingNoteFragment;
//import com.example.finalnoteapp.fragment.SettingFragment;
//import com.example.finalnoteapp.fragment.TrashbinFragment;
//import com.google.android.material.navigation.NavigationView;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int FRAGMENT_HOME = 0;
//    private static final int FRAGMENT_REMINDER = 1;
//    private static final int FRAGMENT_NEW_REMINDER = 2;
//    private static final int FRAGMENT_TRANSBIN = 3;
//    private static final int FRAGMENT_SETTING = 4;
//    private static final int FRAGMENT_SAVING_NOTE = 5;
//    private int mCurrentFragment = FRAGMENT_HOME;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
//                this,
//                drawer,
//                toolbar,
//                R.string.app_nav_drawer_open,
//                R.string.app_nav_drawer_close
//        );
//        drawer.addDrawerListener(toogle);
//        toogle.syncState();
//
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().
//                beginTransaction();
//        fragmentTransaction.replace(R.id.flContent, new HomeFragment());
//        fragmentTransaction.commit();
//
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.getMenu().findItem(R.id.app_note).setChecked(true);
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        displayView(item);
//                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//                        drawer.closeDrawer(GravityCompat.START);
//                        return false;
//                    }
//                }
//        );
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if(drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }else{
//            super.onBackPressed();
//        }
//
//    }
//    public void displayView(MenuItem item){
//        Fragment fragment = null;
//        switch (item.getItemId()) {
//            case R.id.app_note:
//                if (mCurrentFragment != FRAGMENT_HOME) {
//                    replaceFragment(new HomeFragment());
//
//                    mCurrentFragment = FRAGMENT_HOME;
//                }
//                break;
//            case R.id.reminder:
//                if (mCurrentFragment != FRAGMENT_REMINDER) {
//                    replaceFragment(new ReminderFragment());
////                    item.setChecked(true);
//                    mCurrentFragment = FRAGMENT_REMINDER;
//                }
//                break;
//            case R.id.app_new_reminder:
//
//                if (mCurrentFragment != FRAGMENT_NEW_REMINDER) {
//                    replaceFragment(new NewReminderFragment());
////                    item.setChecked(true);
//                    mCurrentFragment = FRAGMENT_NEW_REMINDER;
//                }
//                break;
//            case R.id.app_savingNote:
//                if (mCurrentFragment != FRAGMENT_SAVING_NOTE) {
//                    replaceFragment(new SavingNoteFragment());
////                    item.setChecked(true);
//                    mCurrentFragment = FRAGMENT_SAVING_NOTE;
//                }
//                break;
//            case R.id.app_trashbin:
//                if (mCurrentFragment != FRAGMENT_TRANSBIN) {
//                    replaceFragment(new TrashbinFragment());
////                    item.setChecked(true);
//                    mCurrentFragment = FRAGMENT_TRANSBIN;
//                }
//                break;
//            case R.id.app_setting:
//
//                if (mCurrentFragment != FRAGMENT_SETTING) {
//                    replaceFragment(new SettingFragment());
////                    item.setChecked(true);
//                    mCurrentFragment = FRAGMENT_SETTING;
//                }
//                break;
//            default:
//
//                if (mCurrentFragment != FRAGMENT_HOME) {
//                    replaceFragment(new HomeFragment());
////                    item.setChecked(true);
//                    mCurrentFragment = FRAGMENT_HOME;
//                }
//                break;
//        }
//        item.setChecked(true);
//
//    }
//    private void replaceFragment(Fragment fragment){
//        try {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.flContent, fragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }catch (Exception e){
//            Log.e("error","error: "+e);
//        }
//    }
//
//}
//

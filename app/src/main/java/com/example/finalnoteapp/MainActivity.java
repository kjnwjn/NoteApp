


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
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalnoteapp.data.Note;
import com.example.finalnoteapp.databinding.ActivityMainBinding;
import com.example.finalnoteapp.databinding.LayoutHeaderNavBinding;
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
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_REMINDER = 1;
    private static final int FRAGMENT_NEW_REMINDER = 2;
    private static final int FRAGMENT_TRANSBIN = 3;
    private static final int FRAGMENT_SETTING = 4;
    private static final int FRAGMENT_SAVING_NOTE = 5;


    private int mCurrentFragment = FRAGMENT_HOME;
    public static int mCurrentTypeDisplay = Note.TYPE_LIST;
    private DrawerLayout mDrawerLayout;
    private ActivityMainBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private NavigationView nav;
    private Menu mMenu;


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
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        this.mMenu = menu;
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

//            TrashbinFragment.setTypeDisplayRecyclerView(Note.TYPE_LIST);
//            mCurrentTypeDisplay =  Note.TYPE_LIST;
//            TrashbinFragment.recyclerView.setLayoutManager(TrashbinFragment.mLinearLayoutManager);
        }else{
            HomeFragment.setTypeDisplayRecyclerView(Note.TYPE_GRID);
            mCurrentTypeDisplay =  Note.TYPE_GRID;
            HomeFragment.recyclerView.setLayoutManager(HomeFragment.mGridLayoutManager);

//            TrashbinFragment.setTypeDisplayRecyclerView(Note.TYPE_GRID);
//            mCurrentTypeDisplay =  Note.TYPE_GRID;
//            TrashbinFragment.recyclerView.setLayoutManager(TrashbinFragment.mGridLayoutManager);
        }
        HomeFragment.noteAdapter.notifyDataSetChanged();
//        TrashbinFragment.noteDeletedAdapter.notifyDataSetChanged();
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
            case R.id.reminder:
                if(mCurrentFragment != FRAGMENT_REMINDER){
                    replaceFragment(new ReminderFragment(),"FRAGMENT_REMINDER");
                    mCurrentFragment = FRAGMENT_REMINDER;
                }
                break;
            case R.id.app_new_reminder:
                if(mCurrentFragment != FRAGMENT_NEW_REMINDER){
                    replaceFragment(new NewReminderFragment(),"FRAGMENT_NEW_REMINDER");
                    mCurrentFragment = FRAGMENT_NEW_REMINDER;
                }
                break;
            case R.id.app_savingNote:
                Toast.makeText(this,"app_savingNote" ,Toast.LENGTH_SHORT).show();
                if(mCurrentFragment != FRAGMENT_SAVING_NOTE){
                    replaceFragment(new SavingNoteFragment(),"FRAGMENT_SAVING_NOTE");
                    mCurrentFragment = FRAGMENT_SAVING_NOTE;
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

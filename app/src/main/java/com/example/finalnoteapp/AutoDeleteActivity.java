package com.example.finalnoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AutoDeleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_delete);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(AutoDeleteActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.time));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
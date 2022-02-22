package com.example.userprofiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MapsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        String message = intent.getStringExtra((ProfilePage.KEY));
//
//        TextView textView = findViewById(R.id.name);
//        textView.setText(message);


    }
}

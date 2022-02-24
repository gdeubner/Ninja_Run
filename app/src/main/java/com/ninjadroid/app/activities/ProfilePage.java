package com.ninjadroid.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninjadroid.app.R;

public class ProfilePage extends AppCompatActivity {
    public static final String KEY = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        String message = intent.getStringExtra((LoginPage.KEY));

        TextView textView = findViewById(R.id.name);
        textView.setText(message);
        final Button mapButton = findViewById(R.id.map);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfilePage.this, MapActivity.class);

                intent.putExtra(KEY, message);
                startActivity(intent);

            }
        });

    }
}

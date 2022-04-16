package com.ninjadroid.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.containers.ProfileContainer;

public class EditProfileActivity extends AppCompatActivity {
    public static final String KEY = "key";

    private String userID;
    private String username;
    private String password;
    private String weight;
    private String heightft;
    private String heightin;
    private String name;

    private ProfileContainer profile;
    private ProfileContainer changedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("AYOOOOOOOO", "im dying");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Log.i("AYOOOOOOOO", "come on plz");
        Intent intent = getIntent();
        //username = intent.getStringExtra((LoginPage.KEY));
        //ProfileContainer temp = intent.getParcelableExtra("profileObject");
        profile = (ProfileContainer) intent.getExtras().getSerializable("profileObject");
        //profile = temp;

        final TextView usernameTitle = findViewById(R.id.usernameE);
        final EditText passwordEdit = findViewById(R.id.passwordEditE);
        final EditText nameEdit = findViewById(R.id.nameEditE);
        final EditText weightEdit = findViewById(R.id.weightEditE);
        final EditText heightftEdit = findViewById(R.id.heightftEditE);
        final EditText heightinEdit = findViewById(R.id.heightinEditE);

        userID = Integer.toString(profile.getUserId());
        username = profile.getUsername();
        password = profile.getPassword();
        weight = Double.toString(profile.getWeight());
        heightft = Integer.toString(profile.getHeight_ft());
        heightin = Double.toString(profile.getHeight_in());
        name = profile.getName();

        usernameTitle.setText(username);
        nameEdit.setText(name);
        passwordEdit.setText(password);
        weightEdit.setText(weight);
        heightftEdit.setText(heightft);
        heightinEdit.setText(heightin);

        Button saveButton = findViewById(R.id.btn_saveChanges);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = nameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                weight = weightEdit.getText().toString();
                heightft = heightftEdit.getText().toString();
                heightin = heightinEdit.getText().toString();

                editInfo(getBaseContext(),userID,username, password,weight,heightft,heightin,name);

                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(R.id.nav_profile, intent);
                finish();

            }
        });

        Button cancelButton = findViewById(R.id.btn_cancelChanges);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EditProfile", "clicked cancel");
                finish();
            }
        });
        getSupportActionBar().setTitle(R.string.edit_profile_title);
    }


    private void editInfo(Context context, String userID,String username, String password, String weight, String heightft, String heightin, String name) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.updateProfile())
                .appendQueryParameter("user_id", userID)
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", password)
                .appendQueryParameter("weight", weight)
                .appendQueryParameter("height_ft", heightft)
                .appendQueryParameter("height_in", heightin)
                .appendQueryParameter("name", name);

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //yay
                        Log.i("successfully updatedddd", "yay");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    Log.e("Get Request Response", error.getMessage());

                } catch (Exception e){
                    Log.e("Get Request Response", "Unspecified server error");
                }

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
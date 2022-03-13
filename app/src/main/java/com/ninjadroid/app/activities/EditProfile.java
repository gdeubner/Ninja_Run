package com.ninjadroid.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.URLBuilder;

public class EditProfile extends AppCompatActivity {
    public static final String KEY = "key";
    private String userID;
    private String username;
    private String password;
    private String weight;
    private String heightft;
    private String heightin;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("hmmm", "seeeeeee");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        //username = intent.getStringExtra((LoginPage.KEY));
        username = intent.getStringExtra("key");

        queryInfo(getBaseContext(), username);

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView usernameTitle = findViewById(R.id.username);
                final EditText passwordEdit = findViewById(R.id.passwordEdit);
                final EditText nameEdit = findViewById(R.id.nameEdit);
                final EditText weightEdit = findViewById(R.id.weightEdit);
                final EditText heightftEdit = findViewById(R.id.heightftEdit);
                final EditText heightinEdit = findViewById(R.id.heightinEdit);
                final Button editButton = findViewById(R.id.editButton);

                name = nameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                weight = weightEdit.getText().toString();
                heightft = heightftEdit.getText().toString();
                heightin = heightinEdit.getText().toString();

                editInfo(getBaseContext(),userID,username, password,weight,heightft,heightin,name);
            }
        });
    }

    private void queryInfo(Context context, String username) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserNamePath())
                .appendQueryParameter("username", username);

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response.length() > 40){
                            String message = response.substring(2, response.length()-2);
                            Log.i("Get Request Response", message);
                            String[] result = message.split(",");

                            final TextView usernameTitle = findViewById(R.id.username);
                            final EditText passwordEdit = findViewById(R.id.passwordEdit);
                            final EditText nameEdit = findViewById(R.id.nameEdit);
                            final EditText weightEdit = findViewById(R.id.weightEdit);
                            final EditText heightftEdit = findViewById(R.id.heightftEdit);
                            final EditText heightinEdit = findViewById(R.id.heightinEdit);
                            final Button editButton = findViewById(R.id.editButton);

                            userID = result[0].substring(18);
                            password = result[2].substring(12, result[2].length()-1);
                            weight = result[3].substring(9);
                            heightft = result[4].substring(12);
                            heightin = result[5].substring(12);
                            name = result[9].substring(8,result[9].length()-1);
                            Log.i("SIGHHHHHHH",userID);

                            usernameTitle.setText(username);
                            nameEdit.setText(name);
                            passwordEdit.setText(password);
                            weightEdit.setText(weight);
                            heightftEdit.setText(heightft);
                            heightinEdit.setText(heightin);

                        }else{
                            Toast.makeText(EditProfile.this, "Couldn't Find User!",
                                    Toast.LENGTH_SHORT).show();
                        }

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
                        Intent intent = new Intent(EditProfile.this, MainActivity.class);
                        intent.putExtra("key", userID);
                        startActivity(intent);

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
package com.ninjadroid.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.URLBuilder;

public class LoginPage extends AppCompatActivity {

    public static final String KEY = "key";
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.signin);
        final Button registerButton = findViewById(R.id.register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(username.equals("test") ){
                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    intent.putExtra(KEY, 17);
                    startActivity(intent);
                }else if(username.equals("") || password.equals("")){
                    Toast.makeText(LoginPage.this, "Please Enter Username and Password!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    queryID(getBaseContext(), username, password);
                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(username.equals("") || password.equals("")){
                    Toast.makeText(LoginPage.this, "Please Enter A Username and Password!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(getBaseContext(), username, password);
                }

            }
        });
    }

    private void queryID(Context context, String username, String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserIDPath())
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", password);

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response.length() > 4){
                            String message = response.substring(2, response.length()-2);
                            Log.i("Get Request Response", message);

                            Intent intent = new Intent(LoginPage.this, MainActivity.class);
                            intent.putExtra(KEY, message);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginPage.this, "Please Enter A Valid Username and Password!",
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
    private void registerUser(Context context, String username, String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.registerUser())
                .appendQueryParameter("var_un", username)
                .appendQueryParameter("var_pw", password)
                .appendQueryParameter("var_lb","0")
                .appendQueryParameter("var_ft","0")
                .appendQueryParameter("var_in","0")
                .appendQueryParameter("var_nam","Name");

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response.length() > 6){
                            String message = response.substring(1, response.length()-1);
                            Log.i("Get Request Response", message);

                            Intent intent = new Intent(LoginPage.this, EditProfile.class);
                            intent.putExtra(KEY, username);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginPage.this, "That Username is Taken!",
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
}
package com.ninjadroid.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.webServices.AddHistory;

import java.io.File;

public class LoginActivity extends AppCompatActivity {

    public static final String KEY = "userID";
    private String username = "";
    private String password = "";

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button registerButton;
    Activity activity;

    /**
     * assigns the various views and assigns the login and register buttons their clickListeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

          usernameEditText = findViewById(R.id.username);
          passwordEditText = findViewById(R.id.password);
          loginButton = findViewById(R.id.signin);
          registerButton = findViewById(R.id.register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(username.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Please Enter Username and Password!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    queryID(getBaseContext(), username, password);
                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                String content = username + "," + password;
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("info", content);
                startActivity(intent);

            }
        });
        activity = this;
    }

    /**
     * checks to see if the app has permission to use the phone's location services
     */
    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    /**
     * checks to see if the app has permission to use the phone's location services and
     * requests them it the app does not
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use " +
                                "location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    /**
     * asks the server if the username password combo is valid. If it is, the app proceeds to the
     * next page, otherwise it launches a Toast message, reporting a bad username/password.
     * @param context
     * @param username used for login
     * @param password used for login
     */
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

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(KEY, message);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "Please Enter A Valid Username and Password!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(LoginActivity.this, "Sorry! Our Server is Down!",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Get Request Response", error.getMessage());

                } catch (Exception e){
                    Log.e("Get Request Response", "Unspecified server error");
                }

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * empties the password field if the user navigates back to the login page
     */
    @Override
    protected void onResume() {
        super.onResume();
        passwordEditText.setText("");
    }

    /**
     * if the user presses the back button on this login page, popup will appear, asking if the user
     * really wants to leave the app
     */
    @Override
    public void onBackPressed() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You're about to exit Ninja Run")
                    .setTitle("Are you sure?");
            builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
    }
}
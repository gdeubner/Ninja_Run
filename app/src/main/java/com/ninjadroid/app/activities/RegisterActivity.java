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

public class RegisterActivity extends AppCompatActivity {

    private String username;
    private String password;
    private String weight;
    private String heightft;
    private String heightin;
    private String name;

    /**
     * assigns the text views and gives the buttons their onClickListeners
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameEdit = findViewById(R.id.usernameEditR);
        final EditText passwordEdit = findViewById(R.id.passwordEditR);
        final EditText nameEdit = findViewById(R.id.nameEditR);
        final EditText weightEdit = findViewById(R.id.weightEditR);
        final EditText heightftEdit = findViewById(R.id.heightftEditR);
        final EditText heightinEdit = findViewById(R.id.heightinEditR);

        Intent intent = getIntent();
        //username = intent.getStringExtra((LoginPage.KEY));
        String info = intent.getStringExtra("info");

        if(info.equals(",")){
        }else if(info.length() > 1 && info.charAt(0)==','){
            password = info.substring(1);
            passwordEdit.setText(password);
        }else if(info.length() > 1 && info.charAt(info.length()-1)==','){
            username = info.substring(0,info.length()-1);
            usernameEdit.setText(username);
        } else{
            String temp[] = info.split(",");
            username = temp[0];
            password = temp[1];
            usernameEdit.setText(username);
            passwordEdit.setText(password);
        }


        Button editButton = findViewById(R.id.registerButtonR);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                name = nameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                weight = weightEdit.getText().toString();
                heightft = heightftEdit.getText().toString();
                heightin = heightinEdit.getText().toString();

                registerUser(getBaseContext(),username, password,weight,heightft,heightin,name);
            }
        });

        Button cancelButton = findViewById(R.id.backButtonR);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * sends the user-entered data to the server to create the new user account. Provides an error
     * toast message if the username is already taken or the user left fields blank
     * @param context
     * @param username
     * @param password
     * @param weight
     * @param heightft
     * @param heightin
     * @param name
     */
    private void registerUser(Context context, String username, String password, String weight,
                              String heightft, String heightin, String name) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.registerUser())
                .appendQueryParameter("var_un", username)
                .appendQueryParameter("var_pw", password)
                .appendQueryParameter("var_lb", weight)
                .appendQueryParameter("var_ft", heightft)
                .appendQueryParameter("var_in", heightin)
                .appendQueryParameter("var_nam", name);

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response.length() == 6 && response.substring(1,5).equals("fail")) {
                            Toast.makeText(RegisterActivity.this, "That Username is Taken!",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            String userID = response.substring(2, response.length() - 2);

                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            intent.putExtra("ProfileFragment",1);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(RegisterActivity.this, "Please Fill Out Everything!",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Get Request Response", error.getMessage());

                } catch (Exception e) {
                    Log.e("Get Request Response", "Unspecified server error");
                }

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * stops
     */
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
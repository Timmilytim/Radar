package com.example.ticketingsystem;

import static com.example.ticketingsystem.URL.LOGIN;
import static com.example.ticketingsystem.URL.SIGNUP;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Login Button
        Button lbutton = (Button)findViewById(R.id.login);

        // Switch to Sign Up Page
        TextView switchPage = (TextView) findViewById(R.id.switchPageToSignup);

        //        TO SWITCH TO DASHBOARD
        lbutton.setOnClickListener(v -> {
            // Email and Password
            EditText lUser = (EditText)findViewById(R.id.loginUsername);
            String lu = lUser.getText().toString();

            EditText lPassword = (EditText)findViewById(R.id.loginPassword);
            String lp = lPassword.getText().toString();


            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", lu);
                jsonBody.put("password", lp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN, jsonBody,
                    response -> {
                        try {
                            Log.d("Response", "LOGIN SUCCESSFUL" + response.toString());

                            // Extract user details from the response
                            int userId = response.optInt("user_id");
                            String email = response.optString("email");
                            String username = response.optString("username");
                            double balance = response.optDouble("balance");

                            // Store the details in UserSession
                            UserSession userSession = UserSession.getInstance();
                            userSession.setUserId(userId);
                            userSession.setEmail(email);
                            userSession.setUsername(username);
                            userSession.setBalance(balance);

                            // Start the Dashboard activity
                            Intent i1 = new Intent(getApplicationContext(), Dashboard.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            finish();
                        } catch (Exception e) {
                            Log.e("Error", "Error parsing response", e);
                        }
                    }, error -> {
                TextView errorMsg = findViewById(R.id.errormsg);
                Log.e("Error", "Error occurred: ", error);

                String errorMessage = error.getMessage();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String body;
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        JSONObject errorJson = new JSONObject(body);
                        errorMessage = errorJson.optString("message", "An error occurred.");
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                errorMsg.setText(errorMessage);

//                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            });

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(jsonObjectRequest);

            Intent i1 = new Intent(getApplicationContext(), Dashboard.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i1);
            finish();
        });

        switchPage.setOnClickListener(view -> Switch.goToSignUp(MainActivity.this));

    }
}
package com.example.ticketingsystem;

import static com.example.ticketingsystem.URL.LOGIN;
import static com.example.ticketingsystem.URL.SIGNUP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
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
        lbutton.setOnClickListener(v -> login());

        switchPage.setOnClickListener(view ->{
            Loader.showLoader(this);
            Switch.goToSignUp(MainActivity.this);
        });

    }

    private void login(){
        Loader.showLoader(this);

        // Email and Password
        EditText lUser = findViewById(R.id.loginUsername);
        String lu = lUser.getText().toString();

        EditText lPassword = findViewById(R.id.loginPassword);
        String lp = lPassword.getText().toString();

        if (lu.isEmpty() || lp.isEmpty()){
            Loader.hideLoader(this);
            TextView errorMsg = findViewById(R.id.errormsg);
            errorMsg.setText("Please Fill all Fields");
        }


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", lu);
            jsonBody.put("password", lp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN, jsonBody,
                response -> {
                    try {
                        Log.d("Response", "LOGIN SUCCESSFUL" + response.toString());
                        Toast.makeText(MainActivity.this, "LOGIN SUCCESSFUL ", Toast.LENGTH_LONG).show();

                        // Extract user details from the response
                        JSONObject userInfo = response.getJSONObject("user_info");
                        int userId = userInfo.optInt("user_id");
                        String username = userInfo.optString("username");
                        String firstname = userInfo.optString("first_name");
                        String lastname = userInfo.optString("last_name");
                        String email = userInfo.optString("email");
                        String phone = userInfo.optString("phone_number");
                        double balance = userInfo.optDouble("wallet_balance");

                        // Store the details in UserSession
                        UserSession userSession = UserSession.getInstance();
                        userSession.setUserId(userId);
                        userSession.setUsername(username);
                        userSession.setFirstname(firstname);
                        userSession.setLastname(lastname);
                        userSession.setEmail(email);
                        userSession.setPhone(phone);
                        userSession.setBalance(balance);

                        // To Start the Dashboard activity
                        Intent i1 = new Intent(getApplicationContext(), Dashboard.class);
                        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i1);
                        Loader.hideLoader(this);
                        finish();
                    } catch (Exception e) {
                        Log.e("Error", "Error parsing response", e);
                    }
                }, error -> {
            Loader.hideLoader(this);
            TextView errorMsg = findViewById(R.id.errormsg);
            errorMsg.setText("Invalid Credentials");
            Log.e("Error", "Error occurred: ", error);

        // TO Check if the Internet is Active
            if (isNetworkActive(error)) {
                String errorMessage = error.getMessage();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String body;
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        JSONObject errorJson = new JSONObject(body);
                        errorMessage = errorJson.optString("message");
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                } else if (error instanceof com.android.volley.NoConnectionError) {
                    errorMessage = "No internet connection. Please check your connection and try again.";
                }

                errorMsg.setText(errorMessage);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(jsonObjectRequest);

    }

    private boolean isNetworkActive(VolleyError error) {
        String errorMessage = "";
        if (error != null) {
            if (error.networkResponse != null && error.networkResponse.data != null) {
                try {
                    String body = new String(error.networkResponse.data, "UTF-8");
                    JSONObject errorJson = new JSONObject(body);
                    errorMessage = errorJson.optString("message");
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            } else if (error instanceof com.android.volley.NoConnectionError) {
                errorMessage = "No internet connection. Please check your connection and try again.";
                Loader.hideLoader(this);
            }
            // Log or display errorMessage as needed
            Log.e("Network Error", errorMessage);
            return error instanceof com.android.volley.NoConnectionError;
        } else {

            return false;
        }
    }


}
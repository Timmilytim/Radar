package com.example.ticketingsystem;

import static com.example.ticketingsystem.URL.SIGNUP;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sign Up Button
        Button signup = (Button)findViewById(R.id.signup);

        TextView switchPage = (TextView)findViewById(R.id.switchPagetoLogin);

        switchPage.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToLogin(signup.this);});

        signup.setOnClickListener(view -> {
            Loader.showLoader(this);
            // Calling all ids and converting them to Strings
            EditText firstName = (EditText)findViewById(R.id.firstName);
            String fn = firstName.getText().toString();

            EditText lastName = (EditText)findViewById(R.id.lastName);
            String ln = lastName.getText().toString();

            EditText userName = (EditText) findViewById(R.id.username);
            String un = userName.getText().toString();

            EditText emailAddress = (EditText)findViewById(R.id.email);
            String em = emailAddress.getText().toString();

            EditText phoneNumber = (EditText)findViewById(R.id.phone);
            String pn = phoneNumber.getText().toString();

            EditText password = (EditText)findViewById(R.id.password);
            String ps = password.getText().toString();

            EditText conPassword = (EditText)findViewById(R.id.confirm);
            String cps = conPassword.getText().toString();

            TextView passChecker = (TextView)findViewById(R.id.passwordChecker);


//                PASSWORD CHECKER
            String checker = null;
            if(!ps.equals(cps)){
                checker = "Make sure your password is corresponding";
            } else {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", em);
                    jsonBody.put("username", un.toLowerCase());
                    jsonBody.put("first_name", fn);
                    jsonBody.put("last_name", ln);
                    jsonBody.put("password", cps);
                    jsonBody.put("phone_number", pn);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Loader.hideLoader(this);
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGNUP, jsonBody,
                        response -> {
                            Log.d("Response", "SIGN UP SUCCESSFUL" + response.toString());
                            Loader.hideLoader(this);
                            Intent i1 = new Intent(getApplicationContext(), SignupSuccess.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            finish();
                        }, error -> {
                    Log.e("Error", "Error occurred", error);
                    Loader.hideLoader(this);
                });

                RequestQueue queue = Volley.newRequestQueue(signup.this);
                queue.add(jsonObjectRequest);
            }
            passChecker.setText(checker);
        });


    }
}
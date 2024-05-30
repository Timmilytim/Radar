package com.example.ticketingsystem;

import static com.example.ticketingsystem.URL.BALANCE;
import static com.example.ticketingsystem.URL.CREDIT;
import static com.example.ticketingsystem.URL.DEBIT;
import static com.example.ticketingsystem.URL.FUND;
import static com.example.ticketingsystem.URL.USERCHECK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Wallet extends AppCompatActivity {
    private LinearLayout booking;
    private LinearLayout history;
    private LinearLayout settings;
    private static CardView fundView;
    private LinearLayout fund;
    private static CardView transferView;
    private LinearLayout transfer;
    private TextView bal;
    private static EditText amount;
    private EditText username;
    private EditText tfAmount;
    private Button pay;
    private Button tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.wallet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();

        bal = findViewById(R.id.balance);
        booking = findViewById(R.id.booking);
        history = findViewById(R.id.history);
        settings = findViewById(R.id.settings);
        fund = findViewById(R.id.fund);
        transfer = findViewById(R.id.transfer);
        fundView = findViewById(R.id.pay);
        transferView = findViewById(R.id.transferview);
        username = findViewById(R.id.username);
        tfAmount = findViewById(R.id.tfamount);
        pay = findViewById(R.id.submit);
        tf = findViewById(R.id.send);
        amount = findViewById(R.id.amount);


        fetchBalance(userId);

        fund.setOnClickListener(v -> {

            fundWallet();});
        pay.setOnClickListener(v-> payFunds());

        transfer.setOnClickListener(v-> {
            transferCash();});


        tf.setOnClickListener(v -> {
            String un = username.getText().toString();
            isUserExisting(un.toLowerCase());
        });

//  SWITCHING THE INTENT FROM THE SWITCH CLASS
        booking.setOnClickListener(view ->{
            Loader.showLoader(this);
            Switch.goToBooking(Wallet.this);
        });

        history.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToHistory(Wallet.this);
        });

        settings.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToSettings(Wallet.this);
        });
    }

    public void transferCash() {
        if (transferView.getVisibility() == View.GONE) {
            inAnimation(transferView);
            transferView.setVisibility(View.VISIBLE);
            if (fundView.getVisibility() == View.VISIBLE) {

                fundView.setVisibility(View.GONE);
            }
        } else {
            outAnimation(transferView);
            transferView.setVisibility(View.GONE);
        }
    }

    public void fundWallet() {
        if (fundView.getVisibility() == View.GONE) {
            inAnimation(fundView);
            fundView.setVisibility(View.VISIBLE);
            if (transferView.getVisibility() == View.VISIBLE) {

                transferView.setVisibility(View.GONE);
            }
        } else {
            outAnimation(fundView);
            fundView.setVisibility(View.GONE);
        }
    }


    public void payFunds(){
        Loader.showLoader(this);
        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();
        String email = userSession.getEmail();
        int amt = Integer.parseInt(amount.getText().toString());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("amount", amt);
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FUND, jsonBody,
                response -> {
                    Loader.hideLoader(this);
                    Log.d("Response", "FUND SUCCESSFUL: " + response.optString("authorization_url"));

                    String authorizationUrl = response.optString("authorization_url");


                    // Opening the URL in a browser through the Payment
                    Intent intent = new Intent(Wallet.this, Payment.class);
                    intent.putExtra("URL", authorizationUrl);
                    startActivity(intent);

                }, error -> {
            Loader.hideLoader(this);
            Log.e("Error", "Error occurred: ", error);
            Toast.makeText(this, "Failed to fund wallet", Toast.LENGTH_SHORT).show();

            // To Check if it's a timeout error
//            if (error instanceof TimeoutError) {
//                Loader.hideLoader(this);
//                Toast.makeText(this, "Request timed out. Please try again later.", Toast.LENGTH_SHORT).show();
//            }
        });
            // To  Retry the funding 3 times
//            int maxRetries = 3;
//            int initialTimeoutMs = 5000;
//            float backoffMultiplier = 1.0f;
//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxRetries, backoffMultiplier));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void isUserExisting(String username) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Loader.showLoader(this);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("isUserExisting", "URL: " + USERCHECK);
            Log.d("isUserExisting", "Request Body: " + jsonBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, USERCHECK, jsonBody,
                    response -> {
                        Loader.hideLoader(this);
                        String res  = response.optString("success");
                        Log.d("isUserExisting", "Response: " + response.toString());
                        send();
                    }, error -> {
                Loader.hideLoader(this);
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    Log.e("isUserExisting", "Error Status Code: " + statusCode);
                    Log.e("isUserExisting", "Error Response Data: " + new String(error.networkResponse.data));
                } else {
                    Log.e("isUserExisting", "Error: ", error);
                }
                Toast.makeText(this, "Username does not exist", Toast.LENGTH_SHORT).show();
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);
        }

    }



    public void send(){
        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();
        Log.d("user", String.valueOf(userId));

        String un = username.getText().toString();

        int tfa = Integer.parseInt(tfAmount.getText().toString());

        Loader.showLoader(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("amount", tfa);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DEBIT, jsonBody,
                response -> {
                         String reference = response.optString("reference");
                         int amount = response.optInt("amount");

                         Loader.hideLoader(this);
                        Log.d("Response", "PENDING" + response.toString());


                        credit(un,reference, amount);

                }, error -> {
            Loader.hideLoader(this);
            Log.e("Error", "Error occurred: ", error);
            Toast.makeText(this, "Failed to debit account", Toast.LENGTH_SHORT).show();

        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void inAnimation(CardView cardView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in);
        cardView.startAnimation(animation);
    }

    private void outAnimation(CardView cardView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.out);
        cardView.startAnimation(animation);
    }

    private void credit (String username, String reference, int amount){
        Loader.showLoader(this);

        Log.d("message1", username);
        Log.d("message2", reference);
        Log.d("message3", String.valueOf(amount));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("receiver_username", username);
            jsonBody.put("reference", reference);
            jsonBody.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, CREDIT, jsonBody,
                response -> {
                    Loader.hideLoader(this);
                    String success = response.optString("message");
                    Log.d("Response", "TRANSFER SUCCESSFUL: " + response.toString());

                    Intent intent = new Intent(Wallet.this, Wallet.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(this, success.toString(), Toast.LENGTH_SHORT).show();

                }, error -> {
                    Loader.hideLoader(this);
                    Log.e("Error", "Error occurred: ", error);
                    Toast.makeText(this, "Failed to credit account", Toast.LENGTH_SHORT).show();

        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void fetchBalance(int userId) {
        Loader.showLoader(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BALANCE, jsonBody,
                response -> {
                    double balance = response.optInt("wallet_balance");
                    bal.setText( "\u20A6"+ balance);
                    Loader.hideLoader(this);
                }, error -> {
            Loader.hideLoader(this);
            Log.e("Balance", "Error occurred: ", error);
            Toast.makeText(this, "Failed to Load Balance Data", Toast.LENGTH_SHORT).show();
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }




}
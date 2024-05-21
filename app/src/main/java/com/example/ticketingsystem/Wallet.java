package com.example.ticketingsystem;

import static com.example.ticketingsystem.URL.FUND;
import static com.example.ticketingsystem.URL.TRANSFER;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

        String balance = "\u20A6" + userSession.getBalance();

        bal.setText(balance);



        fund.setOnClickListener(v -> fundWallet());
        pay.setOnClickListener(v-> payFunds());

        transfer.setOnClickListener(v-> transferCash());
        tf.setOnClickListener(v -> send());

//  SWITCHING THE INTENT FROM THE SWITCH CLASS
        booking.setOnClickListener(view -> Switch.goToBooking(Wallet.this));

        history.setOnClickListener(view -> Switch.goToHistory(Wallet.this));

        settings.setOnClickListener(view -> Switch.goToSettings(Wallet.this));
    }

    public static void transferCash() {
        if (transferView.getVisibility() == View.GONE) {
            transferView.setVisibility(View.VISIBLE);
            if (fundView.getVisibility() == View.VISIBLE) {
                fundView.setVisibility(View.GONE);
            }
        } else {
            transferView.setVisibility(View.GONE);
        }
    }

    public static void fundWallet() {
        if (fundView.getVisibility() == View.GONE) {
            fundView.setVisibility(View.VISIBLE);
            if (transferView.getVisibility() == View.VISIBLE) {
                transferView.setVisibility(View.GONE);
            }
        } else {
            fundView.setVisibility(View.GONE);
        }
    }

    public void payFunds(){

        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();
        String email = userSession.getEmail();
        int amt = Integer.parseInt(amount.getText().toString());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("email", email);
            jsonBody.put("amount", amt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FUND, jsonBody,
                response -> {
                    try {
                        Log.d("Response", "FUND SUCCESSFUL: " + response.toString());
                        String authorizationUrl = response.getString("authorization_url");

                        // Opening the URL in a browser through the Payment
                        Intent intent = new Intent(Wallet.this, Payment.class);
                        intent.putExtra("URL", authorizationUrl);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Log.e("Error", "Error parsing response", e);
                    }
                }, error -> {
            Log.e("Error", "Error occurred: ", error);

        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    public void send(){
        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();
        String un = username.getText().toString();
        int tfa = Integer.parseInt(tfAmount.getText().toString());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            jsonBody.put("username", un);
            jsonBody.put("amount", tfa);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TRANSFER, jsonBody,
                response -> {
                        Log.d("Response", "TRANSFER SUCCESSFUL: " + response.toString());

                }, error -> {
            Log.e("Error", "Error occurred: ", error);

        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

}
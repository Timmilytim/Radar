package com.example.ticketingsystem;

import static com.example.ticketingsystem.URL.HISTORY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class History extends AppCompatActivity {

    private LinearLayout booking;
    private LinearLayout wallet;
    private LinearLayout settings;

    private TextView loadHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchHistory();

        booking = findViewById(R.id.booking);
        wallet = findViewById(R.id.wallet);
        settings = findViewById(R.id.settings);
        loadHistory =  findViewById(R.id.appHistory);



//  SWITCHING THE INTENT FROM THE SWITCH CLASS
        booking.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToBooking(History.this);
        });

        wallet.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToWallet(History.this);
        });

        settings.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToSettings(History.this);
        });
    }

    private void fetchHistory() {

        Loader.showLoader(this);
        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, HISTORY, jsonBody,
                response -> {
                    try {
                        Loader.hideLoader(this);
                        String historyData = response.getString("data");
                        loadHistory.setText(historyData);
                    } catch (JSONException e) {
                        Log.e("History", "Error parsing response", e);
                    }
                }, error -> {
            Loader.hideLoader(this);
            Log.e("History", "Error occurred: ", error);
            Toast.makeText(this, "Fail to Load History Data", Toast.LENGTH_SHORT).show();
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}
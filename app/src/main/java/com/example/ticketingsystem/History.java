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
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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
//        loadHistory =  findViewById(R.id.appHistory);



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
                        parseAndDisplayHistory(response);

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

    private void parseAndDisplayHistory(JSONObject response) throws JSONException {
        LinearLayout historyLayout = findViewById(R.id.historyLayout);  // Ensure this ID matches your XML layout
        historyLayout.removeAllViews();  // Clear any existing views

        // Fetch the array of user tickets
        JSONArray userTicketsArray = response.getJSONArray("user_tickets");

        Log.d("History", "Number of tickets: " + userTicketsArray.length());

        // Loop through each ticket and create a TextView for each
        for (int i = 0; i < userTicketsArray.length(); i++) {
            JSONObject ticket = userTicketsArray.getJSONObject(i);

            int ticketId = ticket.getInt("ticket_id");
            String tripType = ticket.getString("trip_type");
            String fromLoc = ticket.getString("from_loc");
            String toLoc = ticket.getString("to_loc");
            String bookingDate = ticket.getString("booking_date");
            String transportDate = ticket.getString("transport_date");
            String price = ticket.getString("price");

            Log.d("History", "Ticket ID: " + ticketId + ", From: " + fromLoc + ", To: " + toLoc);

            // Create a new CardView for each ticket
            CardView ticketContainer = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 16); // Add margin between each card
            ticketContainer.setLayoutParams(cardParams);
            ticketContainer.setCardBackgroundColor(getResources().getColor(R.color.white));
            ticketContainer.setRadius(20);
            ticketContainer.setCardElevation(4);

            // Create a new LinearLayout for the content of the card view
            LinearLayout cardContentLayout = new LinearLayout(this);
            cardContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            cardContentLayout.setOrientation(LinearLayout.VERTICAL);
            cardContentLayout.setPadding(16, 16, 16, 10); // Add padding to the content layout

            // Create a new TextView for each ticket
            TextView ticketView = new TextView(this);
            ticketView.setTextSize(18); // Set text size in sp
            ticketView.setTextColor(getResources().getColor(R.color.green));
            ticketView.setPadding(20,20,20,0);
            ticketView.setText(
                    "Ticket ID: " + ticketId + "\n" +
                            "Trip Type: " + tripType + "\n" +
                            "From: " + fromLoc + "\n" +
                            "To: " + toLoc + "\n" +
                            "Booking Date: " + bookingDate + "\n" +
                            "Transport Date: " + transportDate + "\n" +
                            "Price: " + price + "\n"
            );

            // Add the TextView to the LinearLayout
            ticketContainer.addView(ticketView);
           historyLayout.addView(ticketContainer);
        }

    }

}
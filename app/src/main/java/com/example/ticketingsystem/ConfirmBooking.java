package com.example.ticketingsystem;


import static com.example.ticketingsystem.URL.BOOKING;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class ConfirmBooking extends AppCompatActivity {

    private TextView to;
    private TextView from;
    private TextView date;
    private  TextView tripType;
    private TextView amount;
    private Button purchase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.confirm_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tripType = findViewById(R.id.tripType);
        to = findViewById(R.id.confirm_to);
        from = findViewById(R.id.confirm_from);
        date = findViewById(R.id.date);
        purchase = findViewById(R.id.purchase);
        amount = findViewById(R.id.price);


        String checkOutTrip = getIntent().getStringExtra("tripType");
        tripType.setText(checkOutTrip);

        String checkOutTo = getIntent().getStringExtra("to");
        to.setText(checkOutTo);

        String checkOutFrom = getIntent().getStringExtra("from");
        from.setText(checkOutFrom);

        String checkOutDate = getIntent().getStringExtra("date");
        date.setText(checkOutDate);

        String price = "\u20A6"+ getIntent().getStringExtra("price");
        amount.setText(price);

        int cPrice = Integer.parseInt(getIntent().getStringExtra("price"));


        purchase.setOnClickListener(v -> {
            Loader.showLoader(this);
            UserSession userSession = UserSession.getInstance();
            int userId = userSession.getUserId();
            Log.d("user", String.valueOf(userId));
            Log.d("message1", checkOutTrip.toString());
            Log.d("message2", checkOutFrom.toString());
            Log.d("message3", checkOutTo.toString());
            Log.d("message4", checkOutDate.toString());
            Log.d("message5", String.valueOf(cPrice));


            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("user_id", userId);
                jsonBody.put("trip_type", checkOutTrip);
                jsonBody.put("from_loc", checkOutFrom);
                jsonBody.put("to_loc", checkOutTo);
                jsonBody.put("transport_date", checkOutDate);
                jsonBody.put("price", cPrice);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BOOKING, jsonBody,
                    response -> {
                        String message = response.optString("message");
                        Log.d("Response", "BOOKING SUCCESSFUL" + message);

                        Intent intent = new Intent(ConfirmBooking.this, Success.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }, error -> {
                Loader.hideLoader(this);
                Toast.makeText(this, "Unable to purchase ticket", Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error occurred", error);
            });

            RequestQueue queue = Volley.newRequestQueue(ConfirmBooking.this);
            queue.add(jsonObjectRequest);

        });
    }

}



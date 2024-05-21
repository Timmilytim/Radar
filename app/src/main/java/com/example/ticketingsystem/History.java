package com.example.ticketingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class History extends AppCompatActivity {

    private LinearLayout booking;
    private LinearLayout wallet;
    private LinearLayout settings;

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

        booking = findViewById(R.id.booking);
        wallet = findViewById(R.id.wallet);
        settings = findViewById(R.id.settings);

//        NAVIGATING TO THE Booking INTENT
        booking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent i5 = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(i5);

            }
        });

//  SWITCHING THE INTENT FROM THE SWITCH CLASS
        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch.goToBooking(History.this);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch.goToWallet(History.this);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch.goToSettings(History.this);
            }
        });
    }
}
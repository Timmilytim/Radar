package com.example.ticketingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    private LinearLayout booking;
    private LinearLayout wallet;
    private LinearLayout history;
    private TextView firstName;
    private  TextView lastName;
    private TextView username;
    private TextView email;
    private  TextView phone;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        booking = findViewById(R.id.booking);
        wallet =  findViewById(R.id.wallet);
        history = findViewById(R.id.history);
        logout = findViewById(R.id.logout);

        UserSession userSession = UserSession.getInstance();

        String fName = userSession.getFirstname();
        firstName.setText(fName);

        String lName = userSession.getLastname();
        lastName.setText(lName);

        String uName = userSession.getUsername();
        username.setText(uName);

        String eAddress = userSession.getEmail();
        email.setText(eAddress);

        String pNumber = userSession.getPhone();
        phone.setText(pNumber);

//  SWITCHING INTENTS FROM THE SWITCH CLASS
        logout.setOnClickListener(view -> handleLogout());

        booking.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToBooking(Settings.this);
        });

        wallet.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToWallet(Settings.this);
        });

        history.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToHistory(Settings.this);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleLogout();  // Log out the user
    }

    public void handleLogout(){
        Loader.showLoader(this);
        UserSession userSession = UserSession.getInstance();
        userSession.setUserId(0);
        userSession.setEmail(null);
        userSession.setUsername(null);
        userSession.setBalance(0.0);

        Intent intent = new Intent(Settings.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
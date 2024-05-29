package com.example.ticketingsystem;
//import static com.example.ticketingsystem.URL.BOOKING;
import static com.example.ticketingsystem.URL.BALANCE;
import static com.example.ticketingsystem.URL.PRICE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;

public class Dashboard extends AppCompatActivity {

    private TextView uName;
    private TextView bal;
    private EditText date;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private RadioGroup radioGroup;
    private RadioButton oneWay;
    private RadioButton roundTrip;
    private MaterialButton fund;
    private Button submit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LocalTime currentTime = LocalTime.now();

        UserSession userSession = UserSession.getInstance();
        int userId = userSession.getUserId();
        fetchBalance(userId);

        uName = findViewById(R.id.uName);
        bal = findViewById(R.id.balance);
        fromSpinner = findViewById(R.id.from_spinner);
        toSpinner = findViewById(R.id.to_spinner);
        date = findViewById(R.id.date);
        LinearLayout wallet = findViewById(R.id.wallet);
        LinearLayout history = findViewById(R.id.history);
        LinearLayout settings = findViewById(R.id.settings);
        radioGroup = findViewById(R.id.radio_group);
        oneWay = findViewById(R.id.one_button);
        roundTrip = findViewById(R.id.round_button);
        submit = findViewById(R.id.submit);
        fund = findViewById(R.id.fund);

        String time =  greeting(currentTime);

        String userName = time + userSession.getUsername();
        uName.setText(userName);

        String balance = "\u20A6" + userSession.getBalance();
        bal.setText(balance);

        date.setOnClickListener(v -> {showDatePicker();});

        ArrayAdapter<CharSequence> locations = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_dropdown_item);
        locations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        fromSpinner.setAdapter(locations);
        toSpinner.setAdapter(locations);




//SUBMITTING THE SELECTED OPTIONS
        submit.setOnClickListener(v -> handleSubmit());

//SWITCHING THE INTENT FROM THE SWITCH CLASS
        wallet.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToWallet(Dashboard.this);});

        history.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToHistory(Dashboard.this);});

        settings.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToSettings(Dashboard.this);});

        fund.setOnClickListener(view -> {
            Loader.showLoader(this);
            Switch.goToWallet(Dashboard.this);
        });

    }

    // METHODS

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String greeting(LocalTime currentTime){
        if (currentTime.isBefore(LocalTime.NOON)){
            return "Good Morning ";
        } else if (currentTime.isBefore(LocalTime.of(17,00))) {
            return "Good Afternoon ";
        }else {
            return "Good Evening ";
        }
    }

    private void showDatePicker(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth +"-"+ (month1 + 1) +"-"+ year1;
                    date.setText(selectedDate);
                    Log.d("testdate", selectedDate);
                },
               year, month, day

                );
        datePicker.show();
    }

    private void handleSubmit() {
        Loader.showLoader(this);
        String selectedDateStr = date.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar selectedDate = Calendar.getInstance();
        try {
            selectedDate.setTime(sdf.parse(selectedDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar currentDate = Calendar.getInstance();

        String from = fromSpinner.getSelectedItem().toString();
        String to = toSpinner.getSelectedItem().toString();

        if (selectedDate.before(currentDate) && !todayDate(selectedDate, currentDate)) {
            Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show();
        } else if (from.equals(to)) {
            Toast.makeText(this, "Please select different locations", Toast.LENGTH_SHORT).show();
        }else if (selectedDateStr.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
        }
        else {
            from = fromSpinner.getSelectedItem().toString();
            to = toSpinner.getSelectedItem().toString();

            String tripType;
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            if (radioButtonId == R.id.one_button) {
                tripType = getString(R.string.ow);
            } else if (radioButtonId == R.id.round_button) {
                tripType = getString(R.string.rw);
            } else {
                tripType = "";
            }


            JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("from_loc", from);
            jsonBody.put("to_loc", to);
            jsonBody.put("trip_type",tripType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

            String finalFrom = from;
            String finalTo = to;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PRICE, jsonBody,
                response -> {
                    Log.d("Response", "CONFIRM BOOKING" + response.toString());
                    String price = response.optString("price");

                    Intent i1 = new Intent(getApplicationContext(), ConfirmBooking.class);

                    i1.putExtra("from", finalFrom);
                    i1.putExtra("to", finalTo);
                    i1.putExtra("date", selectedDateStr);
                    i1.putExtra("tripType", tripType);
                    i1.putExtra("price", price);
                    startActivity(i1);
                }, error -> {
            Log.e("Error", "Error occurred", error);
        });

        RequestQueue queue = Volley.newRequestQueue(Dashboard.this);
        queue.add(jsonObjectRequest);

        }
    }

    private boolean todayDate(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
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
                    try {
                        Loader.hideLoader(this);
                        String balance = response.getString("balance");
                        bal.setText( "\u20A6"+ balance);
                    } catch (JSONException e) {
                        Log.e("Balance", "Error parsing response", e);
                    }
                }, error -> {
            Loader.hideLoader(this);
            Log.e("Balance", "Error occurred: ", error);
            Toast.makeText(this, "Failed to Load Balance Data", Toast.LENGTH_SHORT).show();
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }


}



package com.example.ticketingsystem;

import android.content.Context;
import android.content.Intent;

public class Switch {
    public static void goToBooking(Context context) {
        Intent intent = new Intent(context,Dashboard.class);
        context.startActivity(intent);
    }

    public static void goToWallet(Context context) {
        Intent intent = new Intent(context, Wallet.class);
        context.startActivity(intent);
    }

    public static void goToHistory(Context context) {
        Intent intent = new Intent(context, History.class);
        context.startActivity(intent);
    }

    public static void goToSettings(Context context) {
        Intent intent = new Intent(context, Settings.class);
        context.startActivity(intent);
    }

    public static void goToLogin(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void goToSignUp(Context context){
        Intent intent = new Intent(context, signup.class);
        context.startActivity(intent);
    }
}

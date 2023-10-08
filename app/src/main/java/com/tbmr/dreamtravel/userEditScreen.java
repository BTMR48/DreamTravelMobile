package com.tbmr.dreamtravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class userEditScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_screen);
    }

    public void homePage(View view) {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }



    private void saveTravelerRegistered() {
        SharedPreferences sharedPreferences = getSharedPreferences("travelerRegistered", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("travelerRegistered", "yes");
        editor.apply();
    }
    public static String getTravelerRegistered(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefsNic", MODE_PRIVATE);
        return sharedPreferences.getString("travelerRegistered", null);
    }
}
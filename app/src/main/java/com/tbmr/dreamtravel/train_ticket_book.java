package com.tbmr.dreamtravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class train_ticket_book extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_ticket_book);
    }

    public void homePage(View view) {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }
}
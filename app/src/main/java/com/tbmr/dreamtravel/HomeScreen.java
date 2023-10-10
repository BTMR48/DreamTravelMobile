package com.tbmr.dreamtravel;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Get the token using the getToken method
        String token = LoginScreen.getToken(getApplicationContext());

        // Get the reference to the TextView and set the token as its text
        TextView tokenTextView = findViewById(R.id.token);
        tokenTextView.setText(token);

        // Get the token using the getToken method
        String nic = LoginScreen.getNic(getApplicationContext());

        // Get the reference to the TextView and set the token as its text
        TextView nicTextView = findViewById(R.id.nic);
        nicTextView.setText(nic);
    }

    public void userEditScreen(View view) {
        Intent intent = new Intent(this, userEditScreen.class);
        startActivity(intent);
    }
    public void useBookHistory(View view) {
        Intent intent = new Intent(this, Ticket_Booked_list.class);
        startActivity(intent);
    }
    public void trainTicketBooking(View view) {
        Intent intent = new Intent(this, train_ticket_book.class);
        startActivity(intent);
    }
}

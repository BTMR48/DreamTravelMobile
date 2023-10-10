package com.tbmr.dreamtravel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class train_ticket_book extends AppCompatActivity {

    EditText seatCountEditText;
    Button userEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_ticket_book);

        seatCountEditText = findViewById(R.id.seatCount);
        userEditButton = findViewById(R.id.userEditbtn);

        userEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seatCountStr = seatCountEditText.getText().toString();
                if (seatCountStr.isEmpty()) {
                    Toast.makeText(train_ticket_book.this, "Please enter seat count", Toast.LENGTH_SHORT).show();
                    return;
                }

                int seatCount = Integer.parseInt(seatCountStr);
                if (seatCount < 1 || seatCount > 4) {
                    Toast.makeText(train_ticket_book.this, "Sorry, Cannot book", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call API here
                bookSeats(seatCount);
            }
        });
    }

    public void homePage(View view) {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    private void bookSeats(int seatCount) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Your SSL and API setup code here...

                    String apiUrl = "https://10.0.0.1:62214/api/bookings/";

                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("id", "");
                    jsonRequest.put("bookingID", String.valueOf(System.currentTimeMillis()));
                    jsonRequest.put("nic", LoginScreen.getNic(getApplicationContext()));
                    jsonRequest.put("trainID", "Tr001");
                    jsonRequest.put("reservationDate", "2023-10-10T06:35:01.707Z");
                    jsonRequest.put("bookingDate", "2023-10-10T06:35:01.707Z");
                    jsonRequest.put("status", 0);
                    jsonRequest.put("referenceID", "string");
//                    jsonRequest.put("seatCount", "4");
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String token = LoginScreen.getToken(getApplicationContext());

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonRequest.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Handle success
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(train_ticket_book.this, "Booking successful", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // Handle failure
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(train_ticket_book.this, "Booking failed", Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(train_ticket_book.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }
}

package com.tbmr.dreamtravel;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class train_ticket_book extends AppCompatActivity {
    private String trainId;
    EditText seatCountEditText,bookDate;
    Button book,bookTicket;
    TextView departureTimeTextView, arrivalTimeTextView, startStationTextView, stoppingStationTextView, trainIdTextView;
//    private TextView responseTV;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_ticket_book);
        bookDate = findViewById(R.id.bookDate);

        seatCountEditText = findViewById(R.id.seatCount);
        book = findViewById(R.id.bookTicket);
        // Initialize your UI elements
        departureTimeTextView = findViewById(R.id.departureTime);
        arrivalTimeTextView = findViewById(R.id.arrivalTime);
        startStationTextView = findViewById(R.id.startStation);
        stoppingStationTextView = findViewById(R.id.stoppingStation);
        trainIdTextView = findViewById(R.id.trainId);
//        responseTV = findViewById(R.id.idTVResponse); // Make sure you have a TextView with this ID in your layout
        loadingPB = findViewById(R.id.idLoadingPB); // Make sure you have a ProgressBar with this ID in your layout
        // Get the schedule ID passed from the previous activity
        // Initialize calendar and get the current date
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Calculate the minimum date (yesterday)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH,+30);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH,+ 1);
        Intent intent = getIntent();

        String scheduleId = intent.getStringExtra("scheduleId");
        System.out.println("scheduleId: train_ticket_book " + scheduleId);

        book.setOnClickListener(v -> {
            String bookDateValue = bookDate.getText().toString();
            String seatCountValue = seatCountEditText.getText().toString();

            // Check if both fields are empty
            if (bookDateValue.isEmpty() && seatCountValue.isEmpty()) {
                Toast.makeText(train_ticket_book.this, "Please fill the seat count and date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if date is empty
            if (bookDateValue.isEmpty()) {
                Toast.makeText(train_ticket_book.this, "Please add date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if seat count is empty
            if (seatCountValue.isEmpty()) {
                Toast.makeText(train_ticket_book.this, "Please add seat count", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse the seat count value and validate it
            int seatCount = Integer.parseInt(seatCountValue);
            if (seatCount < 1 || seatCount > 4) {
                Toast.makeText(train_ticket_book.this, "Invalid seat count", Toast.LENGTH_SHORT).show();
                return;
            }

            // If all validations pass, proceed to book seats
            bookSeats(scheduleId);
        });


        bookDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(train_ticket_book.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = year +  "-" + (month + 1) + "-"+ dayOfMonth   ;
                                bookDate.setText(selectedDate);
                            }
                        }, year, month, day);

                // Set the minimum and maximum dates
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                datePickerDialog.show();
            }
        });
        // Make the API call
        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    System.out.println("scheduleId: " + scheduleId);
                    URL url = new URL("https://10.0.2.2:62214/api/schedules/" + scheduleId);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        String responseMessage = sb.toString();

                        // Parse JSON and update UI
                        JSONObject jsonResponse = new JSONObject(responseMessage);
                        String departureTime = jsonResponse.getString("departureTime");
                        String arrivalTime = jsonResponse.getString("arrivalTime");
                        String startStation = jsonResponse.getString("startStation");
                        String stoppingStation = jsonResponse.getString("stoppingStation");
                        String trainId = jsonResponse.getJSONObject("train").getString("id");

//                        this.trainId = trainId; // Update the class-level variable
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                departureTimeTextView.setText("Departure Time : " + departureTime);
                                arrivalTimeTextView.setText("Arrival Time : " + arrivalTime);
                                startStationTextView.setText("Start Station : " + startStation);
                                stoppingStationTextView.setText("Stopping Station : " + stoppingStation);
                                trainIdTextView.setText("Train Id: " + trainId);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public void homePage(View view) {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    private void bookSeats( String scheduleId) {
        System.out.println("bookSeats METHOD: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String trainIdPass = trainIdTextView.getText().toString();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = sdf.format(new Date());
                    // Your SSL and API setup code here...
                    String originalDateStr = bookDate.getText().toString(); // Make sure this matches the actual format
                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd"); // Make sure this is the format in signUpDateOfBirth
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date;
                    date = originalFormat.parse(originalDateStr);
                    String formattedDateStr = targetFormat.format(date);  // This should now be like "2023-10-05"
                    String apiUrl = "https://10.0.2.2:62214/api/bookings";

                    System.out.println("bookingID: " + System.currentTimeMillis());
                    System.out.println("nic: " + LoginScreen.getNic(getApplicationContext()));
                    System.out.println("trainID: " + trainIdPass);//error message
                    System.out.println("reservationDate: " + formattedDateStr);
                    System.out.println("bookingDate: " + bookDate.getText().toString() );
                    System.out.println("today date: " + currentDate );

                    int seatCountInt = Integer.parseInt(seatCountEditText.getText().toString());
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("id", "");
                    jsonRequest.put("bookingID", "B-"+String.valueOf(System.currentTimeMillis()));
                    jsonRequest.put("scheduleID", scheduleId);

                    jsonRequest.put("seatCount", seatCountInt);
                    jsonRequest.put("nic", LoginScreen.getNic(getApplicationContext()));
                    jsonRequest.put("trainID", trainIdPass);

                    jsonRequest.put("reservationDate", formattedDateStr );
                    jsonRequest.put("bookingDate", currentDate);
                    jsonRequest.put("status", 0);
                    jsonRequest.put("referenceID", "RF-"+String.valueOf(System.currentTimeMillis()));

                    System.out.println("JSON reservationDate: " + jsonRequest.getString("reservationDate"));
                    System.out.println("JSON bookingDate: " + jsonRequest.getString("bookingDate"));


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
                            Intent intent;


                            // Handle other cases or defaults here if necessary
                            intent = new Intent(train_ticket_book.this, HomeScreen.class);  // default action; change as needed


                            startActivity(intent);
                        });
                    } else {
                        // Handle failure
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(train_ticket_book.this, "Booking failed", Toast.LENGTH_SHORT).show();
//                            responseTV.setText("Booked Failed");
                            System.out.println("------------------------------------------");
                            System.out.println("Booked Failed");
                            System.out.println("bookingID: " + String.valueOf(System.currentTimeMillis()));
                            System.out.println("nic: " + LoginScreen.getNic(getApplicationContext()));
                            System.out.println("trainID: " + trainIdPass);//error message
                            System.out.println("reservationDate: " + formattedDateStr);
                            System.out.println("bookingDate: " + currentDate );
                            System.out.println("scheduleId: " + scheduleId );
                            System.out.println("------------------------------------------");
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(train_ticket_book.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        responseTV.setText("Network error in  API call: " + e.getMessage());
                        loadingPB.setVisibility(View.GONE);
                    });
                }
            }
        }).start();
    }
}

package com.tbmr.dreamtravel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class userEditScreen extends AppCompatActivity {
    private EditText  userEditEmail,userEditNic, userEditName, userEditBirthday, userEditPass;
    private TextView idTVResponse;
    private Button userEditbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_screen);
        userEditNic = findViewById(R.id.userEditNic);
        userEditEmail = findViewById(R.id.userEditEmail);
        userEditName = findViewById(R.id.userEditName);
        userEditBirthday = findViewById(R.id.userEditBirthday);
        userEditbtn = findViewById(R.id.userEditbtn);
        idTVResponse = findViewById(R.id.idTVResponse);
        // Fetch user data
        fetchUserData();
        userEditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });


    }

    public void homePage(View view) {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }

    private void fetchUserData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get token and NIC from SharedPreferences
                    String token = LoginScreen.getToken(getApplicationContext());
                    String nic = LoginScreen.getNic(getApplicationContext());

                    // API URL for fetching user data
                    String apiUrl = "https://10.0.2.2:62214/api/travelers/" + nic;

                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

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
                        final String name = jsonResponse.getString("name");
                        final String email = jsonResponse.getString("email");
                        final String nicApi = jsonResponse.getString("nic");
//

                        String displayDate = "N/A";
                        if (jsonResponse.has("dateOfBirth") && !jsonResponse.isNull("dateOfBirth")) {
                            String dateOfBirth = jsonResponse.getString("dateOfBirth");
                            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = isoFormat.parse(dateOfBirth);
                            displayDate = displayFormat.format(date);

                            // Update the calendar instance with the parsed date
                            Calendar apiDateCalendar = Calendar.getInstance();
                            apiDateCalendar.setTime(date);
                            final int apiYear = apiDateCalendar.get(Calendar.YEAR);
                            final int apiMonth = apiDateCalendar.get(Calendar.MONTH);
                            final int apiDay = apiDateCalendar.get(Calendar.DAY_OF_MONTH);
                            Calendar maxDate = Calendar.getInstance();
                            maxDate.add(Calendar.YEAR, -10);
                            Calendar minDate = Calendar.getInstance();
                            minDate.add(Calendar.YEAR, -100);
                            userEditBirthday.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("Date of Birth: " + dateOfBirth);
                                    DatePickerDialog datePickerDialog = new DatePickerDialog(userEditScreen.this,
                                            new DatePickerDialog.OnDateSetListener() {
                                                @Override
                                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                                                    userEditBirthday.setText(selectedDate);
                                                }
                                            }, apiYear, apiMonth, apiDay);  // Set the initial date to the API date

                                    // Set the minimum and maximum dates
                                    datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                                    datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                                    datePickerDialog.show();
                                }
                            });
                        }

                        final String finalDisplayDate = displayDate;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                userEditNic.setText(nicApi);
                                userEditName.setText(name);
                                userEditEmail.setText(email);
                                userEditBirthday.setText(finalDisplayDate);
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                idTVResponse.setText("Failed to fetch data! Response code: " + responseCode);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            idTVResponse.setText("Network error: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void updateUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get token and NIC from SharedPreferences
                    String token = LoginScreen.getToken(getApplicationContext());
                    String nic = LoginScreen.getNic(getApplicationContext());

                    // API URL for updating user data
                    String apiUrl = "https://10.0.2.2:62214/api/travelers/" + nic;

                    // Create a JSON request
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("id", "");
                    jsonRequest.put("nic", userEditNic.getText().toString());
                    jsonRequest.put("name", userEditName.getText().toString());
                    jsonRequest.put("email", userEditEmail.getText().toString());
                    // Convert date to the format expected by the API
                    String uiDate = userEditBirthday.getText().toString();
                    SimpleDateFormat uiFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    Date date = uiFormat.parse(uiDate);
                    String apiDate = apiFormat.format(date);

                    jsonRequest.put("dateOfBirth", apiDate);

                    // Debugging: Print out the JSON payload
                    Log.d("Debug", "JSON Payload: " + jsonRequest.toString());


                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    byte[] outputBytes = jsonRequest.toString().getBytes("UTF-8");
                    connection.getOutputStream().write(outputBytes);

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // Navigate to HomeScreen
                                Intent intent = new Intent(userEditScreen.this, HomeScreen.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                idTVResponse.setText("Failed to update! Response code: " + responseCode);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            idTVResponse.setText("Network error: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }


}
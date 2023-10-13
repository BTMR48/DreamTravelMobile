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
//    private TextView idTVResponse;
    private Button userEditbtn,userDeactivateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_screen);
        userEditNic = findViewById(R.id.userEditNic);
        userEditEmail = findViewById(R.id.userEditEmail);
        userEditName = findViewById(R.id.userEditName);
        userEditBirthday = findViewById(R.id.userEditBirthday);
        userEditbtn = findViewById(R.id.userEditbtn);
//        idTVResponse = findViewById(R.id.idTVResponse);
         userDeactivateBtn = findViewById(R.id.userDeactivateBtn);

        // Fetch user data
        fetchUserData();
        userEditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUser();

            }
        });
        userDeactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deactivateUser();

            }
        });


    }

    public void homePage(View view) {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }

    private void fetchUserData() {
        // Create a new thread to perform network operation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Retrieve token and NIC (National Identification Code) from SharedPreferences
                    String token = LoginScreen.getToken(getApplicationContext());
                    String nic = LoginScreen.getNic(getApplicationContext());

                    // Define the API URL to fetch user data
                    String apiUrl = "https://10.0.2.2:62214/api/travelers/" + nic;

                    // Initialize the HTTP connection
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    // Get the HTTP response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Stream to read data from the connection
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        // Read data from stream
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }

                        // Parse the received JSON data
                        String responseMessage = sb.toString();
                        JSONObject jsonResponse = new JSONObject(responseMessage);

                        // Extract user data from JSON
                        final String name = jsonResponse.getString("name");
                        final String email = jsonResponse.getString("email");
                        final String nicApi = jsonResponse.getString("nic");

                        // Additional logic to handle Date of Birth
                        // (code omitted for brevity)

                        // Update UI on the main thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                userEditNic.setText(nicApi);
                                userEditName.setText(name);
                                userEditEmail.setText(email);
                                // Other UI updates
                            }
                        });
                    } else {
                        // Handle failure scenarios
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI to indicate failure
                            }
                        });
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI to indicate a network error
                        }
                    });
                }
            }
        }).start();
    }


    private void updateUser() {
        // Create a new thread to run the network operation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Retrieve token and NIC (National Identification Code) from SharedPreferences
                    String token = LoginScreen.getToken(getApplicationContext());
                    String nic = LoginScreen.getNic(getApplicationContext());

                    // Define API URL for updating user data
                    String apiUrl = "https://10.0.2.2:62214/api/travelers/" + nic;

                    // Create JSON object to hold request data
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("id", "");
                    jsonRequest.put("nic", userEditNic.getText().toString());
                    jsonRequest.put("name", userEditName.getText().toString());
                    jsonRequest.put("email", userEditEmail.getText().toString());

                    // Convert the user-entered date to the API expected format
                    String uiDate = userEditBirthday.getText().toString();
                    SimpleDateFormat uiFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    Date date = uiFormat.parse(uiDate);
                    String apiDate = apiFormat.format(date);

                    jsonRequest.put("dateOfBirth", apiDate);

                    // Debugging: Log the JSON payload
                    Log.d("Debug", "JSON Payload: " + jsonRequest.toString());

                    // Initialize the HTTP connection
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true); // Enable output stream for PUT request

                    // Send the JSON payload to the server
                    byte[] outputBytes = jsonRequest.toString().getBytes("UTF-8");
                    connection.getOutputStream().write(outputBytes);

                    // Get HTTP response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Successful update, navigate to HomeScreen
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(userEditScreen.this, HomeScreen.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        // Handle update failure
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // Handle UI updates for failure here
                            }
                        });
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // Handle UI updates for network errors here
                        }
                    });
                }
            }
        }).start();
    }


    private void deactivateUser() {
        // Create a new thread to run the network operation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Retrieve token and NIC (National Identification Code) from SharedPreferences
                    String token = LoginScreen.getToken(getApplicationContext());
                    String nic = LoginScreen.getNic(getApplicationContext());

                    // Define the API URL for deactivating the user
                    String apiUrl = "https://10.0.2.2:62214/api/users/" + nic + "/deactivate";

                    // Initialize the HTTP connection
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PATCH"); // Use PATCH as the HTTP method
                    connection.setRequestProperty("Authorization", "Bearer " + token); // Set the authorization header

                    // Get the HTTP response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // If successful, navigate to the LoginScreen
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(userEditScreen.this, LoginScreen.class);
                                startActivity(intent);
                                finish(); // Close current activity
                            }
                        });
                    } else {
                        // Handle the failure case
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // Handle UI updates for failure here
                            }
                        });
                    }
                } catch (Exception e) {
                    // Handle exceptions (e.g., network errors)
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // Handle UI updates for network errors here
                        }
                    });
                }
            }
        }).start();
    }


}
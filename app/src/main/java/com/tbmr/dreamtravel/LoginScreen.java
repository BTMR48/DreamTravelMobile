package com.tbmr.dreamtravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
// LoginScreen Activity class definition

public class LoginScreen extends AppCompatActivity {
    // Declare UI elements

    private EditText mNic, mPass;
    private TextView mTextView;
    private Button signInBtn;
    private ProgressBar loadingPB;  // Add this ProgressBar to your layout as well
//    private TextView responseTV;    // Add this TextView to your layout as well

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        // Initialize UI elements

        mNic = findViewById(R.id.loginNIC);
        mPass = findViewById(R.id.loginPass);
        signInBtn = findViewById(R.id.LoiginBtn);
        mTextView = findViewById(R.id.loginpath2);
        loadingPB = findViewById(R.id.idLoadingPB); // Ensure this ID exists in your layout
//        responseTV = findViewById(R.id.idTVResponse); // Ensure this ID exists in your layout

        // Click listener for navigating to the SignUpScreen

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, SignUpScreen.class));
            }
        });
        // Click listener for the Sign-in button

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }
    // Method to handle user login

    private void loginUser() {

        loadingPB.setVisibility(View.VISIBLE);// Show loading ProgressBar


        // Create a new Thread for the network operation
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Bypass SSL for development purposes ONLY
                    TrustManager[] trustAllCertificates = new TrustManager[]{
                            new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    HostnameVerifier allHostsValid = new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    };
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                    // API URL for login
                    String apiUrl = "https://10.0.2.2:62214/api/users/login";

                    // Create a JSON request
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("nic", mNic.getText().toString());
                    jsonRequest.put("password", mPass.getText().toString());

                    // Send the POST request
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    byte[] outputBytes = jsonRequest.toString().getBytes("UTF-8");
                    connection.getOutputStream().write(outputBytes);

                    int responseCode = connection.getResponseCode();
                    String responseMessage = "";

                    // Handle the response on the UI thread
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        responseMessage = sb.toString();

                        // Here, you can extract the token and do further processing
                        JSONObject jsonResponse = new JSONObject(responseMessage);
                        String token = jsonResponse.getString("token");

                        // Save token to SharedPreferences
                        saveToken(token);
                        saveNic(mNic.getText().toString());

                        // Navigate to HomeScreen on the UI thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            // Get the token using the travel register method

                            @Override
                            public void run() {
                                loadingPB.setVisibility(View.GONE);
                                try {
                                    // Bypass SSL for development purposes ONLY
                                    TrustManager[] trustAllCertificates = new TrustManager[]{
                                            new X509TrustManager() {
                                                public X509Certificate[] getAcceptedIssuers() {
                                                    return null;
                                                }

                                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                                }

                                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                                }
                                            }
                                    };
                                    SSLContext sc = SSLContext.getInstance("TLS");
                                    sc.init(null, trustAllCertificates, new java.security.SecureRandom());
                                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                                    HostnameVerifier allHostsValid = new HostnameVerifier() {
                                        public boolean verify(String hostname, SSLSession session) {
                                            return true;
                                        }
                                    };
                                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                                    // API URL for login
                                    String apiUrl = "https://10.0.2.2:62214/api/travelers/register";

                                    // Create a JSON request
                                    JSONObject jsonRequest = new JSONObject();
                                    jsonRequest.put("nic", mNic.getText().toString());
                                    jsonRequest.put("password", mPass.getText().toString());

                                    // Send the POST request
                                    URL url = new URL(apiUrl);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setRequestMethod("POST");
                                    connection.setRequestProperty("Content-Type", "application/json");
                                    connection.setDoOutput(true);

                                    byte[] outputBytes = jsonRequest.toString().getBytes("UTF-8");
                                    connection.getOutputStream().write(outputBytes);

                                    int responseCode = connection.getResponseCode();
                                    String responseMessage = "";

                                    // Handle the response on the UI thread
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = connection.getInputStream();
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                        StringBuilder sb = new StringBuilder();
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            sb.append(line);
                                        }
                                        responseMessage = sb.toString();

                                        // Here, you can extract the token and do further processing
                                        JSONObject jsonResponse = new JSONObject(responseMessage);
                                        String token = jsonResponse.getString("token");

                                        // Save token to SharedPreferences
                                        saveToken(token);
                                        saveNic(mNic.getText().toString());

                                        // Navigate to HomeScreen on the UI thread
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            // Get the token using the travel register method

                                            @Override
                                            public void run() {
                                                loadingPB.setVisibility(View.GONE);
                                                Intent intent;


                                                // Handle other cases or defaults here if necessary
                                                intent = new Intent(LoginScreen.this, HomeScreen.class);  // default action; change as needed


                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        // Handle unsuccessful login attempts or other server responses
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingPB.setVisibility(View.GONE);
//                                                responseTV.setText("Login failed! Response code: " + responseCode);
                                            }
                                        });
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingPB.setVisibility(View.GONE);
//                                            responseTV.setText("Network error: " + e.getMessage());
                                        }
                                    });
                                }
                                Intent intent;


                                intent = new Intent(LoginScreen.this, HomeScreen.class);


                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        // Handle unsuccessful login attempts or other server responses
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                loadingPB.setVisibility(View.GONE);
//                                responseTV.setText("Login failed! Response code: " + responseCode);
                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loadingPB.setVisibility(View.GONE);
//                            responseTV.setText("Network error: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }
    // Method to save token into SharedPreference
    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
    // Method to retrieve token from SharedPreferences

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
    // Method to save NIC into SharedPreferences

    private void saveNic(String mNic) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefsNic", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NIC", mNic);
        editor.apply();
    }
    // Method to retrieve NIC from SharedPreferences

    public static String getNic(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefsNic", MODE_PRIVATE);
        return sharedPreferences.getString("NIC", null);
    }

}

package com.tbmr.dreamtravel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.tbmr.dreamtravel.models.User;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SignUpScreen extends AppCompatActivity {

    private EditText mEmail, mPass, signUpNic,signUpName,signUpDateOfBirth;
    private Button signUpBtn;
//    private TextView responseTV;
    private TextView  loginpath;
    private ProgressBar loadingPB;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://10.0.0.1:62214/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        mEmail = findViewById(R.id.signupEmail);
        mPass = findViewById(R.id.signupPass);
        signUpNic = findViewById(R.id.signupNic);
        signUpBtn = findViewById(R.id.signupbtn);
//        responseTV = findViewById(R.id.idTVResponse);
        loadingPB = findViewById(R.id.idLoadingPB); // Make sure you have a ProgressBar with this ID in your layout
        signUpName = findViewById(R.id.signupName);
        signUpDateOfBirth = findViewById(R.id.signupBirthday);
        loginpath = findViewById(R.id.loginpath);
        // Initialize calendar and get the current date
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Calculate the minimum date (yesterday)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -10);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);

        loginpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
            }
        });
        signUpDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpScreen.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = year +  "-" + (month + 1) + "-"+ dayOfMonth   ;
                                signUpDateOfBirth.setText(selectedDate);
                            }
                        }, year, month, day);

                // Set the minimum and maximum dates
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                datePickerDialog.show();
            }
        });

        signUpBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString();
            String password = mPass.getText().toString();
            String nic = signUpNic.getText().toString();

            if (!isValidEmail(email)) {
                Toast.makeText(SignUpScreen.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(SignUpScreen.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidNic(nic)) {
                Toast.makeText(SignUpScreen.this, "NIC must be a maximum of 12 characters and minimum 10", Toast.LENGTH_SHORT).show();
                return;
            }

            registerNewUser();
        });


    }

    private boolean isValidEmail(String email) {
        // Use Java's built-in email validation. This may not catch all edge cases.
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidNic(String nic) {
        return nic.length() >= 10 && nic.length() <= 12;
    }

    private void registerNewUser() {
        loadingPB.setVisibility(View.VISIBLE);

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
                    String apiUrl = "https://10.0.2.2:62214/api/users/register";

                    // Create a JSON request
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("email", mEmail.getText().toString());
                    jsonRequest.put("password", mPass.getText().toString());
                    jsonRequest.put("nic", signUpNic.getText().toString());
                    jsonRequest.put("id", "");
                    jsonRequest.put("role", 0);
                    // Send the POST request
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    byte[] outputBytes = jsonRequest.toString().getBytes("UTF-8");
                    connection.getOutputStream().write(outputBytes);

                    int responseCode = connection.getResponseCode();

                    // Handle the response on the UI thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // Start the second API call here
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String originalDateStr = signUpDateOfBirth.getText().toString(); // Make sure this matches the actual format
                                            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd"); // Make sure this is the format in signUpDateOfBirth
                                            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date;
                                            date = originalFormat.parse(originalDateStr);
                                            String formattedDateStr = targetFormat.format(date);  // This should now be like "2023-10-05"

                                            String secondApiUrl = "https://10.0.2.2:62214/api/travelers/register";
                                            JSONObject secondJsonRequest = new JSONObject();
                                            secondJsonRequest.put("id", "");
                                            secondJsonRequest.put("nic", signUpNic.getText().toString());
                                            secondJsonRequest.put("email", mEmail.getText().toString());
                                            secondJsonRequest.put("name", signUpName.getText().toString()); // Assuming you have a signUpName EditText
                                            secondJsonRequest.put("dateOfBirth", formattedDateStr);


                                            URL secondUrl = new URL(secondApiUrl);
                                            HttpURLConnection secondConnection = (HttpURLConnection) secondUrl.openConnection();
                                            secondConnection.setRequestMethod("POST");
                                            secondConnection.setRequestProperty("Content-Type", "application/json");
                                            secondConnection.setDoOutput(true);

                                            byte[] secondOutputBytes = secondJsonRequest.toString().getBytes("UTF-8");
                                            secondConnection.getOutputStream().write(secondOutputBytes);

                                            int secondResponseCode = secondConnection.getResponseCode();

                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (secondResponseCode == HttpURLConnection.HTTP_OK) {
                                                //      responseTV.setText("Both registrations were successful!");
                                                        System.out.println("Date of Birth: " + formattedDateStr);
                                                        Intent intent;


                                                        // Handle other cases or defaults here if necessary
                                                        intent = new Intent(SignUpScreen.this, LoginScreen.class);  // default action; change as needed


                                                        startActivity(intent);

                                                    } else {
//                                                        responseTV.setText("Second registration failed! Response code: " + secondResponseCode);

                                                        System.out.println("Date of Birth: " + originalDateStr);

                                                    }
                                                    loadingPB.setVisibility(View.GONE);
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    responseTV.setText("Network error in second API call: " + e.getMessage());
                                                    loadingPB.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            } else {
//                                responseTV.setText("First registration failed! Response code: " + responseCode);
                                loadingPB.setVisibility(View.GONE);
                            }
                        }
                    });


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



}


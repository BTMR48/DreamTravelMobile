package com.tbmr.dreamtravel;

import android.content.Intent;
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
import com.tbmr.dreamtravel.models.User;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
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

    private EditText mEmail, mPass, signUpNic;
    private Button signUpBtn;
    private TextView responseTV;
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
        responseTV = findViewById(R.id.idTVResponse); // Make sure you have a TextView with this ID in your layout
        loadingPB = findViewById(R.id.idLoadingPB); // Make sure you have a ProgressBar with this ID in your layout

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().toString().isEmpty() || mPass.getText().toString().isEmpty() || signUpNic.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpScreen.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                registerNewUser();
            }
        });
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
                            loadingPB.setVisibility(View.GONE);
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                responseTV.setText("Registered Successfully!");
                            } else {
                                responseTV.setText("Registration failed! Response code: " + responseCode);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loadingPB.setVisibility(View.GONE);
                            responseTV.setText("Network error: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }



}


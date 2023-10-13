package com.tbmr.dreamtravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<Schedule> schedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        // Get the token using the getToken method
        String token = LoginScreen.getToken(getApplicationContext());

        // Get the reference to the TextView and set the token as its text
//        TextView tokenTextView = findViewById(R.id.token);
//        tokenTextView.setText(token);

        // Get the token using the getToken method
        String nic = LoginScreen.getNic(getApplicationContext());

        // Get the reference to the TextView and set the token as its text
//        TextView nicTextView = findViewById(R.id.nic);
//        nicTextView.setText(nic);
        recyclerView = findViewById(R.id.shedule_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        schedules = new ArrayList<>();
        adapter = new ScheduleAdapter(schedules);  // Initialize the adapter here
        recyclerView.setAdapter(adapter);  // Set the adapter to the RecyclerView
        loadSchedules();

    }

    private void loadSchedules() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Bypass SSL verification (Not recommended for production)
                    TrustManager[] trustAllCertificates = new TrustManager[]{
                            new X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                                // Initialize and configure HTTP connection

                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                }
                                // Handle and parse the server response

                                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                }
                            }
                    };

                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    URL url = new URL("https://10.0.2.2:62214/api/schedules");
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
                        JSONArray jsonResponse = new JSONArray(responseMessage);
                        schedules.clear();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject scheduleObject = jsonResponse.getJSONObject(i);
                            int scheduleId = Integer.parseInt(scheduleObject.getString("id"));
                            String departureTime = scheduleObject.getString("departureTime");
                            String arrivalTime = scheduleObject.getString("arrivalTime");
                            String startStation = scheduleObject.getString("startStation");
                            String stoppingStation = scheduleObject.getString("stoppingStation");
                            String trainName = scheduleObject.getJSONObject("train").getString("name");
                            schedules.add(new Schedule(scheduleId,departureTime, arrivalTime, startStation, stoppingStation, trainName));
                            System.out.println("scheduleId: homescreen JSONObject" + scheduleId);

                        }

                        // Update RecyclerView on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Navigation methods to different screens


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
    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

        private final List<Schedule> schedules;

        ScheduleAdapter(List<Schedule> schedules) {
            this.schedules = schedules;
        }


        public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list, parent, false);
            return new ScheduleViewHolder(view);
        }
        // Populate RecyclerView with data

        @Override
        public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
            Schedule schedule = schedules.get(position);
            holder.departureTime.setText("Departure Time : " +schedule.getDepartureTime());
            holder.arrivalTime.setText("Arrival Time : " +schedule.getArrivalTime());
            holder.startStation.setText("Start Station : " +schedule.getStartStation());
            holder.stoppingStation.setText("Stopping Station : " +schedule.getStoppingStation());
            holder.trainName.setText("Train Name : "  +schedule.getTrainName());
        }

        @Override
        public int getItemCount() {
            return schedules.size();
        }

        // RecyclerView adapter class

        class ScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView departureTime, arrivalTime, startStation, stoppingStation, trainName;

            ScheduleViewHolder(@NonNull View itemView) {
                super(itemView);
                departureTime = itemView.findViewById(R.id.departureTime);
                arrivalTime = itemView.findViewById(R.id.arrivalTime);
                startStation = itemView.findViewById(R.id.startStation);
                stoppingStation = itemView.findViewById(R.id.stoppingStation);
                trainName = itemView.findViewById(R.id.trainName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {


                int position = getAdapterPosition();
                Schedule clickedSchedule = schedules.get(position);

                String id = String.valueOf(clickedSchedule.getId());  // Use the getter to get the id
                System.out.println("scheduleId: homescreen " + id);
                // Create an Intent to start OneSchedule activity
                Intent intent = new Intent(v.getContext(), train_ticket_book.class);
                intent.putExtra("scheduleId", id);  // Pass the id here
                v.getContext().startActivity(intent);
            }
        }
    }

}

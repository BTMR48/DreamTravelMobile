package com.tbmr.dreamtravel;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Ticket_Booked_list extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookings;
    private TextView idTVResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booked_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        idTVResponse = findViewById(R.id.idTVResponse); // Replace 'idTVResponse' with the actual ID from your XML layout

        bookings = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookings);
        recyclerView.setAdapter(bookingAdapter);
        // Fetch token for API calls
        String nic = LoginScreen.getNic(getApplicationContext());
        // Fetch token for API calls
        String token = LoginScreen.getToken(getApplicationContext());

        // Load initial data
        loadBookings(token,nic);
    }

    private void loadBookings(final String token, final String nic) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Bypass SSL for development purposes ONLY
                    TrustManager[] trustAllCertificates = new TrustManager[]{
                            new X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    URL url = new URL("https://10.0.2.2:62214/api/bookings/nic/" + nic);
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
                        JSONArray jsonResponse = new JSONArray(responseMessage);
                        bookings.clear();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject bookingObject = jsonResponse.getJSONObject(i);
                            String referenceID = bookingObject.getString("referenceID");
                            String scheduleID = bookingObject.getString("scheduleID");
                            String bookingID = bookingObject.getString("bookingID");
                            String bookingDate = bookingObject.getString("bookingDate");
                            int seatCount = bookingObject.getInt("seatCount");
                            String trainID = bookingObject.getString("trainID");
                            String reservationDate = bookingObject.getString("reservationDate");
                            int status = bookingObject.getInt("status");
                            bookings.add(new Booking(referenceID,scheduleID,bookingDate,bookingID, seatCount, trainID, reservationDate, status));
                            System.out.println("scheduleId for  " +  scheduleID);
                            System.out.println("bookingDate for  " +  bookingDate);
                            System.out.println("bookingDate for  " +  bookingDate);
                        }

                        // Update RecyclerView on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bookingAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void cancelBooking( String bookingID,  String token, String nic) {
        System.out.println("cancelBooking    private void : " + bookingID);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Bypass SSL for development purposes ONLY
                    TrustManager[] trustAllCertificates = new TrustManager[]{
                            new X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    URL url = new URL("https://10.0.2.2:62214/api/bookings/" + bookingID + "/status");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PATCH");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestProperty("Content-Type", "application/json");

                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("status", 1);

                    OutputStream os = connection.getOutputStream();
                    os.write(jsonRequest.toString().getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("bookingID: " + bookingID);
                                // Refresh the list of bookings
                                loadBookings(token,nic);
                                finish();
                            }
                        });

                    }else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                idTVResponse.setText("Failed to deactivate! Response code: " + responseCode);
                            }
                        });
                    }

                } catch (Exception e) {
                    System.out.println("Exception: " + bookingID);
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


    private void updateBooking(final Booking booking, final String seatCount, final String token, final String nic) {
        System.out.println("updateBooking    private void : " + booking.bookingID);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Integer.parseInt(seatCount) < 1 || Integer.parseInt(seatCount) > 4) {
                    // Show an error message
                    return;
                }
                try {
                    // Bypass SSL for development purposes ONLY
                    TrustManager[] trustAllCertificates = new TrustManager[]{
                            new X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    URL url = new URL("https://10.0.2.2:62214/api/bookings/" + booking.bookingID);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestProperty("Content-Type", "application/json");
                    System.out.println("bookingID: " + booking.bookingID);
                    System.out.println("nic: " + nic);
                    System.out.println("trainID"+ booking.trainID);
                    System.out.println("referenceID"+ booking.getReferenceID());
                    System.out.println("scheduleId " +  booking.getBookingDate());
                    System.out.println("booking.bookingDate " +  booking.getScheduleID());
                    System.out.println("seatCount " +  seatCount);

                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("id", "");
                    jsonRequest.put("bookingID", booking.bookingID);
                    jsonRequest.put("nic", nic);
                    jsonRequest.put("trainID", booking.trainID);
                    jsonRequest.put("reservationDate", booking.reservationDate);
                    jsonRequest.put("bookingDate", booking.getScheduleID());
                    jsonRequest.put("status", booking.status);
                    jsonRequest.put("referenceID", booking.getReferenceID());
                    jsonRequest.put("scheduleId", booking.getBookingDate());

                       jsonRequest.put("seatCount", seatCount);

                    OutputStream os = connection.getOutputStream();
                    os.write(jsonRequest.toString().getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("bookingID: " + booking.bookingID);
                                // Refresh the list of bookings
                                loadBookings(token,nic);
                                finish();
                            }
                        });

                    }else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                idTVResponse.setText("Failed to deactivate! Response code: " + responseCode);
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

    public void homePage(View view) {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }
    private static class Booking {
        String bookingID;
        int seatCount;
        String trainID;
        String reservationDate;
        String bookingDate;
        String referenceID;
        String  scheduleID;
        int status;





        public Booking(String referenceID,String bookingDate , String scheduleID,String bookingID, int seatCount, String trainID, String reservationDate, int status) {
            this.bookingID = bookingID;
            this.seatCount = seatCount;
            this.trainID = trainID;
            this.reservationDate = reservationDate;
            this.status = status;
            this.scheduleID = scheduleID;
            this.referenceID = referenceID;
            this.bookingDate = bookingDate;
        }

        public String getBookingID() {
            return bookingID;
        }

        public int getSeatCount() {
            return seatCount;
        }

        public String getTrainID() {
            return trainID;
        }

        public String getReservationDate() {
            return reservationDate;
        }

        public int getStatus() {
            return status;
        }
        public String getScheduleID() {
            return scheduleID;
        }

        public String getReferenceID() {
            return referenceID;
        }
        public String getBookingDate() {
            return bookingDate;
        }

    }


    private class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

        private final List<Booking> bookings;

        BookingAdapter(List<Booking> bookings) {
            this.bookings = bookings;
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_card, parent, false);
            return new BookingViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
            Booking booking = bookings.get(position);
            try {
                LocalDate date = LocalDate.parse(booking.getScheduleID(), DateTimeFormatter.ISO_DATE_TIME);
                holder.bookingDateTextView.setText("Booking Date " + date);
            } catch (DateTimeParseException e) {
                holder.bookingDateTextView.setText("Booking Date " + booking.getScheduleID());
            }

            try {
                LocalDate reservationDate = LocalDate.parse(booking.getReservationDate(), DateTimeFormatter.ISO_DATE_TIME);
                holder.reservationDateTextView.setText("Reservation Date: " + reservationDate);
            } catch (DateTimeParseException e) {
                holder.reservationDateTextView.setText("Reservation Date: " + booking.getReservationDate());
            }
            holder.bookingIDTextView.setText("Booking  ID " + booking.getBookingID());
            holder.seatCountEditText.setText(String.valueOf(booking.getSeatCount()));
            holder.trainIDTextView.setText("Train ID: " + booking.getTrainID());

            if (booking.getScheduleID() != null) {
                holder.scheduleIdTextView.setText("scheduleID: " + booking.getBookingDate());
            } else {
                holder.scheduleIdTextView.setText("scheduleID: N/A");  // or set it to some default value or make it invisible
            }



            if (booking.getStatus() == 0) {
                holder.statusTextView.setText("Status: Active");
            } else {
                holder.statusTextView.setText("Status: Canceled");
            }

            // Set visibility of buttons based on status
            if (booking.getStatus() == 0) {
                holder.updateButton.setVisibility(View.VISIBLE);
                holder.cancelButton.setVisibility(View.VISIBLE);
            } else {
                holder.updateButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);
            }
        }



        @Override
        public int getItemCount() {
            return bookings.size();
        }

        class BookingViewHolder extends RecyclerView.ViewHolder {

            TextView bookingDateTextView,bookingIDTextView;
            EditText seatCountEditText;
            TextView trainIDTextView;
            TextView reservationDateTextView,scheduleIdTextView;
            TextView statusTextView;
            Button cancelButton;
            Button updateButton;

            BookingViewHolder(@NonNull View itemView) {
                super(itemView);
                bookingIDTextView = itemView.findViewById(R.id.bookingID);
                bookingDateTextView = itemView.findViewById(R.id.bookingDate);
                seatCountEditText = itemView.findViewById(R.id.seatCount);
                trainIDTextView = itemView.findViewById(R.id.trainID);
                reservationDateTextView = itemView.findViewById(R.id.reservationDate);
                statusTextView = itemView.findViewById(R.id.status);
                cancelButton = itemView.findViewById(R.id.cancelButton);
                updateButton = itemView.findViewById(R.id.updateButton);
                scheduleIdTextView = itemView.findViewById(R.id.scheduleId);  // Make sure the ID matches what's in your XML layout
            // Set OnClickListener for the update button
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the seat count from the EditText
                        String seatCount = seatCountEditText.getText().toString();

                        // Get the token and nic
                        String token = LoginScreen.getToken(itemView.getContext());
                        String nic = LoginScreen.getNic(itemView.getContext());

                        // Call the updateBooking method
                        Booking booking = bookings.get(getAdapterPosition());
                        updateBooking(booking, seatCount, token, nic);
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the seat count from the EditText
                        String seatCount = seatCountEditText.getText().toString();

                        // Get the token and nic
                        String token = LoginScreen.getToken(itemView.getContext());
                        String nic = LoginScreen.getNic(itemView.getContext());

                        // Call the updateBooking method
                        Booking booking = bookings.get(getAdapterPosition());
                        cancelBooking(booking.bookingID,token, nic);
                    }
                });
            }

            void bind(Booking booking) {
                bookingDateTextView.setText(booking.bookingDate);
                seatCountEditText.setText(String.valueOf(booking.seatCount));
                trainIDTextView.setText(booking.trainID);
                reservationDateTextView.setText(booking.reservationDate);
                statusTextView.setText(booking.status == 0 ? "Active" : "Canceled");

                String token = LoginScreen.getToken(getApplicationContext());
                String nic = LoginScreen.getNic(getApplicationContext());

                if (booking.status == 0) {
                    cancelButton.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.VISIBLE);
                    cancelButton.setOnClickListener(v -> cancelBooking(booking.bookingID, token,nic));
                    updateButton.setOnClickListener(v -> updateBooking(booking, seatCountEditText.getText().toString(), token, nic));
                } else {
                    cancelButton.setVisibility(View.GONE);
                    updateButton.setVisibility(View.GONE);
                }
            }
        }
    }



}

package com.tbmr.dreamtravel;
// schedule modele class
public class Schedule {
    public int id;
    public String departureTime;
    public String arrivalTime;
    public String startStation;
    public String stoppingStation;
    public String trainName;


    public Schedule(int id, String departureTime, String arrivalTime, String startStation, String stoppingStation, String trainName) {
        this.id = id;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.startStation = startStation;
        this.stoppingStation = stoppingStation;
        this.trainName = trainName;
    }


    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getStartStation() {
        return startStation;
    }

    public String getStoppingStation() {
        return stoppingStation;
    }

    public String getTrainName() {
        return trainName;
    }
    // Add a getter for id
    public int getId() {
        return id;
    }
}

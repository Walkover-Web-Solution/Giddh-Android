package com.Giddh.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by walkover on 24/3/15.
 */
public class TripInfo implements Serializable{


    String tripName;
    String owner;
    String tripId;
    ArrayList<TripInfo>savedTrips;

    public ArrayList<TripInfo> getSavedTrips() {
        return savedTrips;
    }

    public void setSavedTrips(ArrayList<TripInfo> savedTrips) {
        this.savedTrips = savedTrips;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}

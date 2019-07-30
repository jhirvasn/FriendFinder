package com.example.friendfinder.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Position {

    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;

    public Position(Integer locationId, Double lat, Double lon) {
        this.locationId = locationId;
        this.lat = lat;
        this.lon = lon;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Position{" +
                "locationId=" + locationId +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}

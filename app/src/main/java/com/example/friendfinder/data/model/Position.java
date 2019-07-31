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
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public Position(Integer locationId, Double lat, Double lon, Integer userId) {
        this.locationId = locationId;
        this.lat = lat;
        this.lon = lon;
        this.userId = userId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Position{" +
                "locationId=" + locationId +
                ", lat=" + lat +
                ", lon=" + lon +
                ", userId=" + userId +
                '}';
    }
}

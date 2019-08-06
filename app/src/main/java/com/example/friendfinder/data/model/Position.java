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
    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    public Position(Integer locationId, Double lat, Double lon, String deviceId) {
        this.locationId = locationId;
        this.lat = lat;
        this.lon = lon;
        this.deviceId = deviceId;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "Position{" +
                "locationId=" + locationId +
                ", lat=" + lat +
                ", lon=" + lon +
                ", deviceId=" + deviceId +
                '}';
    }
}

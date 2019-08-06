package com.example.friendfinder.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    @SerializedName("pairingNumber")
    @Expose
    private String pairingNumber;

    public User(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPairingNumber() {
        return pairingNumber;
    }

    public void setPairingNumber(String pairingNumber) {
        this.pairingNumber = pairingNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "deviceId='" + deviceId + '\'' +
                ", pairingNumber='" + pairingNumber + '\'' +
                '}';
    }
}

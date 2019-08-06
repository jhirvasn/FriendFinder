package com.example.friendfinder.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("friendId")
    @Expose
    private Integer friendId;

    @SerializedName("deviceId1")
    @Expose
    private String deviceId1;

    @SerializedName("deviceId2")
    @Expose
    private String deviceId2;

    public Friend(String deviceId1, String deviceId2) {
        this.deviceId1 = deviceId1;
        this.deviceId2 = deviceId2;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public String getDeviceId1() {
        return deviceId1;
    }

    public void setDeviceId1(String deviceId1) {
        this.deviceId1 = deviceId1;
    }

    public String getDeviceId2() {
        return deviceId2;
    }

    public void setDeviceId2(String deviceId2) {
        this.deviceId2 = deviceId2;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "friendId=" + friendId +
                ", deviceId1='" + deviceId1 + '\'' +
                ", deviceId2='" + deviceId2 + '\'' +
                '}';
    }
}

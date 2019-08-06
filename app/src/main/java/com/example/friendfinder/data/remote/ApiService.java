package com.example.friendfinder.data.remote;

import com.example.friendfinder.data.model.Friend;
import com.example.friendfinder.data.model.Position;
import com.example.friendfinder.data.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // get all locations
    @GET("locations")
    Single<List<Position>> getPositions();

    // get one location
    @GET("locations/{device_id}")
    Single<List<Position>> getPosition(@Path("device_id") String deviceId);

    // create one location
    @POST("locations")
    Single<Position> createPosition(@Body Position position);

    // update one location
    @PUT("locations/{device_id}")
    Single<Position> updatePosition(@Path("device_id") String deviceId,
                                    @Body Position position);
    // delete one location
    @DELETE("locations/{device_id}")
    Single<Position> deletePosition(@Path("device_id") String deviceId);

    // get all users
    @GET("users")
    Single<List<User>> getUsers();

    // get one user
    @GET("users/{device_id}")
    Single<List<User>> getUser(@Path("device_id") String deviceId);

    // get one user with pairingNumber
    @GET("users/pairingnumber/{pairing_id}")
    Single<List<User>> getUserWithPairingNumber(@Path("pairing_id") String pairingNumber);

    // create one user
    @POST("users")
    Single<User> createUser(@Body User user);

    //get all your friends
    @GET("friends/{device_id}")
    Single<List<User>> getYourFriends(@Path("device_id") String deviceId);

    // create friend pair
    @POST("friends")
    Single<Friend> createFriendPair(@Body Friend friend);
}

package com.example.friendfinder.data.remote;

import com.example.friendfinder.data.model.Position;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // get all
    @GET("locations")
    Single<List<Position>> getPositions();

    // get one
    @GET("locations/{location_id}")
    Single<List<Position>> getPosition(@Path("location_id") int locationId);

    // get user location
    /*@GET("locations/{user_id}")
    Single<List<Position>> getPosition(@Path("user_id") int userId);*/

    // create one
    @POST("locations")
    Single<Position> createPosition(@Body Position position);

    // update one
    @PUT("locations/{location_id}")
    Single<Position> updatePosition(@Path("location_id") int locationId,
                                    @Body Position position);
    // delete one
    @DELETE("locations/{location_id}")
    Single<Position> deletePosition(@Path("location_id") int locationId);
}

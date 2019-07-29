package com.example.friendfinder;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("")
    Single<LocationData> getLocationData();
}

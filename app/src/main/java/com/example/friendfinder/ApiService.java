package com.example.friendfinder;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("v2/5d3fe8ed330000cf239d279f")
    Single<List<Position>> getPositions();
}

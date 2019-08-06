package com.example.friendfinder.data.remote;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://85.23.33.207:3000/";

    public static ApiService getApiService() {

        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }
}
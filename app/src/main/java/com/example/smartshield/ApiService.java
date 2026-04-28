package com.example.smartshield;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("apps")
    Call<List<AppModel>> getAllowedApps();
}

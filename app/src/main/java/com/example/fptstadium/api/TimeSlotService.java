package com.example.fptstadium.api;

import com.example.fptstadium.data.model.response.GetTimeSlotResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TimeSlotService {
    // API để lấy time slot theo ID
    @GET("{id}")
    Call<GetTimeSlotResponse> getTimeSlotById(@Path("id") String id);
}


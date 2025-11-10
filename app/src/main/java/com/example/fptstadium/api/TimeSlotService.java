package com.example.fptstadium.api;

import com.example.fptstadium.data.model.response.GetTimeSlotResponse;
import com.example.fptstadium.data.model.response.TimeSlotBookingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TimeSlotService {
    // API để lấy time slot theo ID
    @GET("timeslots/{id}")
    Call<GetTimeSlotResponse> getTimeSlotById(@Path("id") String id);

    // API để lấy time slot theo field ID và date
    @GET("timeslots/fields/{fieldId}")
    Call<TimeSlotBookingResponse> getTimeSlots(
        @Path("fieldId") String fieldId,
        @Query("date") String date
    );

}

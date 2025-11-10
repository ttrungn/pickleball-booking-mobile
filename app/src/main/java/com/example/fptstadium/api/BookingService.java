package com.example.fptstadium.api;

import com.example.fptstadium.data.model.request.BookingRequest;
import com.example.fptstadium.data.model.request.LoginRequest;
import com.example.fptstadium.data.model.response.BookingResponse;
import com.example.fptstadium.data.model.response.GetBookingResponse;
import com.example.fptstadium.data.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingService {
    @POST("bookings")
    Call<BookingResponse> booking(@Body BookingRequest request);

    @GET("bookings/users")
    Call<GetBookingResponse> getUserBookings();

    @GET("bookings/users")
    Call<GetBookingResponse> getUserBookings(
            @Query("status") Integer status
    );

    @PUT("bookings/{id}/cancel")
    Call<BookingResponse> cancelBooking(@Path("id") String bookingId);

}

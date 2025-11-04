package com.example.fptstadium.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fptstadium.api.BookingService;
import com.example.fptstadium.data.model.request.BookingRequest;
import com.example.fptstadium.data.model.response.BookingResponse;
import com.example.fptstadium.data.model.response.GetBookingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class BookingRepository {
    private final BookingService bookingService;

    @Inject
    public BookingRepository(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public LiveData<BookingResponse> createBooking(BookingRequest request) {
        MutableLiveData<BookingResponse> result = new MutableLiveData<>();

        bookingService.booking(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    BookingResponse errorResponse = new BookingResponse();
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                BookingResponse errorResponse = new BookingResponse();
                result.setValue(errorResponse);
            }
        });

        return result;
    }

    public LiveData<GetBookingResponse> getUserBookings() {
        MutableLiveData<GetBookingResponse> result = new MutableLiveData<>();

        bookingService.getUserBookings().enqueue(new Callback<GetBookingResponse>() {
            @Override
            public void onResponse(Call<GetBookingResponse> call, Response<GetBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GetBookingResponse> call, Throwable t) {
                result.setValue(null);
            }
        });

        return result;
    }

    public LiveData<BookingResponse> cancelBooking(String bookingId) {
        MutableLiveData<BookingResponse> result = new MutableLiveData<>();

        bookingService.cancelBooking(bookingId).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                result.setValue(null);
            }
        });

        return result;
    }
}


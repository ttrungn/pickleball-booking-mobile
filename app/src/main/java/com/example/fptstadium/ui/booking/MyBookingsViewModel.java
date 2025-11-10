package com.example.fptstadium.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.api.BookingService;
import com.example.fptstadium.data.model.response.BookingResponse;
import com.example.fptstadium.data.model.response.GetBookingResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class MyBookingsViewModel extends ViewModel {

    private final BookingService bookingService;
    private final MutableLiveData<List<GetBookingResponse.BookingData>> bookingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelSuccessLiveData = new MutableLiveData<>();

    // Current filter status
    private Integer currentFilterStatus = null;

    @Inject
    public MyBookingsViewModel(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public LiveData<List<GetBookingResponse.BookingData>> getBookingsLiveData() {
        return bookingsLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getCancelSuccessLiveData() {
        return cancelSuccessLiveData;
    }

    public void loadUserBookings() {
        loadUserBookings(currentFilterStatus);
    }

    public void loadUserBookings(Integer status) {
        currentFilterStatus = status;
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<GetBookingResponse> call;
        if (status != null) {
            call = bookingService.getUserBookings(status);
        } else {
            call = bookingService.getUserBookings();
        }

        call.enqueue(new Callback<GetBookingResponse>() {
            @Override
            public void onResponse(Call<GetBookingResponse> call, Response<GetBookingResponse> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    GetBookingResponse bookingResponse = response.body();
                    if (bookingResponse.isSuccess() && bookingResponse.getData() != null) {
                        bookingsLiveData.setValue(bookingResponse.getData());
                    } else {
                        errorLiveData.setValue(bookingResponse.getMessage() != null ?
                            bookingResponse.getMessage() : "Không thể tải danh sách đặt sân");
                    }
                } else {
                    errorLiveData.setValue("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetBookingResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Không thể kết nối tới server: " + t.getMessage());
            }
        });
    }

    public void cancelBooking(String bookingId) {
        loadingLiveData.setValue(true);

        bookingService.cancelBooking(bookingId).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                loadingLiveData.setValue(false);
                // Accept both 200 OK and 204 No Content as success
                if (response.isSuccessful()) {
                    // 204 No Content doesn't have a body, so check for null
                    if (response.code() == 204 || response.body() == null || response.body().isSuccess()) {
                        cancelSuccessLiveData.setValue(true);
                        loadUserBookings();
                    } else {
                        errorLiveData.setValue(response.body().getMessage() != null ?
                            response.body().getMessage() : "Không thể hủy đặt sân");
                    }
                } else {
                    errorLiveData.setValue("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Không thể kết nối tới server: " + t.getMessage());
            }
        });
    }
}

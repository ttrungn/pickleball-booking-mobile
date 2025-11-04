package com.example.fptstadium.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.data.model.request.BookingRequest;
import com.example.fptstadium.data.model.response.BookingResponse;
import com.example.fptstadium.data.model.response.GetBookingResponse;
import com.example.fptstadium.data.repository.BookingRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BookingViewModel extends ViewModel {
    private final BookingRepository bookingRepository;

    @Inject
    public BookingViewModel(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public LiveData<BookingResponse> createBooking(BookingRequest request) {
        return bookingRepository.createBooking(request);
    }

    public LiveData<GetBookingResponse> getUserBookings() {
        return bookingRepository.getUserBookings();
    }

    public LiveData<BookingResponse> cancelBooking(String bookingId) {
        return bookingRepository.cancelBooking(bookingId);
    }
}


package com.example.fptstadium.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.data.model.response.MomoPaymentResponse;
import com.example.fptstadium.data.repository.PaymentRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PaymentViewModel extends ViewModel {

    private final PaymentRepository paymentRepository;

    @Inject
    public PaymentViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public LiveData<MomoPaymentResponse> getMomoPaymentUrl(String bookingId) {
        return paymentRepository.getMomoPaymentUrl(bookingId);
    }
}

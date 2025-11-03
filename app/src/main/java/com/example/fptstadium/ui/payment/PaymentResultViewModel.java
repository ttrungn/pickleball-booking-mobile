package com.example.fptstadium.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.data.model.request.MomoPaymentCallbackRequest;
import com.example.fptstadium.data.repository.PaymentRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PaymentResultViewModel extends ViewModel {

    private final PaymentRepository paymentRepository;

    @Inject
    public PaymentResultViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public LiveData<Boolean> submitMomoPayment(MomoPaymentCallbackRequest request) {
        return paymentRepository.submitMomoPayment(request);
    }
}

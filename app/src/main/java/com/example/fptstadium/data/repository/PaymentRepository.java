package com.example.fptstadium.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fptstadium.api.PaymentService;
import com.example.fptstadium.data.model.response.MomoPaymentResponse;
import com.example.fptstadium.data.model.request.MomoPaymentCallbackRequest;

import javax.inject.Inject;

public class PaymentRepository {
    private final PaymentService paymentService;

    @Inject
    public PaymentRepository(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public LiveData<MomoPaymentResponse> getMomoPaymentUrl(String bookingId) {
        MutableLiveData<MomoPaymentResponse> result = new MutableLiveData<>();
        paymentService.getMomoPaymentUrl(bookingId)
                .enqueue(new retrofit2.Callback<MomoPaymentResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<MomoPaymentResponse> call, retrofit2.Response<MomoPaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(response.body());
                        } else {
                            result.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MomoPaymentResponse> call, Throwable t) {
                        result.setValue(null);
                    }
                });
        return result;
    }

    public LiveData<Boolean> submitMomoPayment(MomoPaymentCallbackRequest request) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        paymentService.submitMomoPayment(request)
                .enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                        result.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        result.setValue(false);
                    }
                });
        return result;
    }
}

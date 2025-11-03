package com.example.fptstadium.api;

import com.example.fptstadium.data.model.response.MomoPaymentResponse;
import com.example.fptstadium.data.model.request.MomoPaymentCallbackRequest;

import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentService {

	// Example: GET payments/momo/url?bookingId={uuid}
	@GET("payments/momo/url")
	Call<MomoPaymentResponse> getMomoPaymentUrl(@Query("bookingId") String bookingId);

	// Submit momo payment callback payload to backend
	@POST("payments/momo")
	Call<ResponseBody> submitMomoPayment(@Body MomoPaymentCallbackRequest request);
}


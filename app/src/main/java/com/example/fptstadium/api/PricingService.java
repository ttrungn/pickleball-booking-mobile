package com.example.fptstadium.api;

import com.example.fptstadium.data.model.response.GetPricingsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PricingService {
    @GET("pricings/field/{fieldId}")
    Call<GetPricingsResponse> getPricingsByField(@Path("fieldId") String fieldId);
}


package com.example.fptstadium.api;

import com.example.fptstadium.data.model.response.GetPricingsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PricingService {
    // Preferred route (plural 'fields') returns plain list
    @GET("pricings/fields/{fieldId}")
    Call<GetPricingsResponse> getPricingsByField(@Path("fieldId") String fieldId);

    // Singular route attempt
    @GET("pricings/field/{fieldId}")
    Call<GetPricingsResponse> getPricingsByFieldSingular(@Path("fieldId") String fieldId);

    // Query fallback (pagination meta ignored)
    @GET("pricings")
    Call<GetPricingsResponse> getPricings(
            @Query("FieldId") String fieldId,
            @Query("IsActive") Boolean isActive,
            @Query("PageNumber") Integer pageNumber,
            @Query("PageSize") Integer pageSize
    );
}

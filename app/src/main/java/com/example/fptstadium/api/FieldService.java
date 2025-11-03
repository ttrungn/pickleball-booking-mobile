package com.example.fptstadium.api;

import com.example.fptstadium.data.model.response.GetFieldsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FieldService {
    @GET("fields")
    Call<GetFieldsResponse> getFields(
            @Query("Name") String name,
            @Query("MinPrice") Double minPrice,
            @Query("MaxPrice") Double maxPrice,
            @Query("IsActive") Boolean isActive,
            @Query("PageNumber") int pageNumber,
            @Query("PageSize") int pageSize
    );
}

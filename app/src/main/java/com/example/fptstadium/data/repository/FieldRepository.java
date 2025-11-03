package com.example.fptstadium.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fptstadium.api.FieldService;
import com.example.fptstadium.data.model.PaginationInfo;
import com.example.fptstadium.data.model.response.GetFieldsResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldRepository {

    private static final String TAG = "FieldRepository";
    private final FieldService fieldService;
    private final MutableLiveData<List<GetFieldsResponse.FieldItem>> fieldsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<PaginationInfo> paginationLiveData = new MutableLiveData<>();

    @Inject
    public FieldRepository(FieldService fieldService) {
        this.fieldService = fieldService;
        Log.d(TAG, "FieldRepository created");
    }

    public LiveData<List<GetFieldsResponse.FieldItem>> getFields(String name, Double minPrice, Double maxPrice, Boolean isActive, int pageNumber, int pageSize) {
        Log.d(TAG, "getFields called with params: name=" + name + ", isActive=" + isActive + ", page=" + pageNumber + ", size=" + pageSize);

        fieldService.getFields(name, minPrice, maxPrice, isActive, pageNumber, pageSize).enqueue(new Callback<GetFieldsResponse>() {
            @Override
            public void onResponse(Call<GetFieldsResponse> call, Response<GetFieldsResponse> response) {
                Log.d(TAG, "API Response received: code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    GetFieldsResponse body = response.body();
                    Log.d(TAG, "Response body: success=" + body.isSuccess() + ", message=" + body.getMessage());

                    if (body.isSuccess() && body.getData() != null) {
                        List<GetFieldsResponse.FieldItem> data = body.getData();
                        Log.d(TAG, "Data received: " + data.size() + " items");
                        fieldsLiveData.postValue(data);

                        // Extract pagination info
                        PaginationInfo paginationInfo = new PaginationInfo(
                            body.getPageNumber(),
                            body.getTotalPages(),
                            body.getTotalCount(),
                            body.isHasPreviousPage(),
                            body.isHasNextPage()
                        );
                        paginationLiveData.postValue(paginationInfo);
                        Log.d(TAG, "Pagination: page " + body.getPageNumber() + " of " + body.getTotalPages() + ", total items: " + body.getTotalCount());
                    } else {
                        String errorMsg = body.getMessage() != null ? body.getMessage() : "No data";
                        Log.e(TAG, "API returned error: " + errorMsg);
                        errorLiveData.postValue(errorMsg);
                    }
                } else {
                    String errorMsg = "HTTP Error: " + response.code();
                    Log.e(TAG, errorMsg);
                    errorLiveData.postValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<GetFieldsResponse> call, Throwable t) {
                String errorMsg = "Network Failure: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                errorLiveData.postValue(errorMsg);
            }
        });
        return fieldsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<PaginationInfo> getPaginationInfo() {
        return paginationLiveData;
    }
}


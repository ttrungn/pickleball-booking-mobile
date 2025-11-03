package com.example.fptstadium.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.data.model.PaginationInfo;
import com.example.fptstadium.data.model.response.GetFieldsResponse;
import com.example.fptstadium.data.repository.FieldRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final FieldRepository fieldRepository;
    private final LiveData<String> errorLiveData;
    private final LiveData<PaginationInfo> paginationLiveData;

    // Filter parameters
    private String searchQuery = null;
    private Double minPrice = null;
    private Double maxPrice = null;

    @Inject
    public HomeViewModel(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
        this.errorLiveData = fieldRepository.getError();
        this.paginationLiveData = fieldRepository.getPaginationInfo();
        Log.d(TAG, "HomeViewModel created");
    }

    public LiveData<List<GetFieldsResponse.FieldItem>> fetchFields(int pageNumber) {
        Log.d(TAG, "fetchFields() called for page " + pageNumber +
              ", search: " + searchQuery + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice);
        // Return the LiveData from repository with filters
        return fieldRepository.getFields(searchQuery, minPrice, maxPrice, true, pageNumber, 8);
    }

    public void setSearchQuery(String query) {
        this.searchQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        Log.d(TAG, "Search query set to: " + this.searchQuery);
    }

    public void setPriceFilter(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        Log.d(TAG, "Price filter set to: min=" + minPrice + ", max=" + maxPrice);
    }

    public void clearFilters() {
        this.searchQuery = null;
        this.minPrice = null;
        this.maxPrice = null;
        Log.d(TAG, "All filters cleared");
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<PaginationInfo> getPaginationLiveData() {
        return paginationLiveData;
    }
}

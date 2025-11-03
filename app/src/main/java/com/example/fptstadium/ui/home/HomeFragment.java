package com.example.fptstadium.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fptstadium.databinding.FragmentHomeBinding;
import com.example.fptstadium.ui.adapter.FieldAdapter;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private FieldAdapter adapter;
    private int currentPage = 1;
    private int totalPages = 1;

    // Search debounce handler
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 500; // 500ms delay

    // Filter state
    private boolean isFilterVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerView();
        setupPagination();
        setupSearchAndFilter(); // Setup search and filter
        observeCommonData(); // Setup error and pagination observers

        // Immediate data loading
        loadFields(currentPage);
        Log.d(TAG, "Initial loadFields() called in onCreateView");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);

        // Initialize adapter with empty list
        adapter = new FieldAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);

        // Set initial visibility
        binding.recyclerView.setVisibility(View.GONE);
        binding.loadingIndicator.setVisibility(View.GONE);

        Log.d(TAG, "RecyclerView setup complete");
    }

    private void setupPagination() {
        binding.btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadFields(currentPage);
                Log.d(TAG, "Loading page " + currentPage);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadFields(currentPage);
                Log.d(TAG, "Loading page " + currentPage);
            }
        });


        updatePaginationUI();
    }

    private void updatePaginationUI() {
        binding.tvPageInfo.setText("Page " + currentPage + " of " + totalPages);
        binding.btnPrevious.setEnabled(currentPage > 1);
        binding.btnNext.setEnabled(currentPage < totalPages);
    }

    private void observeCommonData() {
        // Observe errors
        homeViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.loadingIndicator.setVisibility(View.GONE);
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe pagination info
        homeViewModel.getPaginationLiveData().observe(getViewLifecycleOwner(), paginationInfo -> {
            if (paginationInfo != null) {
                totalPages = paginationInfo.getTotalPages();
                currentPage = paginationInfo.getCurrentPage();
                updatePaginationUI();
                Log.d(TAG, "Pagination updated: page " + currentPage + " of " + totalPages);
            }
        });
    }

    private void loadFields(int pageNumber) {
        Log.d(TAG, "loadFields called for page " + pageNumber);
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);

        // Fetch fields and observe the result
        homeViewModel.fetchFields(pageNumber).observe(getViewLifecycleOwner(), fields -> {
            binding.loadingIndicator.setVisibility(View.GONE);

            if (fields != null && !fields.isEmpty()) {
                Log.d(TAG, "Received " + fields.size() + " fields");
                binding.recyclerView.setVisibility(View.VISIBLE);
                adapter.updateFields(fields);
                binding.recyclerView.scrollToPosition(0);

                Toast.makeText(getContext(), "Loaded " + fields.size() + " fields", Toast.LENGTH_SHORT).show();
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                if (fields != null) {
                    Toast.makeText(getContext(), "No fields available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSearchAndFilter() {
        // Setup search with debounce
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove previous callback
                searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Create new search runnable
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    Log.d(TAG, "Search query: " + query);
                    homeViewModel.setSearchQuery(query);
                    currentPage = 1; // Reset to first page
                    loadFields(currentPage);
                };

                // Post delayed to debounce
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        // Setup filter button
        binding.btnFilter.setOnClickListener(v -> {
            toggleFilterVisibility();
        });

        // Setup apply filter button
        binding.btnApplyFilter.setOnClickListener(v -> {
            applyPriceFilter();
        });

        // Setup clear filter button
        binding.btnClearFilter.setOnClickListener(v -> {
            clearFilters();
        });
    }

    private void toggleFilterVisibility() {
        isFilterVisible = !isFilterVisible;
        binding.filterContainer.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        Log.d(TAG, "Filter visibility: " + (isFilterVisible ? "VISIBLE" : "GONE"));
    }

    private void applyPriceFilter() {
        String minPriceStr = binding.etMinPrice.getText().toString().trim();
        String maxPriceStr = binding.etMaxPrice.getText().toString().trim();

        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (!minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
            if (!maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }

            // Validate
            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                Toast.makeText(getContext(), "Giá tối thiểu phải nhỏ hơn giá tối đa", Toast.LENGTH_SHORT).show();
                return;
            }

            homeViewModel.setPriceFilter(minPrice, maxPrice);
            currentPage = 1; // Reset to first page
            loadFields(currentPage);

            Toast.makeText(getContext(), "Đã áp dụng bộ lọc giá", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Price filter applied: min=" + minPrice + ", max=" + maxPrice);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Vui lòng nhập giá hợp lệ", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid price format", e);
        }
    }

    private void clearFilters() {
        binding.etMinPrice.setText("");
        binding.etMaxPrice.setText("");
        binding.etSearch.setText("");

        homeViewModel.clearFilters();
        currentPage = 1; // Reset to first page
        loadFields(currentPage);

        Toast.makeText(getContext(), "Đã xóa tất cả bộ lọc", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "All filters cleared");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        Log.d(TAG, "RecyclerView state - Adapter: " + binding.recyclerView.getAdapter() +
                   ", ItemCount: " + (adapter != null ? adapter.getItemCount() : "null") +
                   ", ChildCount: " + binding.recyclerView.getChildCount() +
                   ", Visibility: " + binding.recyclerView.getVisibility());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

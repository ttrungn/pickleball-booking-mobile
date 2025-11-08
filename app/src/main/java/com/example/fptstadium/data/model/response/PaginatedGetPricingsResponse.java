package com.example.fptstadium.data.model.response;

import com.example.fptstadium.data.model.Pricing;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaginatedGetPricingsResponse {

    @SerializedName("data")
    private PageData data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public PageData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static class PageData {
        @SerializedName("items")
        private List<Pricing> items;

        @SerializedName("pageNumber")
        private Integer pageNumber;

        @SerializedName("pageSize")
        private Integer pageSize;

        @SerializedName("totalCount")
        private Integer totalCount;

        @SerializedName("totalPages")
        private Integer totalPages;

        public List<Pricing> getItems() {
            return items;
        }

        public Integer getPageNumber() { return pageNumber; }
        public Integer getPageSize() { return pageSize; }
        public Integer getTotalCount() { return totalCount; }
        public Integer getTotalPages() { return totalPages; }
    }
}


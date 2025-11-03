package com.example.fptstadium.data.model;

public class PaginationInfo {
    private int currentPage;
    private int totalPages;
    private int totalCount;
    private boolean hasPreviousPage;
    private boolean hasNextPage;

    public PaginationInfo(int currentPage, int totalPages, int totalCount, boolean hasPreviousPage, boolean hasNextPage) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.hasPreviousPage = hasPreviousPage;
        this.hasNextPage = hasNextPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }
}

